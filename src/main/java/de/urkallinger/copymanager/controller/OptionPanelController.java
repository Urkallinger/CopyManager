package de.urkallinger.copymanager.controller;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.urkallinger.copymanager.MainApp;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class OptionPanelController {

	@FXML
	private VBox vBox = new VBox();
	@FXML
	private ListView<String> fileExtensionList = new ListView<>();
	@FXML
	private Button btnAdd = new Button();
	@FXML
	private TextField txtPattern = new TextField();
	@FXML
	private TextField txtTemplate = new TextField();
	@FXML
	private Button btnUseTemplate = new Button();
	@FXML
	private Button btnClear = new Button();	
	
	private MainApp mainApp;

	@FXML
	private void initialize() {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
		btnAdd.setGraphic(new ImageView(imgRes));

		imgRes = new Image(getClass().getResourceAsStream("/images/useTemplate.png"));
		btnUseTemplate.setGraphic(new ImageView(imgRes));

		imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		btnClear.setGraphic(new ImageView(imgRes));
		
		fileExtensionList.setCellFactory(lv -> {

			ListCell<String> cell = new ListCell<>();
			ContextMenu contextMenu = getContextMenu(cell);

			cell.textProperty().bind(cell.itemProperty());
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
		});
	}

	@FXML
	private void handleAdd() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New file extension");
		dialog.setHeaderText("Please enter a new file extension");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(ext -> {
			if (ext.isEmpty())
				return;

			if (ext.startsWith(".")) {
				ext = ext.substring(1, ext.length());
			}

			fileExtensionList.getItems().add(ext);
			fileExtensionList.refresh();
			mainApp.addFileExtension(ext);
			mainApp.updateFileList();
		});
	}

	private ContextMenu getContextMenu(ListCell<String> cell) {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteItem = new MenuItem();
		deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
		deleteItem.setOnAction(event -> {
			mainApp.removeFileExtension(cell.getItem());
			fileExtensionList.getItems().remove(cell.getItem());
			mainApp.clearFileList();
			mainApp.updateFileList();
		});
		contextMenu.getItems().add(deleteItem);

		return contextMenu;
	}

	@FXML
	private void handleUseTemplate() {
    	if(!getPattern().isPresent()) {
    		mainApp.warning("no pattern defined.");
    		return;
    	}
    	
    	if(getTemplate().isEmpty()) {
    		mainApp.warning("no template defined.");
    		return;
    	}
		mainApp.updateNewFileName();
	}
	
	@FXML
	private void handleClear() {
		mainApp.clearFileList();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public Optional<Pattern> getPattern() {
		Optional<Pattern> opt = Optional.empty();
		if (!txtPattern.getText().isEmpty()) {
			try {

				Pattern pattern = Pattern.compile(txtPattern.getText());
				opt = Optional.of(pattern);
			} catch (PatternSyntaxException e) {
				mainApp.error("error compiling pattern");
				mainApp.error(e.getMessage());
			}
		}
		return opt;
	}

	public String getTemplate() {
		return txtTemplate.getText();
	}
}
