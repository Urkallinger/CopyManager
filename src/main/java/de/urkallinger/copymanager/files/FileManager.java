package de.urkallinger.copymanager.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.exceptions.FileCopierInProgressException;
import de.urkallinger.copymanager.exceptions.FileReaderInProgressException;
import de.urkallinger.copymanager.utils.Str;

public class FileManager {

	private List<FileListItem> fileCache = new ArrayList<>();
	private List<String> extensions;
	private Thread fileReader = new Thread();
	private Thread copyThread = new Thread();

	public FileManager() {
		this.extensions = new ArrayList<>();
	}

	public List<String> getFileExtensions() {
		return extensions;
	}

	public void readFiles(File rootDir, ParamCallback<List<FileListItem>> callback) 
			throws FileReaderInProgressException {
		if(!fileReader.isAlive()) {
			Runnable reader = new FileReader(rootDir, callback);
			fileReader = new Thread(reader, "FileReader");
			fileReader.setDaemon(true);
			fileReader.start();
		} else {
			throw new FileReaderInProgressException(Str.get("FileManager.thread_already_reading_err"));
		}
	}
	
	public boolean isReadingFiles() {
		return fileReader.isAlive();
	}

	public void copyFiles(final List<FileListItem> files, final File targetDir)
			throws FileCopierInProgressException {
		if(!copyThread.isAlive()) {
			Runnable cpy = new FileCopier(files, targetDir);
			copyThread = new Thread(cpy, "FileCopier");
			copyThread.setDaemon(true);
			copyThread.start();
		} else {
			throw new FileCopierInProgressException(Str.get("FileManager.thread_already_copying_err"));
		}
	}
	
	public List<FileListItem> getFileCache() {
		return fileCache;
	}
	
	public List<FileListItem> getFiteredFileCache() {
		return fileCache.stream()
				 		.filter(item -> extensions.contains(item.getExtension()))
				 		.collect(Collectors.toList());
	}
	
	public void setFileCache(List<FileListItem> files) {
		this.fileCache = files;
	}
}
