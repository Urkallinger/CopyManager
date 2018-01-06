package de.urkallinger.copymanager.ui.dialogs;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.ExtensionListDialogController;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExtensionListDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionListDialog.class);

	private final MainApp mainApp;

	private Stage parentStage;
	private Set<String> extensions;
	private ExtensionListDialogController dialogController;

	public ExtensionListDialog(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle(Str.get("keywords.extensions"));
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			dialogController.addListItems(extensions);

			Scene scene = new Scene(layout);
			scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());

			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			LOGGER.error(Str.get("ExtensionListDialog.init_err"));
			LOGGER.error(e.getMessage());
		}
	}
}
