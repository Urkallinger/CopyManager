package de.urkallinger.copymanager.callables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.config.Settings;
import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.utils.Str;
import javafx.application.Platform;

public class FileCopier implements Runnable {

	private final List<FileListItem> files;
	private final File targetDir;
	private final Configuration cfg;

	public FileCopier(List<FileListItem> files, File targetDir) {
		this.files = files;
		this.targetDir = targetDir;
		this.cfg = ConfigurationManager.loadConfiguration();
	}

	@Override
	public void run() {
		double stepSize = 1.0 / files.size();
		double step = 0;
		MainApp.getLogger().enableProgressBar(true, 0);
		for (FileListItem file : files) {
			final String name = file.getNewName().isEmpty() ? file.getName() : file.getNewName();
			final String newName = name + "." + file.getExtension();

			File from = new File(file.getAbsolutPath());
			File to = new File(targetDir, newName);

			// Wenn Ziel nicht existiert -> kopieren
			// Wenn Ziel exisitiert und überschreiben aktiviert ist -> kopieren
			if (!to.exists() || (to.exists() && cfg.getSetting(Settings.OVERRIDE_FILES))) {
				FutureTask<Integer> cpyInfo = getCpyInfoTask(from, to);
				FutureTask<Boolean> copyFile = getCpyFileTask(from, to, cpyInfo);

				Thread copyFilesThread = new Thread(copyFile);
				copyFilesThread.start();

				try {
					copyFilesThread.join();
					if (copyFile.get()) {
						MainApp.getLogger().setDone(cpyInfo.get());
					} else {
						MainApp.getLogger().setFailed(cpyInfo.get());
					}
				} catch (InterruptedException e) {
					MainApp.getLogger().error(e.getMessage());
				} catch (ExecutionException e) {
					MainApp.getLogger().error(e.getMessage());
				}
			} else {
				String msg = String.format("Can not copy file. File already exists. (%s)", to.getAbsolutePath());
				MainApp.getLogger().warning(msg);
			}
			step += stepSize;
			MainApp.getLogger().setProgress(step);
		}
		MainApp.getLogger().enableProgressBar(false, 2000);
	}

	private FutureTask<Integer> getCpyInfoTask(File from, File to) {
		return new FutureTask<>(() -> {
			String action = String.format(Str.get("FileCopier.copy_action"), from.getAbsolutePath(),
					to.getAbsolutePath());
			return MainApp.getLogger().action(action, true);
		});
	}

	private FutureTask<Boolean> getCpyFileTask(File from, File to, FutureTask<Integer> cpyInfoTask) {
		return new FutureTask<>(() -> {
			try {
				Platform.runLater(cpyInfoTask);
				FileUtils.copyFile(from, to);
			} catch (IOException e) {
				MainApp.getLogger().error(e.getMessage());
				return false;
			}
			return true;
		});
	}
}
