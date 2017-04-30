package de.urkallinger.copymanager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {

	private String lastSrcDir;
	private String lastDestDir;
	private Map<String, String> pattern;
	private String locale;
	
	public Configuration() {
		this.lastSrcDir = "";
		this.lastDestDir = "";
		this.pattern = new HashMap<>();
		this.locale = Locale.ENGLISH.toString();
	}

	public String getLastSrcDir() {
		return lastSrcDir;
	}

	public void setLastSrcDir(String lastSrcDir) {
		this.lastSrcDir = lastSrcDir;
	}

	public String getLastDestDir() {
		return lastDestDir;
	}

	public void setLastDestDir(String lastDestDir) {
		this.lastDestDir = lastDestDir;
	}

	public Map<String, String> getPattern() {
		return pattern;
	}

	public void addPattern(String name, String pattern) {
		this.pattern.put(name, pattern);
	}

	public Locale getLocale() {
		return Locale.forLanguageTag(locale);
	}
	
	public void SetLocale(String locale) {
		this.locale = locale;
	}
}
