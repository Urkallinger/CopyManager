package de.urkallinger.copymanager.controller;

import java.util.List;
import java.util.Optional;

import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.data.RenameConfigItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RenameConfigsDialogController extends UIController {

	@FXML
	private TableView<RenameConfigItem> table = new TableView<>();
	@FXML
	private TableColumn<RenameConfigItem, String> nameCol = new TableColumn<>();
	@FXML
	private TableColumn<RenameConfigItem, String> patternCol = new TableColumn<>();
	@FXML
	private TableColumn<RenameConfigItem, String> templateCol = new TableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();

	private Optional<RenameConfigItem> selection = Optional.empty();

	@FXML
	public void initialize() {
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setOnKeyPressed(event -> handleTableKeyEvent(event));

		nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		patternCol.prefWidthProperty().bind(table.widthProperty().multiply(0.37));
		templateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.37));

		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		patternCol.setCellValueFactory(cellData -> cellData.getValue().patternProperty());
		templateCol.setCellValueFactory(cellData -> cellData.getValue().templateProperty());

		patternCol.setCellFactory(call -> new TableCell<RenameConfigItem, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item);
					setStyle("-fx-font-family: Consolas");
			} else {
					setText(null);
				}
			}
		});

		templateCol.setCellFactory(call -> new TableCell<RenameConfigItem, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item);
					setStyle("-fx-font-family: Consolas");
				} else {
					setText(null);
				}
			}
		});
	}

	private void handleTableKeyEvent(KeyEvent event) {
		switch (event.getCode()) {
		case ENTER:
			handleOk();
			break;

		case ESCAPE:
			handleCancel();
			break;

		case DELETE:
			RenameConfigItem renameConfig = table.getSelectionModel().getSelectedItem();
			deleteRenameConfig(renameConfig);
		default:
			break;
		}
	}

	private void deleteRenameConfig(RenameConfigItem renameConfig) {
		table.getItems().remove(renameConfig);
		Configuration cfg = ConfigurationManager.loadConfiguration();
		cfg.getRenameConfigurations().remove(renameConfig);
		ConfigurationManager.saveConfiguration(cfg);
	}

	private void requestFocus() {
	    execOnFxAppThread(() -> {
			table.requestFocus();
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
		});
	}

	@FXML
	private void handleOk() {
		Optional<RenameConfigItem> item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
		item.ifPresent(it -> selection = Optional.ofNullable(it));
		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void addListItems(List<RenameConfigItem> renameConfigs) {
		table.getItems().addAll(renameConfigs);
		requestFocus();
	}

	public Optional<RenameConfigItem> getSelectedRenameConfig() {
		return selection;
	}
}
