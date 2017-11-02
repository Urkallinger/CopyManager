package de.urkallinger.copymanager.files.filter;

import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.exceptions.CMException;

public interface FileNameFilter {
	/**
	 * Filters the filename of the given {@link FileListItem item} and returns the
	 * new filtered filename of the item.
	 * 
	 * @param item
	 *            file list item which should be filtered.
	 * @return new filename.
	 * @throws CMException
	 */
	String filter(FileListItem item) throws CMException;
}
