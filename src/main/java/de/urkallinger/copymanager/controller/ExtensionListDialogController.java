package de.urkallinger.copymanager.controller;

import java.util.Set;

import de.urkallinger.copymanager.model.ExtensionListItem;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExtensionListDialogController extends UIController {

	@FXML
	private Label lblDirPath = new Label();
	@FXML
	private TableView<ExtensionListItem> table = new TableView<>();
	@FXML
	private TableColumn<ExtensionListItem, Boolean> chbCol = new TableColumn<>();
	@FXML
	private TableColumn<ExtensionListItem, String> extCol = new TableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();
	
	@FXML
	private void initialize() {
		chbCol.setCellValueFactory(cellData -> cellData.getValue().chbProperty().asObject());
		extCol.setCellValueFactory(cellData -> cellData.getValue().extensionProperty());
		
		chbCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(Integer idx) {
				ExtensionListItem item = table.getItems().get(idx);
				return item.chbProperty();
			}
		}));
		
		table.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.SPACE) {
					ExtensionListItem item = table.getSelectionModel().getSelectedItem();
					item.setChecked(!item.isChecked());
				}
			}
		});
	}
	
	@FXML
	private void handleOk() {
		table.getItems().forEach(eli -> {
			if(eli.isChecked()) {
				mainApp.addFileExtension(eli.getExtension());
			}
		});
		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void addListItems(Set<String> extensions) {
		extensions.forEach(ext -> {
			ExtensionListItem eli  = new ExtensionListItem(ext, false);
			table.getItems().add(eli);
		});
	}
}
