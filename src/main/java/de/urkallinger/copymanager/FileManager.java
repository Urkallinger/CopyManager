package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Platform;

public class FileManager {

	private List<String> extensions;
	private ConsoleCallback console;

	public FileManager(final ConsoleCallback cc) {
		this.console = cc;
		this.extensions = new ArrayList<>();
	}

	private FutureTask<Integer> getCpyInfoTask(File from, File to) {
		return new FutureTask<>(new Callable<Integer>() {
		    @Override
		    public Integer call() throws Exception {
		    	return console.action("-> from: " + from.getAbsolutePath() + "\n" +
						         	  "-> to:   " + to.getAbsolutePath(),
						         	  true);
		    }
		});
	}
	
	private FutureTask<Boolean> getCpyFileRunnable(File from, File to, FutureTask<Integer> cpyInfoTask) {
		return new FutureTask<>(new Callable<Boolean>() {
			public Boolean call() {
				Platform.runLater(cpyInfoTask);
				try {
					FileUtils.copyFile(from, to);
				} catch (IOException e) {
					// TODO: Logging mit Fehlermeldung einrichten oder irgendwie die Fehlermeldung auf der Console ausgeben
					return false;
				}
				return true;
			}
		});
	}
	
	public List<String> getFileExtensions() {
		return extensions;
	}

	public List<File> getFileList(File rootDir) {
		Collection<File> files = FileUtils.listFiles(rootDir, extensions.toArray(new String[extensions.size()]), true);
		return new ArrayList<>(files);
	}

	public void copyFiles(final List<FileListItem> files, final  File targetDir) {
		Runnable cpy = new Runnable() {
			public void run() {
				files.forEach(fli -> {
					final String name = fli.getNewName().isEmpty() ? fli.getName() : fli.getNewName();
					final String newName = name + "." + fli.getExtension();
					
					File from = fli.getFile();
					File to = new File(targetDir, newName);
					
					FutureTask<Integer> cpyInfo = getCpyInfoTask(from, to);
					FutureTask<Boolean> copyFile = getCpyFileRunnable(from, to, cpyInfo);
					
					Thread copyFileThread = new Thread(copyFile);
					copyFileThread.start();
					
					try {
						copyFileThread.join();
						if(copyFile.get()) {
							console.setDone(cpyInfo.get());
						} else {
							console.setFailed(cpyInfo.get());
						}
						
					} catch (InterruptedException e) {
						// TODO: console.setFailed implementieren
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				});
			}
		};
		
		Thread copyThread = new Thread(cpy);
		copyThread.setDaemon(true);
		copyThread.start();
	}
}
