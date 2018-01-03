package de.urkallinger.copymanager.controller;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.CMLogger;
import de.urkallinger.copymanager.data.ConsoleItem;
import de.urkallinger.copymanager.logging.ListViewAppender;
import de.urkallinger.copymanager.utils.Str;
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
import javafx.scene.text.Font;

public class ConsoleController extends UIController implements CMLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleController.class);

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

        ListViewAppender.addListView(console);
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
            execute(() -> console.scrollTo(console.getItems().size() - 1));
        }
    }

    @Override
    public int action(String text, boolean indicator) {
        CountDownLatch latch = new CountDownLatch(1);
        execute(() -> {
            ConsoleItem item = new ConsoleItem(text, Level.INFO);
            if (indicator) {
                item.setGraphic(getLoadingIndicator());
            }
            addListItem(item);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            return -1;
        }
        return console.getItems().size() - 1;
    }

    @Override
    public void setDone(final int idx) {
        execute(() -> {
            try {
                Image imgRes = new Image(getClass().getResourceAsStream("/images/done.png"));
                console.getItems().get(idx).setGraphic(new ImageView(imgRes));
                console.refresh();
            } catch (IndexOutOfBoundsException e) {
                String msg = String.format(Str.get("ConsoleController.update_item_ioob"), idx);
                LOGGER.error(msg);
            }
        });
    }

    @Override
    public void setFailed(final int idx) {
        execute(() -> {
            try {
                Image imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
                console.getItems().get(idx).setGraphic(new ImageView(imgRes));
                console.refresh();
            } catch (IndexOutOfBoundsException e) {
                String msg = String.format(Str.get("ConsoleController.update_item_ioob"), idx);
                LOGGER.error(msg);
            }
        });
    }

    @Override
    public void setProgress(final double x) {
        execute(() -> progressBar.setProgress(x));
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

    private void execute(Runnable run) {
        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }
}
