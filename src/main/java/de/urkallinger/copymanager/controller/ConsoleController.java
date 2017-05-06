package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.CMLogger;
import de.urkallinger.copymanager.model.ConsoleItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ConsoleController extends UIController implements CMLogger {
	@FXML
	private ListView<ConsoleItem> console = new ListView<>();
	@FXML
	private Button btnClear = new Button();
	@FXML
	private ToggleButton btnScrollLock = new ToggleButton();
	@FXML
	private ProgressBar progressBar = new ProgressBar();

	private ImageView locked;
	private ImageView unlocked;

	@FXML
	public void initialize() {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		btnClear.setGraphic(new ImageView(imgRes));

		locked = new ImageView(new Image(getClass().getResourceAsStream("/images/locked.png")));
		unlocked = new ImageView(new Image(getClass().getResourceAsStream("/images/unlocked.png")));

		btnScrollLock.setGraphic(locked);
		btnScrollLock.setSelected(true);

		console.setFocusTraversable(false);

		console.setCellFactory(value -> new ListCell<ConsoleItem>() {
			@Override
			protected void updateItem(ConsoleItem item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item.getText());
					setTextFill(item.getColor());
					setFont(new Font("Consolas", 15));
					setGraphic(item.getGraphic());
				} else {
					setText(null);
					setGraphic(null);
					setTextFill(null);
				}
			}
		});
	}

	@FXML
	private void onClear() {
		console.getItems().clear();
		console.refresh();
	}

	@FXML
	private void onScrollLock() {
		if (btnScrollLock.isSelected()) {
			btnScrollLock.setGraphic(locked);
		} else {
			btnScrollLock.setGraphic(unlocked);
		}
	}

	private ProgressIndicator getLoadingIndicator() {
		final ProgressIndicator pi = new ProgressIndicator();
		pi.setVisible(true);
		pi.setStyle(" -fx-progress-color: #729917;");
		pi.setMaxWidth(25);
		pi.setMaxHeight(25);
		pi.setPrefWidth(25);
		pi.setPrefHeight(25);
		return pi;
	}

	private void addListItem(ConsoleItem item) {
		console.getItems().add(item);
		if (btnScrollLock.isSelected()) {
			Platform.runLater(() -> console.scrollTo(console.getItems().size() - 1));
		}
	}

	@Override
	public void error(String text) {
		Platform.runLater(() -> {
			ConsoleItem item = new ConsoleItem(text, Color.RED);
			addListItem(item);
		});
	}

	@Override
	public void warning(String text) {
		Platform.runLater(() -> {
			ConsoleItem item = new ConsoleItem(text, Color.rgb(255, 100, 0));
			addListItem(item);
		});
	}

	@Override
	public void info(String text) {
		Platform.runLater(() -> {
			ConsoleItem item = new ConsoleItem(text, Color.BLACK);
			addListItem(item);
		});
	}

	@Override
	public int action(String text, boolean indicator) {
		// TODO: Platform.runLater -> überlegen wie man das hin bekommt, wegen Rückgabetyp
		ConsoleItem item = new ConsoleItem(text, Color.BLUE);
		if (indicator) {
			item.setGraphic(getLoadingIndicator());
		}
		addListItem(item);

		return console.getItems().size() - 1;
	}

	@Override
	public void setDone(final int idx) {
		Platform.runLater(() -> {
			Image imgRes = new Image(getClass().getResourceAsStream("/images/done.png"));
			console.getItems().get(idx).setGraphic(new ImageView(imgRes));
			console.refresh();
		});
	}

	@Override
	public void setFailed(final int idx) {
		Platform.runLater(() -> {
			Image imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
			console.getItems().get(idx).setGraphic(new ImageView(imgRes));
			console.refresh();
		});
	}

	@Override
	public void setProgress(final double x) {
		Platform.runLater(() -> progressBar.setProgress(x));
	}

	@Override
	public void enableProgressBar(boolean enable, final long millis) {
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(millis);
			} catch (Exception e) {
			}
			Platform.runLater(() -> {
				progressBar.setVisible(enable);
				progressBar.setProgress(0.0);
				if (enable) {
					AnchorPane.setBottomAnchor(console, 25.0);
				} else {
					AnchorPane.setBottomAnchor(console, 0.0);
				}
			});
		});
		t.setDaemon(true);
		t.start();
	}
}
