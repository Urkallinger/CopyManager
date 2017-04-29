package de.urkallinger.copymanager.callables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Platform;

public class FileCopier implements Runnable {

	private final List<FileListItem> files;
	private final File targetDir;
	
	public FileCopier(List<FileListItem> files, File targetDir) {
		this.files = files;
		this.targetDir = targetDir;
	}
	
	@Override
	public void run() {
		files.forEach(fli -> {
			final String name = fli.getNewName().isEmpty() ? fli.getName() : fli.getNewName();
			final String newName = name + "." + fli.getExtension();

			File from = new File(fli.getAbsolutPath());
			File to = new File(targetDir, newName);

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
		});
	}

	private FutureTask<Integer> getCpyInfoTask(File from, File to) {
		return new FutureTask<>(new Callable<Integer>() {
		    @Override
		    public Integer call() throws Exception {
		    	return MainApp.getLogger()
		    			.action("-> from: " + from.getAbsolutePath() + "\n" +
		    					"-> to:   " + to.getAbsolutePath(),
		    					true);
		    }
		});
	}
	
	private FutureTask<Boolean> getCpyFileTask(File from, File to, FutureTask<Integer> cpyInfoTask) {
		return new FutureTask<>(new Callable<Boolean>() {
			public Boolean call() {
				Platform.runLater(cpyInfoTask);
				try {
					FileUtils.copyFile(from, to);
				} catch (IOException e) {
					MainApp.getLogger().error(e.getMessage());
					return false;
				}
				return true;
			}
		});
	}
}
