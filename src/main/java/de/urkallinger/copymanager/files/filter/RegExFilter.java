package de.urkallinger.copymanager.files.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FilenameUtils;

import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.exceptions.CMException;
import de.urkallinger.copymanager.utils.Str;

public class RegExFilter implements FileNameFilter {

	private final Pattern templPattern = Pattern.compile("#(\\d+)");
	private final Pattern folderPattern = Pattern.compile("#f(\\d+)");
	private final Pattern userPattern;
	private final String template;
	
	public RegExFilter(final String pattern, final String template) throws CMException {
		try {
			this.userPattern = Pattern.compile(pattern);
		} catch (PatternSyntaxException e) {
			throw new CMException(Str.get("OptionPanelController.pattern_compile_err"));
		}
		this.template = template;
	}
	
	private String replaceUserWildCard(String nn, FileListItem item, Matcher m) throws CMException {
		Matcher mx = templPattern.matcher(nn);
		int num = -1;
		try {
			while (mx.find()) {
				num = Integer.valueOf(mx.group(1));
				nn = nn.replace("#" + num, m.group(num).trim());
			}
		} catch (IndexOutOfBoundsException e) {
			String msg = String.format(Str.get("FileNameBuilder.ioob"), num, item.getName());
			throw new CMException(msg);
		}
		return nn;
	}

	private String replaceFolderWildCard(String nn, FileListItem item) throws CMException {
		Matcher mx = folderPattern.matcher(nn);
		String path = FilenameUtils.separatorsToWindows(item.getAbsolutPath());
		String[] split = path.split("\\\\");
		int num = -1;

		try {
			while (mx.find()) {
				num = Integer.valueOf(mx.group(1));
				nn = nn.replace("#f" + num, split[split.length - num - 1].trim());
			}
		} catch (IndexOutOfBoundsException e) {
			String msg = String.format(Str.get("FileNameBuilder.dir_ioob"), num, item.getName());
			throw new CMException(msg);
		}
		return nn;
	}
	
	@Override
	public String filter(FileListItem item) throws CMException {
		String nn = template; // new name

		Matcher m = userPattern.matcher(item.getNewName());
		if (m.matches()) {
			nn = replaceUserWildCard(nn, item, m);
			nn = replaceFolderWildCard(nn, item);
		} else {
			String msg = String.format(Str.get("FileNameBuilder.no_pattern_match"), item.getName());
			throw new CMException(msg);
		}

		return nn;
	}
}
