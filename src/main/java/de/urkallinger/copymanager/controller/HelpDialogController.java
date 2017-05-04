package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.model.HelpItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class HelpDialogController extends UIController {

	@FXML
	private TreeTableView<HelpItem> table = new TreeTableView<>();
	@FXML
	private TreeTableColumn<HelpItem, String> colTopic = new TreeTableColumn<>();
	@FXML
	private TreeTableColumn<HelpItem, String> colDescr = new TreeTableColumn<>();
	@FXML
	Button btnOk = new Button();

	@FXML
	public void initialize() {
		table.setOnKeyPressed(event -> handleTableKeyEvent(event));
		
		colTopic.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
		colDescr.prefWidthProperty().bind(table.widthProperty().multiply(0.74));

		colTopic.setCellValueFactory(param -> param.getValue().getValue().topicProperty());
		colDescr.setCellValueFactory(param -> param.getValue().getValue().descrProperty());

		HelpItem hiRoot = new HelpItem();
		hiRoot.setTopic("SHORTCUTS");
		
		HelpItem hi = new HelpItem();
		hi.setTopic("STRG+O");
		hi.setDescr("Open Directory");
		
		TreeItem<HelpItem> root = new TreeItem<>(hiRoot);
		TreeItem<HelpItem> ti = new TreeItem<>(hi);
		root.getChildren().add(ti);
		
		table.setRoot(root);
	}
	
	private void handleTableKeyEvent(KeyEvent event) {
		switch (event.getCode()) {
		case ESCAPE:
			handleOk();
			break;

		default:
			break;
		}
	}
	@FXML
	private void handleOk() {
		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}
}
