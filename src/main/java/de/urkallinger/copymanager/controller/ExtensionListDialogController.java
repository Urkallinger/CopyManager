package de.urkallinger.copymanager.controller;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import de.urkallinger.copymanager.model.ExtensionListItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ExtensionListDialogController extends UIController {

	@FXML
	private Label lblDirPath = new Label();
	@FXML
	private TableView<ExtensionListItem> table = new TableView<>();
	@FXML
	private TableColumn<ExtensionListItem, Boolean> chbCol = new TableColumn<>();
	@FXML
	private TableColumn<ExtensionListItem, String> extensionCol = new TableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleOk() {
		
	}
	
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void addListItems(List<File> fileList) {
		fileList.forEach(f -> {
			ExtensionListItem eli  = new ExtensionListItem();
			eli.setExtension(FilenameUtils.getExtension(f.getName()));
			table.getItems().add(eli);
		});
	}
}
