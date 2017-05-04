package de.urkallinger.copymanager.callables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.utils.Str;
import javafx.application.Platform;

public class FileReader implements Runnable {

	private final File rootDir;
	private final ParamCallback<List<FileListItem>> callback;

	public FileReader(File rootDir, ParamCallback<List<FileListItem>> callback) {
		this.rootDir = rootDir;
		this.callback = callback;
	}

	private FutureTask<Integer> getFileExtInfoTask() {
		return new FutureTask<>(() -> {
			String action = String.format(Str.get("FileReader.read_action"), rootDir);
			return MainApp.getLogger().action(action, true);
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
			MainApp.getLogger().setDone(extReadInfo.get());
		} catch (InterruptedException | ExecutionException e) {
			String error = String.format(Str.get("FileReader.read_err"), rootDir);
			MainApp.getLogger().error(error);
			MainApp.getLogger().error(e.getMessage());
		}
		callback.call(items);
	}
}
