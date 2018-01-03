package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.copymanager.config.Configuration;
import de.urkallinger.copymanager.config.ConfigurationManager;
import de.urkallinger.copymanager.controller.ConsoleController;
import de.urkallinger.copymanager.controller.FileOverviewController;
import de.urkallinger.copymanager.controller.OptionPanelController;
import de.urkallinger.copymanager.controller.RootLayoutController;
import de.urkallinger.copymanager.data.FileListItem;
import de.urkallinger.copymanager.exceptions.FileCopierInProgressException;
import de.urkallinger.copymanager.exceptions.FileReaderInProgressException;
import de.urkallinger.copymanager.files.FileManager;
import de.urkallinger.copymanager.utils.Str;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static CMLogger logger = new DefaultLogger();
	private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

	private Stage primaryStage;
	private BorderPane rootLayout;

	private FileOverviewController fileOverviewController;
	private OptionPanelController optController;
	private ConsoleController consoleController;
	private RootLayoutController rootController;

	private FileManager fm;
	private Scene scene;
	private Optional<File> currentDir = Optional.empty();

	@Override
	public void start(Stage primaryStage) {
	    this.primaryStage = primaryStage;
		this.primaryStage.setTitle("CopyManager");

		updateConfig();

		initRootLayout();
		showConsole();
		showFileOverview();
		showOptionPanel();

		optController.setFileOverview(fileOverviewController);

		logger = consoleController;

		addGlobalShortCuts();

		fm = new FileManager();
	}

	private void updateConfig() {
		if(!ConfigurationManager.configurationExists()) {
			ConfigurationManager.createNewConfiguration();
		} else {
			Configuration cfg = ConfigurationManager.loadConfiguration();
			ConfigurationManager.saveConfiguration(cfg);
		}
	}

	private void addGlobalShortCuts() {
		// EventFilter verwenden, da manche Events von Controls (bsp. Textfeld)
		// gefressen werden und nie beim EventHandler ankommen
		scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch(event.getCode()) {
			case O:
				if(event.isControlDown()) {
					rootController.handleOpen();
				}
				break;
			case P:
				if(event.isControlDown()) {
					optController.handleLoadRenameConfigs();
				}
				break;
			case U:
				if(event.isControlDown()) {
					optController.handleUseTemplate();
				}
				break;
			case PLUS:
				if(event.isControlDown()) {
					optController.handleAddExtension();
				}
				break;
			case S:
				if(event.isControlDown()) {
					optController.handleSavePattern();
				}
				break;
			case F1:
				rootController.handleCopy();
				break;
			case F5:
				rootController.handleRefresh();
				break;
			case DIGIT1:
				if(event.isControlDown()) {
					rootController.handleCheckAll();
				}
				break;
			case DIGIT2:
				if(event.isControlDown()) {
					rootController.handleUncheckAll();
				}
			default:
				break;
			}
		});
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();

			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			rootController = loader.getController();
			rootController.setMainApp(this);
			rootController.setStage(primaryStage);

			Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.png"));
			primaryStage.getIcons().add(icon);

			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			LOGGER.error(Str.get("MainApp.load_root_layout_err"));
			LOGGER.error(e.getMessage());
		}
	}

	public void showFileOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/FileOverview.fxml"));
			AnchorPane fileOverview = (AnchorPane) loader.load();

			rootController.setRightArea(fileOverview);

			fileOverviewController = loader.getController();
		} catch (IOException e) {
			LOGGER.error(Str.get("MainApp.load_file_overview_err"));
			LOGGER.error(e.getMessage());
		}
	}

	public void showOptionPanel() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/OptionPanel.fxml"));
			AnchorPane optionPanel = (AnchorPane) loader.load();

			rootController.setLeftArea(optionPanel);
			optController = loader.getController();
			optController.setMainApp(this);

		} catch (IOException e) {
			LOGGER.error(Str.get("MainApp.load_option_panel_err"));
			LOGGER.error(e.getMessage());
		}
	}

	public void showConsole() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(Str.getBundle());
			loader.setLocation(getClass().getResource("/view/Console.fxml"));
			AnchorPane console = (AnchorPane) loader.load();

			rootController.setBottomArea(console);
			consoleController = loader.getController();

		} catch (IOException e) {
			LOGGER.error(Str.get("MainApp.load_console_err"));
			LOGGER.error(e.getMessage());
		}
	}

	public void addFileExtension(String extension) {
		if(!fm.getFileExtensions().contains(extension)) {
			fm.getFileExtensions().add(extension);
			optController.addFileExtension(extension);
			String info = String.format(Str.get("MainApp.new_file_ext"), extension);
			LOGGER.info(info);
		}
	}

	public void removeFileExtension(String extension) {
		fm.getFileExtensions().remove(extension);
		optController.removeFileExtension(extension);
		String info = String.format(Str.get("MainApp.file_ext_removed"), extension);
		LOGGER.info(info);
	}

	public void clearNewFileName() {
		fileOverviewController.clearNewFileName();
	}

	public void updateFileCache() {
		readFiles((List<FileListItem> fileList) -> fm.setFileCache(fileList));
	}

	public void readFiles(ParamCallback<List<FileListItem>> callback) {
		currentDir.ifPresent(rootDir -> {
			try {
				fm.readFiles(rootDir, callback);
			} catch (FileReaderInProgressException e) {
				LOGGER.error(e.getMessage());
			}
		});
	}

	public void updateFileList() {
		fileOverviewController.addListItems(fm.getFiteredFileCache());
	}

	public void cacheFileListItems(List<FileListItem> items) {
		fm.setFileCache(items);
	}

	public void setAllChecked(boolean checked) {
		fileOverviewController.setAllChecked(checked);
	}

	public void clearFileList() {
		fileOverviewController.clearFileList();
	}

	public void copyFiles() {
		List<FileListItem> files = fileOverviewController.getCheckedFiles();
		if (files.size() > 0) {
			Configuration cfg = ConfigurationManager.loadConfiguration();
			File target = new File(cfg.getLastDestDir());

			DirectoryChooser directoryChooser = new DirectoryChooser();
			if(target.exists()) directoryChooser.setInitialDirectory(target);
			Optional<File> dest = Optional.ofNullable(directoryChooser.showDialog(this.primaryStage));
			dest.ifPresent(dir -> {
				try {
					cfg.setLastDestDir(dir.getAbsolutePath());
					ConfigurationManager.saveConfiguration(cfg);
					fm.copyFiles(files, dir);
				} catch (FileCopierInProgressException e) {
					LOGGER.error(e.getMessage());
				}
			});
		} else {
			LOGGER.info(Str.get("MainApp.no_files_to_copy"));
		}

	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Optional<File> getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File f) throws FileReaderInProgressException {
		if(fm.isReadingFiles()) {
			throw new FileReaderInProgressException(Str.get("FileManager.thread_already_reading_err"));
		}

		currentDir = Optional.ofNullable(f);
		currentDir.ifPresent(dir -> {
			String info = String.format(Str.get("MainApp.curr_dir_set"), dir);
			LOGGER.info(info);
		});
	}

	public static CMLogger getLogger() {
		return MainApp.logger;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
