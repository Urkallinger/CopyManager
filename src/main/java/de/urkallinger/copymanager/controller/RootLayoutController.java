package de.urkallinger.copymanager.controller;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.events.LoadFilesEvent;
import de.urkallinger.copymanager.events.RefreshFilesEvent;
import de.urkallinger.copymanager.ui.dialogs.SettingsDialog;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class RootLayoutController extends UIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootLayoutController.class);

    @FXML
    private Button btnOpen = new Button();
    @FXML
    private Button btnCopy = new Button();
    @FXML
    private Button btnRefresh = new Button();
    @FXML
    private Button btnCheckAll = new Button();
    @FXML
    private Button btnUncheckAll = new Button();
    @FXML
    private Button btnSettings = new Button();
    @FXML
    private MenuButton btnLanguage = new MenuButton();
    @FXML
    private AnchorPane leftArea = new AnchorPane();
    @FXML
    private AnchorPane rightArea = new AnchorPane();
    @FXML
    private AnchorPane bottomArea = new AnchorPane();

    private Stage stage;

    private Optional<File> currentDir = Optional.empty();

    public RootLayoutController() {
    }

    @FXML
    public void initialize() {
        try {
            Image imgRes = new Image(getClass().getResourceAsStream("/images/folder.png"));
            btnOpen.setGraphic(new ImageView(imgRes));

            imgRes = new Image(getClass().getResourceAsStream("/images/copy.png"));
            btnCopy.setGraphic(new ImageView(imgRes));

            imgRes = new Image(getClass().getResourceAsStream("/images/refresh.png"));
            btnRefresh.setGraphic(new ImageView(imgRes));

            imgRes = new Image(getClass().getResourceAsStream("/images/checkAll.png"));
            btnCheckAll.setGraphic(new ImageView(imgRes));

            imgRes = new Image(getClass().getResourceAsStream("/images/uncheckAll.png"));
            btnUncheckAll.setGraphic(new ImageView(imgRes));

            imgRes = new Image(getClass().getResourceAsStream("/images/settings.png"));
            btnSettings.setGraphic(new ImageView(imgRes));

            Configuration cfg = ConfigurationManager.loadConfiguration();
            String imgPath = String.format("/images/flags/%s.png", cfg.getLocale().toString());
            imgRes = new Image(getClass().getResourceAsStream(imgPath));
            btnLanguage.setGraphic(new ImageView(imgRes));

            createLanguageMenuItems();
        } catch (Exception e) {
            LOGGER.error(Str.get("RootLayoutController.init_err"));
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    public void handleOpen() {
        Optional<File> file = Optional.empty();

        Configuration cfg = ConfigurationManager.loadConfiguration();
        File dir = new File(cfg.getLastSrcDir());

        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (dir.exists()) {
            directoryChooser.setInitialDirectory(dir);
        }
        file = Optional.ofNullable(directoryChooser.showDialog(stage));
        file.ifPresent(currDir -> {
            cfg.setLastSrcDir(currDir.getAbsolutePath());
            ConfigurationManager.saveConfiguration(cfg);

            // TODO: Prüfungen hinzufügen
            /*
             * if (fm.isReadingFiles()) {
             *     throw new FileReaderInProgressException(Str.get("FileManager.thread_already_reading_err"));
             * }
             *
             * currentDir = Optional.ofNullable(f);
             * currentDir.ifPresent(dir -> {
             *     String info = String.format(Str.get("MainApp.curr_dir_set"), dir);
             *     LOGGER.info(info);
             * );
             */
            currentDir = Optional.of(currDir);
            globalEventBus.post(new LoadFilesEvent(currentDir.get()));
        });
    }

    @FXML
    public void handleCopy() {
        mainApp.copyFiles();
    }

    @FXML
    public void handleRefresh() {
        if (currentDir.isPresent()) {
            globalEventBus.post(new RefreshFilesEvent(currentDir.get()));
        } else {
            LOGGER.warn(Str.get("RootLayoutController.refresh_no_dir"));
        }

    }

    @FXML
    public void handleCheckAll() {
        mainApp.setAllChecked(true);
    }

    @FXML
    public void handleUncheckAll() {
        mainApp.setAllChecked(false);
    }

    @FXML
    public void handleSettings() {
        SettingsDialog dialog = new SettingsDialog(mainApp);
        dialog.show();
    }

    private void createLanguageMenuItems() {
        String[] supported = { "de", "en" };

        for (String lang : supported) {
            MenuItem item = new MenuItem();
            String imgPath = String.format("/images/flags/%s.png", lang);
            Image imgRes = new Image(getClass().getResourceAsStream(imgPath));

            item.setGraphic(new ImageView(imgRes));
            item.setOnAction(event -> {
                Configuration cfg = ConfigurationManager.loadConfiguration();
                cfg.SetLocale(lang);
                ConfigurationManager.saveConfiguration(cfg);

                String info = String.format(Str.get("RootLayoutController.switch_language"), lang);
                LOGGER.info(info);
                btnLanguage.setGraphic(new ImageView(imgRes));
            });

            btnLanguage.getItems().add(item);
        }
    }

    private void setDefaultAnchors(Node node) {
        AnchorPane.setBottomAnchor(node, 5.0);
        AnchorPane.setLeftAnchor(node, 5.0);
        AnchorPane.setRightAnchor(node, 5.0);
        AnchorPane.setTopAnchor(node, 5.0);
    }

    public void setLeftArea(Node node) {
        leftArea.getChildren().add(node);
        setDefaultAnchors(node);
    }

    public void setRightArea(Node node) {
        rightArea.getChildren().add(node);
        setDefaultAnchors(node);
    }

    public void setBottomArea(Node node) {
        bottomArea.getChildren().add(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
