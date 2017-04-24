package de.urkallinger.copymanager.dialogs;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.PatternDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PatternDialog {
	private final MainApp mainApp;
	private final LoggerCallback logger;

	private Stage parentStage;
	private Map<String, String> pattern;
	private PatternDialogController dialogController;
	
	public PatternDialog(MainApp mainApp, LoggerCallback logger) {
		this.mainApp = mainApp;
		this.logger = logger;
	}
	
	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}
	
	public void setPattern(Map<String, String> pattern) {
		this.pattern = pattern;
	}
	
	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle("Pattern");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/dialogs/PatternDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			dialogController.addListItems(pattern);

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			logger.error("an error occured while preparing the pattern dialog!");
			logger.error(e.getMessage());
		}
	}
	
	public Optional<String> getSelectedPattern() {
		if(dialogController != null) {
			return dialogController.getSelectedPattern();
		} else {
			return Optional.empty();
		}
	}
}
