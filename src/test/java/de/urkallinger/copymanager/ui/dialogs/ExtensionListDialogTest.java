package de.urkallinger.copymanager.ui.dialogs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TableViewMatchers;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.controller.ExtensionListDialogController;
import de.urkallinger.copymanager.utils.Str;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ExtensionListDialogTest extends ApplicationTest {

	@Mock
	MainApp mainApp;
	
	private static final String MKV = "mkv";
	private static final String DOC = "doc";
	private static final String CSV = "csv";
	private static final String TXT = "txt";
	private static final String AVI = "avi";
	
	private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(MKV, DOC, CSV, TXT, AVI));
	
	@Test
	public void tableContainsElementsTest() throws Exception {
		String table = "#table";
		
		verifyThat(table, NodeMatchers.isNotNull());
		verifyThat(table, TableViewMatchers.hasItems(EXTENSIONS.size()));
		for(String extension : EXTENSIONS) {
			verifyThat(table, TableViewMatchers.hasTableCell(extension));
		}
	}
	
	@Test
	public void singleSelectionTest() {
		clickOn(MKV);
		clickOn("#btnOk");
		
		verify(mainApp, times(1)).addFileExtension(MKV);
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void multiSelectionTest() {
		clickOn(MKV);
		clickOn(AVI);
		clickOn("#btnOk");
		
		verify(mainApp, times(1)).addFileExtension(MKV);
		verify(mainApp, times(1)).addFileExtension(AVI);
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void cancelTest() {
		clickOn("#btnCancel");
		
		verify(mainApp, times(0)).addFileExtension(any());
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void spaceKeyTest() {
		push(KeyCode.DOWN);
		push(KeyCode.DOWN);
 		push(KeyCode.SPACE);
		push(KeyCode.ENTER);
		
		verify(mainApp, times(1)).addFileExtension(any());
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainApp = mock(MainApp.class);
		
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(Str.getBundle());
		loader.setLocation(getClass().getResource("/view/dialogs/ExtensionListDialog.fxml"));
		BorderPane layout = (BorderPane) loader.load();

		ExtensionListDialogController dialogController = loader.getController();
		dialogController.setMainApp(mainApp);
		dialogController.addListItems(EXTENSIONS);

		Scene scene = new Scene(layout);
		stage.setMinWidth(layout.getMinWidth() + 50);
		stage.setMinHeight(layout.getMinHeight() + 50);
		stage.setScene(scene);
		stage.show();
	}
}
