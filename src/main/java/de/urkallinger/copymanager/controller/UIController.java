package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.MainApp;

public abstract class UIController {
	
	protected LoggerCallback logger;
	protected MainApp mainApp;
	
	public void setLogger(LoggerCallback logger) {
		this.logger = logger;
	}
	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
