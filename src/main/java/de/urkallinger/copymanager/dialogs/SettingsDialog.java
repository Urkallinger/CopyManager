package de.urkallinger.copymanager.dialogs;

import java.io.IOException;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.SettingsDialogController;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsDialog {
	private final MainApp mainApp;
	
	private Stage parentStage;
	private SettingsDialogController dialogController;
	
	public SettingsDialog(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}
	
	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle(Str.get("SettingsDialogController.title"));
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/dialogs/SettingsDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);

			Scene scene = new Scene(layout);
			stage.setResizable(false);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			// TODO: Übersetzung
			MainApp.getLogger().error(Str.get("Fehler beim Dialog laden."));
		}
	}
}
