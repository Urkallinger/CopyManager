package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import de.urkallinger.copymanager.controller.ConsoleController;
import de.urkallinger.copymanager.controller.FileOverviewController;
import de.urkallinger.copymanager.controller.OptionPanelController;
import de.urkallinger.copymanager.controller.RootLayoutController;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private FileOverviewController fileOverviewController;
	private OptionPanelController optController;
	private ConsoleController consoleController;
	private RootLayoutController rootController;

	private FileManager fm;
	private Optional<File> currentDir = Optional.empty();
	private LoggerCallback logger;

	private ParamCallback<List<FileListItem>> getUpdateFileCacheCallback() {
		return new ParamCallback<List<FileListItem>>() {
			@Override
			public void call(List<FileListItem> fileList) {
				fm.setFileCache(fileList);
			}
		};
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
		rootController.setLogger(logger);
		consoleController.setLogger(logger);
		fileOverviewController.setLogger(logger);
		optController.setLogger(logger);

		fm = new FileManager(logger);
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

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	public void addFileExtension(String extension) {
		fm.getFileExtensions().add(extension);
		optController.addFileExtension(extension);
		logger.info("new file extension: " + extension);
	}

	public void removeFileExtension(String extension) {
		fm.getFileExtensions().remove(extension);
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
			fm.readFiles(rootDir, callback);
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
			DirectoryChooser directoryChooser = new DirectoryChooser();
			Optional<File> dest = Optional.ofNullable(directoryChooser.showDialog(this.primaryStage));
			dest.ifPresent(dir -> {
				fm.copyFiles(files, dir);
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

	public void setCurrentDir(File f) {
		currentDir = Optional.ofNullable(f);
		currentDir.ifPresent(dir -> {
			logger.info("current directory: " + dir);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
