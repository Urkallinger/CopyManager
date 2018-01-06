package de.urkallinger.copymanager.controller;

import com.google.common.eventbus.EventBus;

import de.urkallinger.copymanager.MainApp;
import javafx.application.Platform;

public abstract class UIController {

    protected EventBus globalEventBus;
	protected MainApp mainApp;

	public MainApp getMainApp() {
		return mainApp;
	}
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setGlobalEventBus(EventBus eventBus) {
	    this.globalEventBus = eventBus;
	    globalEventBus.register(this);
	}

	protected static void execOnFxAppThread(Runnable run) {
        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }
}
