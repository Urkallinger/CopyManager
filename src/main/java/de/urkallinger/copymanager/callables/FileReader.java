package de.urkallinger.copymanager.callables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.utils.Str;

public class FileReader implements Runnable {

	private final File rootDir;
	private final ParamCallback<List<FileListItem>> callback;

	public FileReader(File rootDir, ParamCallback<List<FileListItem>> callback) {
		this.rootDir = rootDir;
		this.callback = callback;
	}

	@Override
	public void run() {
		String action = String.format(Str.get("FileReader.read_action"), rootDir);
		int idx = MainApp.getLogger().action(action, true);
		
		Collection<File> files = FileUtils.listFiles(rootDir, null, true);
		List<FileListItem> items = new ArrayList<>(files.size());
		files.forEach(f -> items.add(new FileListItem(f)));
		
		MainApp.getLogger().setDone(idx);
		callback.call(items);
	}
}
