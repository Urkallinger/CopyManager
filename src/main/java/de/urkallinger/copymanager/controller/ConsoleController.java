package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.LoggerCallback;
import de.urkallinger.copymanager.model.ConsoleItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class ConsoleController extends UIController implements LoggerCallback {
	@FXML
	private ListView<ConsoleItem> console = new ListView<>();
	@FXML
	private Button btnClear = new Button();
	@FXML
	private ToggleButton btnScrollLock = new ToggleButton();
	
	private ImageView locked;
	private ImageView unlocked;
	
	@FXML
	private void initialize() {
		
		Image imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		btnClear.setGraphic(new ImageView(imgRes));
		
		locked = new ImageView(new Image(getClass().getResourceAsStream("/images/locked.png")));
		unlocked = new ImageView(new Image(getClass().getResourceAsStream("/images/unlocked.png")));
		
		btnScrollLock.setGraphic(locked);
		btnScrollLock.setSelected(true);

		console.setFocusTraversable( false );
		
		console.setCellFactory(new Callback<ListView<ConsoleItem>, ListCell<ConsoleItem>>() {
			
			@Override
			public ListCell<ConsoleItem> call(ListView<ConsoleItem> param) {
				return new ListCell<ConsoleItem>() {
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
				};
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
		if(btnScrollLock.isSelected()) {
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
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					console.scrollTo(console.getItems().size()-1);
				}
			});
		}
	}
	
	@Override
	public void error(String text) {
		ConsoleItem item = new ConsoleItem(text, Color.RED);
		addListItem(item);
	}
	
	@Override
	public void warning(String text) {
		ConsoleItem item = new ConsoleItem(text, Color.DARKORANGE);
		addListItem(item);
	}
	
	@Override
	public void info(String text) {
		ConsoleItem item = new ConsoleItem(text, Color.BLACK);
		addListItem(item);
	}
	
	@Override
	public int action(String text, boolean indicator) {
		ConsoleItem item = new ConsoleItem(text, Color.BLUE);
		if(indicator) {
			item.setGraphic(getLoadingIndicator());
		}
		addListItem(item);
		
		return console.getItems().size()-1;
	}

	@Override
	public void setDone(int idx) {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/done.png"));
		console.getItems().get(idx).setGraphic(new ImageView(imgRes));
		console.refresh();
	}

	@Override
	public void setFailed(int idx) {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
		console.getItems().get(idx).setGraphic(new ImageView(imgRes));
		console.refresh();
	}
}
