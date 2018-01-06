package de.urkallinger.copymanager.files;

import static de.urkallinger.copymanager.config.Settings.OVERRIDE_FILES;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.TaskManager;
import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.data.Task;
import de.urkallinger.copymanager.data.TaskStatus;
import de.urkallinger.copymanager.utils.Str;

public class FileCopier implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileCopier.class);

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
		// TODO: Progressbar wieder einbauen
//		MainApp.getLogger().enableProgressBar(true, 0);
		for (FileListItem file : files) {
			final String name = file.getNewName().isEmpty() ? file.getName() : file.getNewName();
			final String newName = String.format("%s.%s", name, file.getExtension());

			File from = new File(file.getAbsolutPath());
			File to = new File(targetDir, newName);

			// Wenn Ziel nicht existiert -> kopieren
			// Wenn Ziel exisitiert und überschreiben aktiviert ist -> kopieren
			if (!to.exists() || (to.exists() && cfg.getSetting(OVERRIDE_FILES))) {
				Thread copyFilesThread = new Thread(getCpyFileRunnable(from, to));
				copyFilesThread.setDaemon(true);
				copyFilesThread.start();

				try {
					copyFilesThread.join();
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage());
				}

			} else {
				String msg = String.format(Str.get("FileCopier.override_file_exists"), to.getAbsolutePath());
				LOGGER.warn(msg);
			}
			step += stepSize;
			// TODO: Progressbar wieder einbauen
//			MainApp.getLogger().setProgress(step);
		}
		// TODO: Progressbar wieder einbauen
//		MainApp.getLogger().enableProgressBar(false, 2000);
	}

	private Runnable getCpyFileRunnable(File from, File to) {
		return () -> {
			String taskText = String.format(Str.get("FileCopier.copy_action"), from.getAbsolutePath(),
					to.getAbsolutePath());
			Task task = new Task(taskText, TaskStatus.IN_PROGRESS);
			TaskManager.addTask(task);
			try {
				FileUtils.copyFile(from, to);

				if(md5(from).equals(md5(to))) {
				    task.setStatus(TaskStatus.SUCCESS);
				    TaskManager.updateTask(task);
				} else {
				    task.setStatus(TaskStatus.FAILURE);
				    TaskManager.updateTask(task);
					LOGGER.error(Str.get("FileCopier.checksum_error"));
				}
			} catch (IOException | NoSuchAlgorithmException e) {
			    task.setStatus(TaskStatus.FAILURE);
			    TaskManager.updateTask(task);
				LOGGER.error(e.getMessage());
			}
		};
	}

	private static String md5(File f) throws NoSuchAlgorithmException, IOException {
		InputStream is = new FileInputStream(f);
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[8192];
			int read = 0;

			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}

			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);

			return bigInt.toString(16);
		} finally {
			try { is.close(); } catch (Exception e) {}
		}
	}
}
