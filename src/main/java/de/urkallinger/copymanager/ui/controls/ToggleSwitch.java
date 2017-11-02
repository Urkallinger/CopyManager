package de.urkallinger.copymanager.ui.controls;

import de.urkallinger.copymanager.utils.Str;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ToggleSwitch extends HBox {

	private final Label label = new Label();
	private final Button button = new Button();
	private final String txtOn = Str.get("ToggleSwitch.on");
	private final String txtOff = Str.get("ToggleSwitch.off");

	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

	public SimpleBooleanProperty switchOnProperty() {
		return switchedOn;
	}

	private void init() {

		label.setText(txtOff);
		label.setStyle("-fx-text-fill: white;");

		getChildren().addAll(label, button);
		button.setOnAction((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		label.setOnMouseClicked((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		setBackgroundStyle("990500");
		bindProperties();
	}

	private void setBackgroundStyle(String color) {
		// Default Width
		setWidth(80);
		label.setAlignment(Pos.CENTER);
		setStyle("-fx-background-color: #"+color+"; "
				+ "-fx-font-weight: bold; "
				+ "-fx-text-fill: white; "
				+ "-fx-background-radius: 4;");
		setAlignment(Pos.CENTER_LEFT);
	}

	private void bindProperties() {
		label.prefWidthProperty().bind(widthProperty().divide(2));
		label.prefHeightProperty().bind(heightProperty());
		button.prefWidthProperty().bind(widthProperty().divide(2));
		button.prefHeightProperty().bind(heightProperty());
	}

	public ToggleSwitch() {
		init();
		switchedOn.addListener((a, b, c) -> {
			if (c) {
				label.setText(txtOn);
				setBackgroundStyle("729917");
				label.toFront();
			} else {
				label.setText(txtOff);
				setBackgroundStyle("990500");
				button.toFront();
			}
		});
	}
}