package de.urkallinger.copymanager.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import de.urkallinger.copymanager.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class RootLayoutController {

	@FXML
	private Button btnOpen = new Button();
	@FXML
	private Button btnCopy = new Button();
	@FXML
	private Button btnRefresh = new Button();
	@FXML
	private Button btnCheckAll = new Button();
	@FXML
	private Button btnUncheckAll = new Button();
	@FXML
	private AnchorPane leftArea = new AnchorPane();
	@FXML
	private AnchorPane rightArea = new AnchorPane();
	@FXML
	private AnchorPane bottomArea = new AnchorPane();
	
	
	private MainApp mainApp;
	private Stage stage;
	
	public RootLayoutController() {
	}
	
	@FXML
	private void initialize() {
		try {
			Image imgRes = new Image(getClass().getResourceAsStream("/images/folder.png"));
			btnOpen.setGraphic(new ImageView(imgRes));

			imgRes = new Image(getClass().getResourceAsStream("/images/copy.png"));
			btnCopy.setGraphic(new ImageView(imgRes));
			
			imgRes = new Image(getClass().getResourceAsStream("/images/refresh.png"));
			btnRefresh.setGraphic(new ImageView(imgRes));
			
			imgRes = new Image(getClass().getResourceAsStream("/images/checkAll.png"));
			btnCheckAll.setGraphic(new ImageView(imgRes));
			
			imgRes = new Image(getClass().getResourceAsStream("/images/uncheckAll.png"));
			btnUncheckAll.setGraphic(new ImageView(imgRes));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleOpen() {
		Optional<File> file = Optional.empty();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		file = Optional.ofNullable(directoryChooser.showDialog(stage));
		file.ifPresent(currDir -> {
			mainApp.setCurrentDir(currDir);
			mainApp.clearFileList();
			mainApp.updateFileList();
			showExtensionDialog();
		});
	}

	@FXML
	private void handleCopy() {
		mainApp.copyFiles();
	}
	
	@FXML
	private void handleRefresh() {
		mainApp.updateFileList();
		Optional<File> currDir = mainApp.getCurrentDir();
		if (currDir.isPresent()) mainApp.info("refresh files from: " + currDir.get());
		else                     mainApp.warning("cannot refresh. no directory selected.");
		
	}
	
	@FXML
	private void handleCheckAll() {
		mainApp.setAllChecked(true);
	}
	
	@FXML
	private void handleUncheckAll() {
		mainApp.setAllChecked(false);
	}
	
	private void showExtensionDialog() {
        try {
        	Stage stage = new Stage();
        	stage.setTitle("Extensions");
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
            BorderPane layout = (BorderPane) loader.load();

            ExtensionListDialogController controller = loader.getController();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(layout);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void setDefaultAnchors(Node node) {
		AnchorPane.setBottomAnchor(node, 5.0);
		AnchorPane.setLeftAnchor(node, 5.0);
		AnchorPane.setRightAnchor(node, 5.0);
		AnchorPane.setTopAnchor(node, 5.0);
	}
	
	public void setLeftArea(Node node) {
		leftArea.getChildren().add(node);
		setDefaultAnchors(node);
	}
	
	public void setRightArea(Node node) {
		rightArea.getChildren().add(node);
		setDefaultAnchors(node);
	}
	
	public void setBottomArea(Node node) {
		bottomArea.getChildren().add(node);
		setDefaultAnchors(node);
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
