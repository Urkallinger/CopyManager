package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.MainApp;

public abstract class UIController {
	
	protected MainApp mainApp;
	
	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
