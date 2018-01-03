package de.urkallinger.copymanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.data.FileListItem.SizeObj;
import de.urkallinger.copymanager.exceptions.CMException;
import de.urkallinger.copymanager.files.filter.FileNameFilter;
import de.urkallinger.copymanager.utils.Str;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FileOverviewController.class);

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
	public void initialize() {
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setOnKeyPressed(event -> handleTableKeyEvent(event));

		chbCol.setCellValueFactory(cellData -> cellData.getValue().chbProperty().asObject());
		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		newNameCol.setCellValueFactory(cellData -> cellData.getValue().newNameProperty());
		extCol.setCellValueFactory(cellData -> cellData.getValue().extensionProperty());
		sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());

		chbCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
		newNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
		extCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
		sizeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

		chbCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(Integer idx) {
				FileListItem item = table.getItems().get(idx);
				return item.chbProperty();
			}
		}));

		sizeCol.setCellFactory(value -> new TableCell<FileListItem, SizeObj>() {
			@Override
			protected void updateItem(SizeObj item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item.getText());
				}
			}
		});
	}

	private void handleTableKeyEvent(KeyEvent event) {
		if (event.getCode() == KeyCode.SPACE) {
			table.getSelectionModel()
			     .getSelectedItems()
			     .forEach(item -> item.setChecked(!item.isChecked()));
		}
	}

	private void requestFocus() {
		Platform.runLater(() -> {
			table.requestFocus();
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
		});
	}

	public void addListItems(List<FileListItem> items) {
		table.getItems().addAll(items);
		requestFocus();
	}

	public void updateNewFileName(List<FileNameFilter> filters) {
		if(!isAnyElementSelected()) {
			LOGGER.warn(Str.get("FileOverviewController.no_file_selected"));
			return;
		}

		table.getItems()
			 .stream()
			 .filter(FileListItem::isChecked)
			 .peek(item -> item.setNewName(item.getName()))
			 .forEach(item -> filters.forEach(filter -> filterItemName(item, filter)));
	}

	private boolean isAnyElementSelected() {
		return table.getItems().stream().filter(FileListItem::isChecked).findAny().isPresent();
	}

	private void filterItemName(FileListItem item, FileNameFilter filter) {
		try {
			item.setNewName(filter.filter(item).trim());
		} catch (CMException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void clearNewFileName() {
		table.getItems().forEach(item -> item.setNewName(""));
	}

	public List<FileListItem> getCheckedFiles() {
		return table.getItems().stream()
				.filter(FileListItem::isChecked) // nur selektierte Elemente
				.collect(Collectors.toList());   // gefilterte Elemente in Liste speichern
	}

	public void clearFileList() {
		table.getItems().clear();
		table.refresh();
	}

	public void setAllChecked(boolean checked) {
		table.getItems().forEach(item -> item.setChecked(checked));
	}
}
