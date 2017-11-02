package de.urkallinger.copymanager.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.urkallinger.copymanager.data.RenameConfigItem;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
	
	private String lastSrcDir;
	private String lastDestDir;
	private List<RenameConfigItem> renameConfigurations;
	private String locale;
	private Map<Settings, Boolean> settings;
	
	public Configuration() {
		this.lastSrcDir = "";
		this.lastDestDir = "";
		this.renameConfigurations = new ArrayList<>();
		this.locale = Locale.ENGLISH.toString();
		this.settings = new HashMap<>();
		setDefaultSettings();
	}
	
	private void setDefaultSettings() {
		settings.put(Settings.OVERRIDE_FILES, true);
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

	public List<RenameConfigItem> getRenameConfigurations() {
		return renameConfigurations;
	}

	public void addRenameConfiguration(String name, String pattern, String template) {
		this.renameConfigurations.add(new RenameConfigItem(name, pattern, template));
	}

	public Locale getLocale() {
		return Locale.forLanguageTag(locale);
	}
	
	public void SetLocale(String locale) {
		this.locale = locale;
	}

	public void addSetting(Settings setting, boolean value) {
		this.settings.put(setting, value);
	}

	public Map<Settings, Boolean> getSettings() {
		return settings;
	}
	
	public boolean getSetting(Settings setting) {
		return settings.get(setting);
	}
}
