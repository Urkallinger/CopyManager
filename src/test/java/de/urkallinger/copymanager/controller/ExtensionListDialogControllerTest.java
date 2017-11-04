package de.urkallinger.copymanager.controller;

import static de.urkallinger.copymanager.TestUtils.AVI;
import static de.urkallinger.copymanager.TestUtils.EXTENSIONS;
import static de.urkallinger.copymanager.TestUtils.MKV;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mock;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TableViewMatchers;

import de.urkallinger.copymanager.MainApp;
import de.urkallinger.copymanager.utils.Str;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ExtensionListDialogControllerTest extends ApplicationTest {

	@Mock
	MainApp mainApp;

	@Test
	public void tableContainsElementsTest() throws Exception {
		String table = "#table";

		verifyThat(table, NodeMatchers.isNotNull());
		verifyThat(table, TableViewMatchers.hasItems(EXTENSIONS.size()));
		for (String extension : EXTENSIONS) {
			verifyThat(table, TableViewMatchers.hasTableCell(extension));
		}
	}

	@Test
	public void singleSelectionTest() {
		Node mkvEntry = from(lookup("#table")).lookup(MKV).queryFirst();

		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn("#btnOk");

		verify(mainApp, times(1)).addFileExtension(MKV);
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void singleUnSelectionTest() {
		Node mkvEntry = from(lookup("#table")).lookup(MKV).queryFirst();

		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn("#btnOk");

		verify(mainApp, times(0)).addFileExtension(any());
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}

	@Test
	public void multiSelectionTest() {
		Node mkvEntry = from(lookup("#table")).lookup(MKV).queryFirst();
		Node aviEntry = from(lookup("#table")).lookup(AVI).queryFirst();
		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn(aviEntry, MouseButton.PRIMARY);
		clickOn("#btnOk");

		verify(mainApp, times(1)).addFileExtension(MKV);
		verify(mainApp, times(1)).addFileExtension(AVI);
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void multiUnSelectionTest() {
		Node mkvEntry = from(lookup("#table")).lookup(MKV).queryFirst();
		Node aviEntry = from(lookup("#table")).lookup(AVI).queryFirst();
		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn(aviEntry, MouseButton.PRIMARY);
		clickOn(mkvEntry, MouseButton.PRIMARY);
		clickOn(aviEntry, MouseButton.PRIMARY);
		clickOn("#btnOk");

		verify(mainApp, times(0)).addFileExtension(any());
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
	public void escapeTest() {
		push(KeyCode.ESCAPE);

		verify(mainApp, times(0)).addFileExtension(any());
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}

	@Test
	public void spaceKeySelectionTest() throws Exception {
		setFocusOnTable();

		push(KeyCode.DOWN);
		push(KeyCode.SPACE);
		push(KeyCode.ENTER);

		verify(mainApp, times(1)).addFileExtension(any());
		verify(mainApp, times(1)).clearFileList();
		verify(mainApp, times(1)).updateFileList();
	}
	
	@Test
	public void spaceKeyUnSelectionTest() throws Exception {
		setFocusOnTable();

		push(KeyCode.DOWN);
		push(KeyCode.SPACE);
		push(KeyCode.SPACE);
		push(KeyCode.ENTER);

		verify(mainApp, times(0)).addFileExtension(any());
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
	
	private void setFocusOnTable() throws Exception {
		TableView<?> table = (TableView<?>) lookup("#table").queryFirst();

		// Focus auf den ersten Eintrag der Tabelle setzen
		final FutureTask<Integer> query = new FutureTask<>(() -> {
			table.requestFocus();
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
			return 0;
		});
		Platform.runLater(query);
		
		// Warten bis der Focus gesetzt wurde
		query.get(1, TimeUnit.SECONDS);
	}
}
