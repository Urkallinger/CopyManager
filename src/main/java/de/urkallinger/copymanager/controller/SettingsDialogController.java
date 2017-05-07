package de.urkallinger.copymanager.controller;

import java.util.Map;

import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.config.Settings;
import de.urkallinger.copymanager.controls.ToggleSwitch;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class SettingsDialogController extends UIController {

	@FXML
	private GridPane grid = new GridPane();
	@FXML
	private ScrollPane scroll = new ScrollPane();
	
	@FXML
	public void initialize() {
		scroll.setFitToWidth(true);
		
		grid.setHgap(10);
		grid.setVgap(10);
		
		grid.getColumnConstraints().get(0).setPercentWidth(80);
		grid.getColumnConstraints().get(1).setPercentWidth(20);
		
		Map<Settings, Boolean> settings = ConfigurationManager.loadConfiguration().getSettings();
		settings.forEach((setting, value) -> {
			String text = Str.get(String.format("settings.%s", setting));
			Label label = new Label(text);
			label.setFont(new Font(15));
			
			ToggleSwitch ts = new ToggleSwitch();
			ts.switchOnProperty().set(value);
			ts.switchOnProperty().addListener(listener -> {
				Configuration cfg = ConfigurationManager.loadConfiguration();
				cfg.addSetting(setting, ts.switchOnProperty().get());
				ConfigurationManager.saveConfiguration(cfg);
			});
			
			int rowIdx = grid.getRowConstraints().size();
			grid.addRow(rowIdx);
			grid.add(label, 0, rowIdx);
			grid.add(ts, 1, rowIdx);
		});
	}
}
