package de.urkallinger.copymanager.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.urkallinger.copymanager.model.FileListItem;
import de.urkallinger.copymanager.model.FileListItem.SizeObj;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class FileOverviewController {
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

	private final Pattern templPattern = Pattern.compile("#(\\d+)");

	@FXML
	private void initialize() {

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
					FileListItem item = table.getSelectionModel().getSelectedItem();
					item.setChecked(!item.isChecked());
				}
			}
		});

	}

	public void addListItems(List<File> files) {
		files.forEach(f -> {
			FileListItem fli = new FileListItem(f);
			table.getItems().add(fli);
		});
	}

	public void updateNewFileName(final Pattern pattern, final String template) {
		if(template.isEmpty()) return;
		
		table.getItems().forEach(item -> {
			String nn = template;
			Matcher m = pattern.matcher(item.getName());
			if (m.matches()) {
				Matcher mx = templPattern.matcher(nn);
				while (mx.find()) {
					int num = Integer.valueOf(mx.group(1));
					nn = nn.replace("#" + num, m.group(num));
				}
				item.setNewName(nn);
			}
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
