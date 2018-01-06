package de.urkallinger.copymanager.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.SettingsDialogController;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialog.class);

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
			scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());

			addShortCuts(scene, stage);
			stage.setResizable(false);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			// TODO: Übersetzung
			LOGGER.error(Str.get("Fehler beim Dialog laden."));
		}
	}

	private void addShortCuts(final Scene scene, final Stage stage) {
		scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
			case ESCAPE:
				stage.close();
				break;

			default:
				break;
			}
		});
	}
}
