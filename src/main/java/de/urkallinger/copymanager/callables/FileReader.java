package de.urkallinger.copymanager.callables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.LoggerCallback;
import javafx.application.Platform;
import javafx.util.Callback;

public class FileReader implements Runnable {

	private final File rootDir;
	private final LoggerCallback logger;
	private final String[] extensions;
	private final Callback<List<File>, Object> callback;

	public FileReader(LoggerCallback logger, File rootDir, List<String> extensions, Callback<List<File>, Object> callback) {
		this.logger = logger;
		this.rootDir = rootDir;
		this.extensions = extensions.toArray(new String[extensions.size()]);
		this.callback = callback;
	}

	@Override
	public void run() {
		FutureTask<Integer> extReadInfo = getFileExtInfoTask();
		Platform.runLater(extReadInfo);
		Collection<File> files = FileUtils.listFiles(rootDir, extensions, true);

		try {
			logger.setDone(extReadInfo.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		callback.call(new ArrayList<>(files));
	}

	private FutureTask<Integer> getFileExtInfoTask() {
		return new FutureTask<>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return logger.action("read files", true);
			}
		});
	}
}
