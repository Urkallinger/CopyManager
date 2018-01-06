package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.data.ConsoleItem;
import de.urkallinger.copymanager.logging.ListViewAppender;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ConsoleController extends UIController {

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
                    setGraphic(item.getGraphic());
                } else {
                    setText(null);
                    setGraphic(null);
                    setTextFill(null);
                }
            }
        });

        console.getItems().addListener(new ListChangeListener<ConsoleItem>() {
            @Override
            public void onChanged(Change<? extends ConsoleItem> c) {
                if (btnScrollLock.isSelected()) {
                    execOnFxAppThread(() -> console.scrollTo(console.getItems().size() - 1));
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

    public void setProgress(final double x) {
        // TODO: Progressbar mit Taskcontroller oder so verknüpfen
        execOnFxAppThread(() -> progressBar.setProgress(x));
    }

    public void enableProgressBar(boolean enable, final long millis) {
        // TODO: Progressbar mit Taskcontroller oder so verknüpfen
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (Exception e) {
            }
            execOnFxAppThread(() -> {
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
