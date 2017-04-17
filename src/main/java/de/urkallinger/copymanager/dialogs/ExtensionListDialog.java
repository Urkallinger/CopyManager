package de.urkallinger.copymanager.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.callables.FileExtensionReader;
import de.urkallinger.copymanager.controller.ExtensionListDialogController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ExtensionListDialog {

	private final MainApp mainApp;
	private final LoggerCallback logger;
	private final File rootDir;

	private Stage stage;
	private BorderPane layout;
	private ExtensionListDialogController dialogController;

	public ExtensionListDialog(MainApp mainApp, LoggerCallback logger, File rootDir) {
		this.mainApp = mainApp;
		this.logger = logger;
		this.rootDir = rootDir;
	}
	
	public void show() {
		this.stage = new Stage();

		stage.setTitle("Extensions");
		prepareDialog();
	}

	private void prepareDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
			this.layout = (BorderPane) loader.load();
			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			
			Runnable runner = getDialogRunner();
			Thread t = new Thread(runner);
			t.setDaemon(true);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable getDialogRunner() {
		return new Runnable() {
			@Override
			public void run() {
				try {
					Callable<Set<String>> c = new FileExtensionReader(logger, rootDir);
					FutureTask<Set<String>> task = new FutureTask<Set<String>>(c);
					Thread reader = new Thread(task);
					
					reader.start();
					reader.join();

					Set<String> exts = task.get();
					Platform.runLater(showDialogRunner(exts));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
				
	}


	private Runnable showDialogRunner(final Set<String> exts) {
		return new Runnable() {
			@Override
			public void run() {
				dialogController.addListItems(exts);
				
				Scene scene = new Scene(layout);
				stage.setScene(scene);
				stage.showAndWait();
			}
		};
	}
}
