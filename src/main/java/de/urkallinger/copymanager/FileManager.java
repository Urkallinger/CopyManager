package de.urkallinger.copymanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.urkallinger.copymanager.callables.FileCopier;
import de.urkallinger.copymanager.model.FileListItem;

public class FileManager {

	private List<String> extensions;
	private LoggerCallback logger;

	public FileManager(final LoggerCallback logger) {
		this.logger = logger;
		this.extensions = new ArrayList<>();
	}

	
	public List<String> getFileExtensions() {
		return extensions;
	}

	public List<File> getFiles(File rootDir) {
		Collection<File> files = FileUtils.listFiles(rootDir, extensions.toArray(new String[extensions.size()]), true);
		return new ArrayList<>(files);
	}

	public void copyFiles(final List<FileListItem> files, final  File targetDir) {
		Runnable cpy = new FileCopier(files, targetDir, logger); 
		Thread copyThread = new Thread(cpy);
		copyThread.setDaemon(true);
		copyThread.start();
	}
}
