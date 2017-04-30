package de.urkallinger.copymanager.dialogs;

import java.io.IOException;
import java.util.Set;

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

	private final MainApp mainApp;

	private Stage parentStage;
	private Set<String> extensions;
	private String dir = "";
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
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle("Extensions");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			dialogController = loader.getController();
			dialogController.setMainApp(mainApp);
			dialogController.addListItems(extensions);
			dialogController.setDir(dir);

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			MainApp.getLogger().error(Str.get("ExtensionListDialog.init_err"));
			MainApp.getLogger().error(e.getMessage());
		}
	}
}
