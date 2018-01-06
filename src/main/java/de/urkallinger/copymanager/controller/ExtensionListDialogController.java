package de.urkallinger.copymanager.controller;

import java.util.Set;

import de.urkallinger.copymanager.data.ExtensionListItem;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExtensionListDialogController extends UIController {

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
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setOnKeyPressed(event -> handleTableKeyEvent(event));
		table.setOnMouseClicked(event -> handleMouseCllick(event));

		chbCol.setCellValueFactory(cellData -> cellData.getValue().chbProperty().asObject());
		extCol.setCellValueFactory(cellData -> cellData.getValue().extensionProperty());

		chbCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(Integer idx) {
				ExtensionListItem item = table.getItems().get(idx);
				return item.chbProperty();
			}
		}));
	}

	private void handleMouseCllick(MouseEvent event) {
		table.getSelectionModel().getSelectedItems().forEach(item -> {
			item.setChecked(!item.isChecked());
		});
		event.consume();
	}

	private void handleTableKeyEvent(KeyEvent event) {
		switch (event.getCode()) {
		case SPACE:
			table.getSelectionModel().getSelectedItems().forEach(item -> {
				item.setChecked(!item.isChecked());
			});
			break;

		case ENTER:
			handleOk();
			break;

		case ESCAPE:
			handleCancel();
			break;

		default:
			break;
		}
	}

	@FXML
	private void handleOk() {
		table.getItems().stream()
			.filter(item -> item.isChecked())
			.forEach(item -> mainApp.addFileExtension(item.getExtension()));

		mainApp.clearFileList();
		mainApp.updateFileList();

		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleCancel() {
		mainApp.clearFileList();
		mainApp.updateFileList();

		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void addListItems(Set<String> extensions) {
		extensions.forEach(ext -> {
			ExtensionListItem eli = new ExtensionListItem(ext);
			table.getItems().add(eli);
		});
		requestFocus();
	}

	private void requestFocus() {
	    execOnFxAppThread(() -> {
			table.requestFocus();
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
		});
	}
}
