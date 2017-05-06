package de.urkallinger.copymanager.controller;

import java.util.Map;
import java.util.Optional;

import de.urkallinger.copymanager.Configuration;
import de.urkallinger.copymanager.ConfigurationManager;
import de.urkallinger.copymanager.model.PatternListItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PatternDialogController extends UIController {

	@FXML
	private TableView<PatternListItem> table = new TableView<>();
	@FXML
	private TableColumn<PatternListItem, String> nameCol = new TableColumn<>();
	@FXML
	private TableColumn<PatternListItem, String> patternCol = new TableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();

	private Optional<String> selection = Optional.empty();

	@FXML
	public void initialize() {
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setOnKeyPressed(event -> handleTableKeyEvent(event));
		
		nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		patternCol.prefWidthProperty().bind(table.widthProperty().multiply(0.74));

		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		patternCol.setCellValueFactory(cellData -> cellData.getValue().patternProperty());

		patternCol.setCellFactory(call -> new TableCell<PatternListItem, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item);
					setFont(new Font("Consolas", 15));
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
			PatternListItem pattern = table.getSelectionModel().getSelectedItem();
			deletePattern(pattern);
		default:
			break;
		}
	}

	private void deletePattern(PatternListItem pattern) {
		table.getItems().remove(pattern);
		Configuration cfg = ConfigurationManager.loadConfiguration();
		cfg.getPattern().remove(pattern.getName());
		ConfigurationManager.saveConfiguration(cfg);
	}
	
	private void requestFocus() {
		Platform.runLater(() -> {
			table.requestFocus();
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
		});
	}

	@FXML
	private void handleOk() {
		Optional<PatternListItem> item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
		item.ifPresent(it -> selection = Optional.ofNullable(it.getPattern()));
		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void addListItems(Map<String, String> pattern) {
		pattern.forEach((k, v) -> {
			PatternListItem item = new PatternListItem(k, v);
			table.getItems().add(item);
		});
		
		requestFocus();
	}

	public Optional<String> getSelectedPattern() {
		return selection;
	}
}
