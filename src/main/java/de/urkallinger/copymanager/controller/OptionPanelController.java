package de.urkallinger.copymanager.controller;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.dialogs.RenameConfigsDialog;
import de.urkallinger.copymanager.model.RenameConfigItem;
import de.urkallinger.copymanager.utils.Str;
import javafx.collections.ObservableList;
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
	private Button btnLoad = new Button();
	@FXML
	private Button btnSave = new Button();
	@FXML
	private TextField txtPattern = new TextField();
	@FXML
	private TextField txtTemplate = new TextField();
	@FXML
	private Button btnClear = new Button();	
	@FXML
	private Button btnUseTemplate = new Button();

	@FXML
	public void initialize() {
		fileExtensionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fileExtensionList.setOnKeyPressed(event -> handleListKeyEvent(event));
		
		Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
		btnAdd.setGraphic(new ImageView(imgRes));

		imgRes = new Image(getClass().getResourceAsStream("/images/load.png"));
		btnLoad.setGraphic(new ImageView(imgRes));
		
		imgRes = new Image(getClass().getResourceAsStream("/images/save.png"));
		btnSave.setGraphic(new ImageView(imgRes));
		
		imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		btnClear.setGraphic(new ImageView(imgRes));
		
		imgRes = new Image(getClass().getResourceAsStream("/images/useTemplate.png"));
		btnUseTemplate.setGraphic(new ImageView(imgRes));
	}
	
	private void handleListKeyEvent(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			ObservableList<String> items = fileExtensionList.getSelectionModel().getSelectedItems();
			items.forEach(item -> mainApp.removeFileExtension(item));
			mainApp.clearFileList();
			mainApp.updateFileList();
		}
	}
	
	@FXML
	public void handleAdd() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(Str.get("OptionPanelController.new_file_ext_title"));
		dialog.setHeaderText(Str.get("OptionPanelController.new_file_ext"));
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
	public void handleUseTemplate() {
		if(!validateRenameConfig()) {
			return;
		}
		mainApp.updateNewFileName();
	}
	
	@FXML
	private void handleClear() {
		mainApp.clearNewFileName();
	}

	@FXML
	public void handleLoadRenameConfigs() {
		Configuration cfg = ConfigurationManager.loadConfiguration();
		List<RenameConfigItem> renameConfigs = cfg.getRenameConfigurations();
		
		if(renameConfigs.size() > 0) {
			RenameConfigsDialog dialog = new RenameConfigsDialog(mainApp);
			dialog.setParentStage(mainApp.getPrimaryStage());
			dialog.setRenameConfigs(renameConfigs);
			dialog.show();
			dialog.getSelectedRenameConfig().ifPresent(renameConfig -> {
				txtPattern.setText(renameConfig.getPattern());
				txtTemplate.setText(renameConfig.getTemplate());
			});
		} else {
			MainApp.getLogger().warning(Str.get("OptionPanelController.no_saved_rename_configs_found"));
		}
	}
	
	@FXML
	public void handleSavePattern() {
		if(!validateRenameConfig()) {
			return;
		}
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(Str.get("OptionPanelController.save_rename_config"));
		dialog.setHeaderText(Str.get("OptionPanelController.save_rename_config_text"));
		Optional<String> result = dialog.showAndWait();
		
		result.ifPresent(name -> {
			if (name.isEmpty()) {
				MainApp.getLogger().error(Str.get("OptionPanelController.rename_config_invalid_name"));
				return;
			}
			
			Configuration cfg = ConfigurationManager.loadConfiguration();
			cfg.addRenameConfiguration(name, txtPattern.getText(), txtTemplate.getText());
			ConfigurationManager.saveConfiguration(cfg);
		});
	}
	
	private boolean validateRenameConfig() {
		if(!getPattern().isPresent()) {
    		MainApp.getLogger().warning(Str.get("OptionPanelController.no_pattern_defined"));
    		return false;
    	}
    	
    	if(getTemplate().isEmpty()) {
    		MainApp.getLogger().warning(Str.get("OptionPanelController.no_template_defined"));
    		return false;
    	}
    	
    	return true;
	}
	
	public Optional<Pattern> getPattern() {
		Optional<Pattern> opt = Optional.empty();
		if (!txtPattern.getText().isEmpty()) {
			try {

				Pattern pattern = Pattern.compile(txtPattern.getText());
				opt = Optional.of(pattern);
			} catch (PatternSyntaxException e) {
				MainApp.getLogger().error(Str.get("OptionPanelController.pattern_compile_err"));
				MainApp.getLogger().error(e.getMessage());
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
