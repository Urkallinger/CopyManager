package de.urkallinger.copymanager.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import de.urkallinger.copymanager.Configuration;
import de.urkallinger.copymanager.ConfigurationManager;

public class Str {
	
	public static ResourceBundle getBundle() {
		Configuration cfg = ConfigurationManager.loadConfiguration();
		Locale locale = cfg.getLocale();
		return ResourceBundle.getBundle("bundles.CopyManager", locale);
	}
	
	public static String get(String key) {
		return getBundle().getString(key);
	}
}
