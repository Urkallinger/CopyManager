package de.urkallinger.copymanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.urkallinger.copymanager.callables.FileCopier;
import de.urkallinger.copymanager.callables.FileReader;
import de.urkallinger.copymanager.model.FileListItem;

public class FileManager {

	private List<FileListItem> fileCache = new ArrayList<>();
	private List<String> extensions;
	private LoggerCallback logger;

	public FileManager(final LoggerCallback logger) {
		this.logger = logger;
		this.extensions = new ArrayList<>();
	}

	public List<String> getFileExtensions() {
		return extensions;
	}

	public void readFiles(File rootDir, ParamCallback<List<FileListItem>> callback) {
		Runnable reader = new FileReader(logger, rootDir, callback);
		Thread t = new Thread(reader);
		t.setDaemon(true);
		t.start();
	}

	public void copyFiles(final List<FileListItem> files, final File targetDir) {
		Runnable cpy = new FileCopier(files, targetDir, logger);
		Thread copyThread = new Thread(cpy);
		copyThread.setDaemon(true);
		copyThread.start();
	}
	
	public List<FileListItem> getFileCache() {
		return fileCache;
	}
	
	public List<FileListItem> getFiteredFileCache() {
		List<FileListItem> items = new ArrayList<>();
		fileCache.forEach(f -> {
			if(this.extensions.contains(f.getExtension())) {
				items.add(f);
			}
		});
		return items;
	}
	
	public void setFileCache(List<FileListItem> files) {
		this.fileCache = files;
	}
}
