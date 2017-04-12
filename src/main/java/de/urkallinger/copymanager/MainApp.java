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
    
    private FileManager fm;
    private Optional<File> currentDir = Optional.empty();
	private RootLayoutController rootController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CopyManager");

        initRootLayout();

        showFileOverview();
        showOptionPanel();
        showConsole();
        fm = new FileManager(this.consoleController);
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

    /**
     * Shows the person overview inside the root layout.
     */
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
    
    public void error(String text) {
        consoleController.error(text);
    }
    
    public void warning(String text) {
        consoleController.warning(text);
    }
    
    public void info(String text) {
        consoleController.info(text);
    }
    
    public void action(String text, boolean indicator) {
        consoleController.action(text, indicator);
    }
    
    public void addFileExtension(String extension) {
        fm.getFileExtensions().add(extension);
        info("new file extension: " + extension);
    }
    
    public void removeFileExtension(String extension) {
        fm.getFileExtensions().remove(extension);
        info("file extension deleted: " + extension);
    }
    
    public void updateNewFileName() {
    	fileOverviewController.updateNewFileName(optController.getPattern().get(),
    			optController.getTemplate());
    }
    
    public void clearNewFileName() {
    	fileOverviewController.clearNewFileName();
    }
    
    public void updateFileList() {
        currentDir.ifPresent(dir -> {
            List<File> fileList = fm.getFileList(dir);
            if(fileList.size() > 0) {
                clearFileList();
                fileOverviewController.addListItems(fileList);
                updateNewFileName();
            }
        });
    }
    
    public void setAllChecked(boolean checked) {
        fileOverviewController.setAllChecked(checked);
    }
    
    public void clearFileList() {
        fileOverviewController.clearFileList();
    }
    
    public void copyFiles() {
    	List<FileListItem> files = fileOverviewController.getCheckedFiles();
    	if(files.size() > 0) {
	    	DirectoryChooser directoryChooser = new DirectoryChooser();
			Optional<File> dest = Optional.ofNullable(directoryChooser.showDialog(this.primaryStage));
			dest.ifPresent(dir -> {
				fm.copyFiles(files, dir);
			});
    	} else {
    		info("no files to copy.");
    	}
    	
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Optional<File> getCurrentDir() {
        return currentDir;
    }
    
    public void setCurrentDir(File f) {
        currentDir = Optional.ofNullable(f);
        currentDir.ifPresent(dir -> info("current directory: " + dir));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
