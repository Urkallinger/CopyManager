package de.urkallinger.copymanager.controller;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.urkallinger.copymanager.Config;
import de.urkallinger.copymanager.ParamCallback;
import de.urkallinger.copymanager.dialogs.ExtensionListDialog;
import de.urkallinger.copymanager.exceptions.FileReaderInProgressException;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Platform;
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
	public void handleOpen() {
		Optional<File> file = Optional.empty();

		Config cfg = Config.getInstance();
		cfg.loadConfig();
		File dir = new File(cfg.getLastSrcDir());
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		if(dir.exists()) directoryChooser.setInitialDirectory(dir);
		file = Optional.ofNullable(directoryChooser.showDialog(stage));
		file.ifPresent(currDir -> {
			ParamCallback<List<FileListItem>> cb = new ParamCallback<List<FileListItem>>() {
				@Override
				public void call(List<FileListItem> files) {
					Runnable runner = new Runnable() {
						public void run() {
							Set<String> extensions = new HashSet<>();
							files.forEach(fli -> extensions.add(fli.getExtension()));
							mainApp.cacheFileListItems(files);
							ExtensionListDialog dialog = new ExtensionListDialog(mainApp, logger);
							dialog.setParentStage(stage);
							dialog.setExtensions(extensions);
							dialog.setDir(currDir.getAbsolutePath());
							dialog.show();
						}
					};
					Platform.runLater(runner);
				}
			};
			
			cfg.setLastSrcDir(currDir.getAbsolutePath());
			cfg.saveConfig();
			
			try {
				mainApp.setCurrentDir(currDir);
				mainApp.readFiles(cb);
			} catch (FileReaderInProgressException e) {
				logger.error(e.getMessage());
			}
		});
	}

	@FXML
	public void handleCopy() {
		mainApp.copyFiles();
	}
	
	@FXML
	public void handleRefresh() {
		Optional<File> currDir = mainApp.getCurrentDir();
		if (currDir.isPresent()) {
			mainApp.updateFileCache();
			mainApp.clearFileList();
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
