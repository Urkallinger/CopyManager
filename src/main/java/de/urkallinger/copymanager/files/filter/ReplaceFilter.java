package de.urkallinger.copymanager.files.filter;

import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.exceptions.CMException;

public class ReplaceFilter implements FileNameFilter {

	private final String oldValue;
	private final String newValue;
	
	public ReplaceFilter(String oldValue, String newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	@Override
	public String filter(FileListItem item) throws CMException {
		return item.getNewName().replaceAll(oldValue, newValue);
	}

}
