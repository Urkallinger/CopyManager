package de.urkallinger.copymanager.ui.dialogs;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.RenameConfigsDialogController;
import de.urkallinger.copymanager.data.RenameConfigItem;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RenameConfigsDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameConfigsDialog.class);

	private final MainApp mainApp;

	private Stage parentStage;
	private List<RenameConfigItem> renameConfigs;
	private RenameConfigsDialogController dialogController;

	public RenameConfigsDialog(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}

	public void setRenameConfigs(List<RenameConfigItem> renameConfigs) {
		this.renameConfigs = renameConfigs;
	}

	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle(Str.get("RenameConfigsDialog.title"));
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/dialogs/RenameConfigsDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			dialogController.addListItems(renameConfigs);

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			LOGGER.error(Str.get("RenameConfigsDialog.init_err"));
			LOGGER.error(e.getMessage());
		}
	}

	public Optional<RenameConfigItem> getSelectedRenameConfig() {
		if(dialogController != null) {
			return dialogController.getSelectedRenameConfig();
		} else {
			return Optional.empty();
		}
	}
}
