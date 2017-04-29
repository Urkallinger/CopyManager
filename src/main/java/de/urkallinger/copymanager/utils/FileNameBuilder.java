package de.urkallinger.copymanager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.model.FileListItem;

public class FileNameBuilder {

	private final Pattern templPattern = Pattern.compile("#(\\d+)");
	private final Pattern folderPattern = Pattern.compile("#f(\\d+)");
	private final Pattern userPattern;
	private final String template;

	public FileNameBuilder(final Pattern pattern, final String template) {
		this.userPattern = pattern;
		this.template = template;
	}

	public String buildFileName(FileListItem item) {
		String nn = template; // new name

		Matcher m = userPattern.matcher(item.getName());
		if (m.matches()) {
			nn = replaceUserWildCard(nn, item, m);
			nn = replaceFolderWildCard(nn, item);
			item.setNewName(nn);
		} else {
			MainApp.getLogger().warning("no pattern match for \"" + item.getName() + "\"");
		}

		return nn;
	}

	private String replaceUserWildCard(String nn, FileListItem item, Matcher m) {
		Matcher mx = templPattern.matcher(nn);
		int num = -1;
		try {
			while (mx.find()) {
				num = Integer.valueOf(mx.group(1));
				nn = nn.replace("#" + num, m.group(num).trim());
			}
		} catch (IndexOutOfBoundsException e) {
			MainApp.getLogger().error("error with #" + num + ". index out of bounds. (" + item.getName() + ")");
		}
		return nn;
	}

	private String replaceFolderWildCard(String nn, FileListItem item) {
		Matcher mx = folderPattern.matcher(nn);
		String path = FilenameUtils.separatorsToWindows(item.getAbsolutPath());
		String[] split = path.split("\\\\");
		int num = -1;

		try {
			while (mx.find()) {
				num = Integer.valueOf(mx.group(1));
				nn = nn.replace("#f" + num, split[split.length - num].trim());
			}
		} catch (IndexOutOfBoundsException e) {
			MainApp.getLogger().error("error with #f" + num + ". index out of bounds. (" + item.getName() + ")");
		}
		return nn;
	}
}
