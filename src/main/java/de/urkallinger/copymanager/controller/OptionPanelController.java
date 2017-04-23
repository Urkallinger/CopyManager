package de.urkallinger.copymanager.controller;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class OptionPanelController extends UIController {

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
	private Button btnClear = new Button();	
	@FXML
	private Button btnUseTemplate = new Button();

	@FXML
	private void initialize() {
		fileExtensionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
		btnAdd.setGraphic(new ImageView(imgRes));

		imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		btnClear.setGraphic(new ImageView(imgRes));
		
		imgRes = new Image(getClass().getResourceAsStream("/images/useTemplate.png"));
		btnUseTemplate.setGraphic(new ImageView(imgRes));
		
		fileExtensionList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					ObservableList<String> items = fileExtensionList.getSelectionModel().getSelectedItems();
					items.forEach(item -> mainApp.removeFileExtension(item));
					mainApp.clearFileList();
					mainApp.updateFileList();
				}
			}
		});
	}

	@FXML
	public void handleAdd() {
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

			mainApp.addFileExtension(ext);
			mainApp.clearFileList();
			mainApp.updateFileList();
		});
	}



	@FXML
	private void handleUseTemplate() {
    	if(!getPattern().isPresent()) {
    		logger.warning("no pattern defined.");
    		return;
    	}
    	
    	if(getTemplate().isEmpty()) {
    		logger.warning("no template defined.");
    		return;
    	}
		mainApp.updateNewFileName();
	}
	
	@FXML
	private void handleClear() {
		mainApp.clearNewFileName();
	}

	public Optional<Pattern> getPattern() {
		Optional<Pattern> opt = Optional.empty();
		if (!txtPattern.getText().isEmpty()) {
			try {

				Pattern pattern = Pattern.compile(txtPattern.getText());
				opt = Optional.of(pattern);
			} catch (PatternSyntaxException e) {
				logger.error("error compiling pattern");
				logger.error(e.getMessage());
			}
		}
		return opt;
	}

	public String getTemplate() {
		return txtTemplate.getText().trim();
	}
	
	public void addFileExtension(String extension) {
		fileExtensionList.getItems().add(extension);
		fileExtensionList.refresh();
	}
	
	public void removeFileExtension(String extension) {
		fileExtensionList.getItems().remove(extension);
		fileExtensionList.refresh();
	}
}
