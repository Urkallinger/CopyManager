package de.urkallinger.copymanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.data.RenameConfigItem;
import de.urkallinger.copymanager.data.ReplacementItem;
import de.urkallinger.copymanager.exceptions.CMException;
import de.urkallinger.copymanager.files.filter.FileNameFilter;
import de.urkallinger.copymanager.files.filter.RegExFilter;
import de.urkallinger.copymanager.files.filter.ReplaceFilter;
import de.urkallinger.copymanager.ui.dialogs.RenameConfigsDialog;
import de.urkallinger.copymanager.utils.Str;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class OptionPanelController extends UIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionPanelController.class);

    @FXML
    private VBox vBox = new VBox();
    @FXML
    private ListView<String> fileExtensionList = new ListView<>();
    @FXML
    private Button btnAddFileEx = new Button();
    @FXML
    private Button btnAddFileReplacement = new Button();
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
    private TableView<ReplacementItem> tblReplacement = new TableView<>();
    @FXML
    private TableColumn<ReplacementItem, String> colOldValue = new TableColumn<>();
    @FXML
    private TableColumn<ReplacementItem, String> colNewValue = new TableColumn<>();

    private FileOverviewController fileOverview;

    @FXML
    public void initialize() {
        fileExtensionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileExtensionList.setOnKeyPressed(event -> handleExtensionListKeyEvent(event));

        tblReplacement.setOnKeyPressed(event -> handleReplacementKeyEvent(event));

        Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
        btnAddFileEx.setGraphic(new ImageView(imgRes));

        imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
        btnAddFileReplacement.setGraphic(new ImageView(imgRes));

        imgRes = new Image(getClass().getResourceAsStream("/images/load.png"));
        btnLoad.setGraphic(new ImageView(imgRes));

        imgRes = new Image(getClass().getResourceAsStream("/images/save.png"));
        btnSave.setGraphic(new ImageView(imgRes));

        imgRes = new Image(getClass().getResourceAsStream("/images/clear.png"));
        btnClear.setGraphic(new ImageView(imgRes));

        imgRes = new Image(getClass().getResourceAsStream("/images/useTemplate.png"));
        btnUseTemplate.setGraphic(new ImageView(imgRes));

        colOldValue.setCellValueFactory(cellData -> cellData.getValue().oldValueProperty());
        colNewValue.setCellValueFactory(cellData -> cellData.getValue().newValueProperty());

        // Zellen editierbar machen
        colOldValue.setCellFactory(TextFieldTableCell.forTableColumn());
        colOldValue.setOnEditCommit(event -> event.getRowValue().setOldValue(event.getNewValue()));

        // Zellen editierbar machen
        colNewValue.setCellFactory(TextFieldTableCell.forTableColumn());
        colNewValue.setOnEditCommit(event -> event.getRowValue().setNewValue(event.getNewValue()));
    }

    private void handleExtensionListKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            ObservableList<String> items = fileExtensionList.getSelectionModel().getSelectedItems();
            items.forEach(item -> mainApp.removeFileExtension(item));
            mainApp.clearFileList();
            mainApp.updateFileList();
        }
    }

    private void handleReplacementKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            ReplacementItem selected = tblReplacement.getSelectionModel().getSelectedItem();
            tblReplacement.getItems().removeIf(item -> item.equals(selected));
        }
    }

    @FXML
    public void handleAddExtension() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(Str.get("OptionPanelController.new_file_ext_title"));
        dialog.setHeaderText(Str.get("OptionPanelController.new_file_ext"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ext -> {
            if (ext.trim().isEmpty())
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
    public void handleAddReplacement() {
        String replace = Str.get("keywords.replace");
        String with = Str.get("keywords.with");
        tblReplacement.getItems().add(new ReplacementItem(replace, with));
    }

    @FXML
    public void handleUseTemplate() {
        try {
            validateTemplateAndPattern();

            RegExFilter regExFilter = new RegExFilter(getPattern(), getTemplate());

            // Erst RegExFilter hinzufügen
            List<FileNameFilter> filters = new ArrayList<>();
            filters.add(regExFilter);

            // Alle weiteren Filter werden auf das Ergebnis des RegEx angewendet
            filters.addAll(tblReplacement.getItems().stream()
                    .map(rep -> new ReplaceFilter(rep.getOldValue(), rep.getNewValue())).collect(Collectors.toList()));

            fileOverview.updateNewFileName(filters);
        } catch (CMException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        mainApp.clearNewFileName();
    }

    @FXML
    public void handleLoadRenameConfigs() {
        Configuration cfg = ConfigurationManager.loadConfiguration();
        List<RenameConfigItem> renameConfigs = cfg.getRenameConfigurations();

        if (renameConfigs.size() > 0) {
            RenameConfigsDialog dialog = new RenameConfigsDialog(mainApp);
            dialog.setParentStage(mainApp.getPrimaryStage());
            dialog.setRenameConfigs(renameConfigs);
            dialog.show();
            dialog.getSelectedRenameConfig().ifPresent(renameConfig -> {
                txtPattern.setText(renameConfig.getPattern());
                txtTemplate.setText(renameConfig.getTemplate());
            });
        } else {
            LOGGER.warn(Str.get("OptionPanelController.no_saved_rename_configs_found"));
        }
    }

    @FXML
    public void handleSavePattern() {
        try {
            validateTemplateAndPattern();
        } catch (CMException e) {
            LOGGER.error(e.getMessage());
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(Str.get("OptionPanelController.save_rename_config"));
        dialog.setHeaderText(Str.get("OptionPanelController.save_rename_config_text"));
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (name.isEmpty()) {
                LOGGER.error(Str.get("OptionPanelController.rename_config_invalid_name"));
                return;
            }

            Configuration cfg = ConfigurationManager.loadConfiguration();
            cfg.addRenameConfiguration(name, txtPattern.getText(), txtTemplate.getText());
            ConfigurationManager.saveConfiguration(cfg);
        });
    }

    private void validateTemplateAndPattern() throws CMException {
        if (getPattern().isEmpty()) {
            throw new CMException(Str.get("OptionPanelController.no_pattern_defined"));
        }

        if (getTemplate().isEmpty()) {
            throw new CMException(Str.get("OptionPanelController.no_template_defined"));
        }
    }

    public String getPattern() {
        return txtPattern.getText() == null ? "" : txtPattern.getText();
    }

    public String getTemplate() {
        return txtTemplate.getText() == null ? "" : txtTemplate.getText().trim();
    }

    public void addFileExtension(String extension) {
        fileExtensionList.getItems().add(extension);
        fileExtensionList.refresh();
    }

    public void removeFileExtension(String extension) {
        fileExtensionList.getItems().remove(extension);
        fileExtensionList.refresh();
    }

    public FileOverviewController getFileOverview() {
        return fileOverview;
    }

    public void setFileOverview(FileOverviewController fileOverview) {
        this.fileOverview = fileOverview;
    }
}
