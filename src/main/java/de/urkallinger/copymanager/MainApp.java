package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.urkallinger.copymanager.callables.FileExtensionReader;
import de.urkallinger.copymanager.callables.FileReader;
import de.urkallinger.copymanager.controller.ConsoleController;
import de.urkallinger.copymanager.controller.ExtensionListDialogController;
import de.urkallinger.copymanager.controller.FileOverviewController;
import de.urkallinger.copymanager.controller.OptionPanelController;
import de.urkallinger.copymanager.controller.RootLayoutController;
import de.urkallinger.copymanager.model.FileListItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

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

	public void updateFileList() {
		currentDir.ifPresent(dir -> {
			Callback<List<File>, Object> callback = new Callback<List<File>, Object>() {
				@Override
				public Object call(List<File> fileList) {
					if (fileList.size() > 0) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								clearFileList();
								fileOverviewController.addListItems(fileList);
								updateNewFileName();
							}
						});
					}
					return null;
				}
			};
			
			Runnable reader = new FileReader(logger, currentDir.get(), fm.getFileExtensions(), callback);
			new Thread(reader).start();
		});
	}

	public void showExtensionDialog() {
		if (!currentDir.isPresent()) {
			logger.warning("could not show extension dialog. no directory is selected.");
			return;
		}

		try {
			final MainApp ma = this;
			Stage stage = new Stage();
			stage.setTitle("Extensions");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();
			
			Runnable readExts = new Runnable() {
				public void run() {
					try {
						Callable<Set<String>> c = new FileExtensionReader(logger, currentDir.get());
						FutureTask<Set<String>> task = new FutureTask<Set<String>>(c);
						Thread reader = new Thread(task);
						
						reader.start();
						reader.join();

						Set<String> exts = task.get();
						
						ExtensionListDialogController controller = loader.getController();
						controller.setMainApp(ma);
						controller.addListItems(exts);

						// Show the scene containing the root layout.
						Scene scene = new Scene(layout);
						Platform.runLater(new  Runnable() {
							
							@Override
							public void run() {
								stage.setScene(scene);
								stage.showAndWait();
								ma.clearFileList();
								ma.updateFileList();
							}
						});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(readExts);
			t.setDaemon(true);
			t.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		currentDir.ifPresent(dir -> logger.info("current directory: " + dir));
	}

	public static void main(String[] args) {
		launch(args);
	}
}
