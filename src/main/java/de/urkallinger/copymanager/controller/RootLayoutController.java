package de.urkallinger.copymanager.controller;

import java.io.File;
import java.util.Optional;

import de.urkallinger.copymanager.dialogs.ExtensionListDialog;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class RootLayoutController extends UIController {

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
			ExtensionListDialog dialog = new ExtensionListDialog(mainApp, logger, currDir);
			dialog.show();
		});
	}

	@FXML
	private void handleCopy() {
		mainApp.copyFiles();
	}
	
	@FXML
	private void handleRefresh() {
		Optional<File> currDir = mainApp.getCurrentDir();
		if (currDir.isPresent()) {
			logger.info("refresh files from: " + currDir.get());
			mainApp.updateFileList();
		}
		else {
			logger.warning("cannot refresh. no directory selected.");
		}
		
	}
	
	@FXML
	private void handleCheckAll() {
		mainApp.setAllChecked(true);
	}
	
	@FXML
	private void handleUncheckAll() {
		mainApp.setAllChecked(false);
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
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
