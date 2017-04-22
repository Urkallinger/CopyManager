package de.urkallinger.copymanager.dialogs;

import java.io.IOException;
import java.util.Set;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.ExtensionListDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExtensionListDialog {

	private final MainApp mainApp;
	private final LoggerCallback logger;

	private Stage stage;
	private BorderPane layout;
	private ExtensionListDialogController dialogController;

	public ExtensionListDialog(MainApp mainApp, LoggerCallback logger) {
		this.mainApp = mainApp;
		this.logger = logger;
	}

	public void show(Stage parentStage, Set<String> extensions) {
		try {
			this.stage = new Stage();
			stage.setTitle("Extensions");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
			this.layout = (BorderPane) loader.load();
			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			dialogController.addListItems(extensions);

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			logger.error("an error occured while preparing the extension dialog!");
			logger.error(e.getMessage());
		}
	}
}
