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
import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Platform;

public class FileReader implements Runnable {

	private final File rootDir;
	private final LoggerCallback logger;
	private final ParamCallback<List<FileListItem>> callback;

	public FileReader(LoggerCallback logger, File rootDir, ParamCallback<List<FileListItem>> callback) {
		this.logger = logger;
		this.rootDir = rootDir;
		this.callback = callback;
	}

	private FutureTask<Integer> getFileExtInfoTask() {
		return new FutureTask<>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return logger.action("read files", true);
			}
		});
	}

	@Override
	public void run() {
		FutureTask<Integer> extReadInfo = getFileExtInfoTask();
		Platform.runLater(extReadInfo);
		Collection<File> files = FileUtils.listFiles(rootDir, null, true);
		List<FileListItem> items = new ArrayList<>(files.size());
		files.forEach(f -> items.add(new FileListItem(f)));
		try {
			logger.setDone(extReadInfo.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		callback.call(items);
	}
}
