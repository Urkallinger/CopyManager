package de.urkallinger.copymanager.callables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Platform;

public class FileCopier implements Runnable {

	private final List<FileListItem> files;
	private final File targetDir;
	private final LoggerCallback logger;
	
	public FileCopier(List<FileListItem> files, File targetDir, LoggerCallback logger) {
		this.files = files;
		this.targetDir = targetDir;
		this.logger = logger;
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
					logger.setDone(cpyInfo.get());
				} else {
					logger.setFailed(cpyInfo.get());
				}

			} catch (InterruptedException e) {
				// TODO SLF4J Logging implementieren
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO SLF4J Logging implementieren
				e.printStackTrace();
			}
		});
	}

	private FutureTask<Integer> getCpyInfoTask(File from, File to) {
		return new FutureTask<>(new Callable<Integer>() {
		    @Override
		    public Integer call() throws Exception {
		    	return logger.action("-> from: " + from.getAbsolutePath() + "\n" +
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
					// TODO SLF4J Logging implementieren
					return false;
				}
				return true;
			}
		});
	}
}
