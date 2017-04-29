package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import de.urkallinger.copymanager.controller.ConsoleController;
import de.urkallinger.copymanager.controller.FileOverviewController;
import de.urkallinger.copymanager.controller.OptionPanelController;
import de.urkallinger.copymanager.controller.RootLayoutController;
import de.urkallinger.copymanager.exceptions.FileCopierInProgressException;
import de.urkallinger.copymanager.exceptions.FileReaderInProgressException;
import de.urkallinger.copymanager.model.FileListItem;
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
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	private FileOverviewController fileOverviewController;
	private OptionPanelController optController;
	private ConsoleController consoleController;
	private RootLayoutController rootController;

	private FileManager fm;
	private Optional<File> currentDir = Optional.empty();
	private Scene scene;

	private ParamCallback<List<FileListItem>> getUpdateFileCacheCallback() {
		return new ParamCallback<List<FileListItem>>() {
			@Override
			public void call(List<FileListItem> fileList) {
				fm.setFileCache(fileList);
			}
		};
	}
	
	private void addGlobalKeyEvents() {
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
					optController.handleLoadPattern();
				}
				break;
			case PLUS:
				if(event.isControlDown()) {
					optController.handleAdd();
				}
				break;
			case F1:
				rootController.handleCopy();
				break;
			case F5:
				rootController.handleRefresh();
				break;
			default:
				break;
			}
		});
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("CopyManager");

		initRootLayout();
		showConsole();
		showFileOverview();
		showOptionPanel();

		logger = consoleController;

		addGlobalKeyEvents();
		
		fm = new FileManager();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
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
			MainApp.getLogger().error("an error occured while loading the root layout.");
			MainApp.getLogger().error(e.getMessage());
		}
	}

	public void showFileOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/FileOverview.fxml"));
			AnchorPane fileOverview = (AnchorPane) loader.load();

			rootController.setRightArea(fileOverview);

			fileOverviewController = loader.getController();
		} catch (IOException e) {
			MainApp.getLogger().error("an error occured while loading the file overview.");
			MainApp.getLogger().error(e.getMessage());
		}
	}

	public void showOptionPanel() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/OptionPanel.fxml"));
			AnchorPane optionPanel = (AnchorPane) loader.load();

			rootController.setLeftArea(optionPanel);
			optController = loader.getController();
			optController.setMainApp(this);

		} catch (IOException e) {
			MainApp.getLogger().error("an error occured while loading the option panel.");
			MainApp.getLogger().error(e.getMessage());
		}
	}

	public void showConsole() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/Console.fxml"));
			AnchorPane console = (AnchorPane) loader.load();

			rootController.setBottomArea(console);
			consoleController = loader.getController();

		} catch (IOException e) {
			MainApp.getLogger().error("an error occured while loading the console panel.");
			MainApp.getLogger().error(e.getMessage());
		}
	}

	public void addFileExtension(String extension) {
		if(!fm.getFileExtensions().contains(extension)) {
			fm.getFileExtensions().add(extension);
			optController.addFileExtension(extension);
			logger.info("new file extension: " + extension);
		}
	}

	public void removeFileExtension(String extension) {
		fm.getFileExtensions().remove(extension);
		optController.removeFileExtension(extension);
		logger.info("file extension deleted: " + extension);
	}

	public void updateNewFileName() {
		fileOverviewController.updateNewFileName(optController.getPattern().get(), optController.getTemplate());
	}

	public void clearNewFileName() {
		fileOverviewController.clearNewFileName();
	}

	public void updateFileCache() {
		readFiles(getUpdateFileCacheCallback());
	}
	
	public void readFiles(ParamCallback<List<FileListItem>> callback) {
		currentDir.ifPresent(rootDir -> {
			try {
				fm.readFiles(rootDir, callback);
			} catch (FileReaderInProgressException e) {
				logger.error(e.getMessage());
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
			Config cfg = Config.getInstance();
			cfg.loadConfig();
			File target = new File(cfg.getLastDestDir());
			
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if(target.exists()) directoryChooser.setInitialDirectory(target);
			Optional<File> dest = Optional.ofNullable(directoryChooser.showDialog(this.primaryStage));
			dest.ifPresent(dir -> {
				try {
					cfg.setLastDestDir(dir.getAbsolutePath());
					cfg.saveConfig();
					fm.copyFiles(files, dir);
				} catch (FileCopierInProgressException e) {
					logger.error(e.getMessage());
				}
			});
		} else {
			logger.info("no files to copy.");
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
			throw new FileReaderInProgressException("there is already a thread reading files.");
		}
		
		currentDir = Optional.ofNullable(f);
		currentDir.ifPresent(dir -> {
			logger.info("current directory: " + dir);
		});
	}

	public static CMLogger getLogger() {
		return MainApp.logger;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
