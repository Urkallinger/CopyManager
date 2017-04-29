package de.urkallinger.copymanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.model.FileListItem.SizeObj;
import de.urkallinger.copymanager.utils.FileNameBuilder;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class FileOverviewController extends UIController {
	@FXML
	private TableView<FileListItem> table = new TableView<>();
	@FXML
	private TableColumn<FileListItem, Boolean> chbCol = new TableColumn<>();
	@FXML
	private TableColumn<FileListItem, String> nameCol = new TableColumn<>();
	@FXML
	private TableColumn<FileListItem, String> newNameCol = new TableColumn<>();
	@FXML
	private TableColumn<FileListItem, String> extCol = new TableColumn<>();
	@FXML
	private TableColumn<FileListItem, SizeObj> sizeCol = new TableColumn<>();

	@FXML
	private void initialize() {
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		chbCol.setCellValueFactory(cellData -> cellData.getValue().chbProperty().asObject());
		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		newNameCol.setCellValueFactory(cellData -> cellData.getValue().newNameProperty());
		extCol.setCellValueFactory(cellData -> cellData.getValue().extensionProperty());
		sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());

		chbCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
		nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
		newNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
		extCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
		sizeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

		chbCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(Integer idx) {
				FileListItem item = table.getItems().get(idx);
				return item.chbProperty();
			}
		}));

		sizeCol.setCellFactory(new Callback<TableColumn<FileListItem, SizeObj>, TableCell<FileListItem, SizeObj>>() {
			@Override
			public TableCell<FileListItem, SizeObj> call(TableColumn<FileListItem, SizeObj> cell) {
				return new TableCell<FileListItem, SizeObj>() {
					@Override
					protected void updateItem(SizeObj item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							setText(item.getText());
						}
					}
				};
			}
		});

		table.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.SPACE) {
					table.getSelectionModel().getSelectedItems().forEach(item -> {
						item.setChecked(!item.isChecked());
					});
				}
			}
		});

	}

	public void addListItems(List<FileListItem> items) {
		table.getItems().addAll(items);
	}

	public void updateNewFileName(final Pattern pattern, final String template) {
		if(template.isEmpty()) return;
		FileNameBuilder nameBuilder = new FileNameBuilder(pattern, template);
		
		table.getItems().forEach(item -> {
			if(!item.isChecked()) return; // continue
			item.setNewName(nameBuilder.buildFileName(item));
		});
	}

	public void clearNewFileName() {
		table.getItems().forEach(item -> item.setNewName(""));
	}
	
	public List<FileListItem> getCheckedFiles() {
		List<FileListItem> flis = new ArrayList<>();
		table.getItems().forEach(fli -> {
			if(fli.isChecked()) {
				flis.add(fli);
			}
		});
		
		return flis;
	}

	public void clearFileList() {
		table.getItems().clear();
		table.refresh();
	}

	public void setAllChecked(boolean checked) {
		table.getItems().forEach(item -> {
			item.setChecked(checked);
		});
	}
}
