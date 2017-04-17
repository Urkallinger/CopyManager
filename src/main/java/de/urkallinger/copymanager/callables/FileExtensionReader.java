package de.urkallinger.copymanager.callables;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.urkallinger.copymanager.LoggerCallback;
import javafx.application.Platform;

public class FileExtensionReader implements Callable<Set<String>> {

	private final File rootDir;
	private final LoggerCallback logger;

	public FileExtensionReader(LoggerCallback logger, File rootDir) {
		this.logger = logger;
		this.rootDir = rootDir;
	}

	@Override
	public Set<String> call() {
		final Set<String> exts = new HashSet<>();
		FutureTask<Integer> extReadInfo = getFileExtInfoTask();
		Platform.runLater(extReadInfo);

		Iterator<File> iter = FileUtils.iterateFiles(rootDir, null, true);
		while (iter.hasNext()) {
			File f = iter.next();
			exts.add(FilenameUtils.getExtension(f.getName()));
		}

		try {
			logger.setDone(extReadInfo.get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exts;
	}

	private FutureTask<Integer> getFileExtInfoTask() {
		return new FutureTask<>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return logger.action("read file extensions", true);
			}
		});
	}
}
