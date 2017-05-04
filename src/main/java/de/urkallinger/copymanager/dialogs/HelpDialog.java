package de.urkallinger.copymanager.dialogs;

import java.io.IOException;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.HelpDialogController;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelpDialog {
	private final MainApp mainApp;
	
	private Stage parentStage;
	private HelpDialogController dialogController;
	
	public HelpDialog(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}
	
	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle("Help");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/dialogs/HelpDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			MainApp.getLogger().error("An error occurred while loading the help dialog");
			MainApp.getLogger().error(e.getMessage());
		}
	}
}
