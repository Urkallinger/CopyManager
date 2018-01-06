//package de.urkallinger.copymanager.controller;
//
//import static de.urkallinger.copymanager.TestUtils.AVI;
//import static de.urkallinger.copymanager.TestUtils.MKV;
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.mock;
//import static org.testfx.api.FxAssert.verifyThat;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.testfx.framework.junit.ApplicationTest;
//import org.testfx.matcher.control.ListViewMatchers;
//import org.testfx.matcher.control.TableViewMatchers;
//
//import de.urkallinger.copymanager.MainApp;
//import de.urkallinger.copymanager.utils.Str;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//
//public class OptionPanelControllerTest extends ApplicationTest {
//
//	@Mock
//	MainApp mainApp;
//	@Mock
//	FileOverviewController fileOverviewController;
//
//	private OptionPanelController optController;
//
//	@Test
//	public void addEmptyExtensionTest() throws Exception {
//		// ARRANGE
//		initMainApp();
//
//		// ACT
//		clickOn("#btnAddFileEx");
//		sleep(200);
//		push(KeyCode.ENTER);
//
//		clickOn("#btnAddFileEx");
//		sleep(200);
//		type(KeyCode.SPACE);
//		type(KeyCode.SPACE);
//		push(KeyCode.ENTER);
//
//		// ASSERT
//		verifyThat("#fileExtensionList", ListViewMatchers.hasItems(0));
//	}
//
//	@Test
//	public void addExtensionTest() throws Exception {
//		// ARRANGE
//		List<String> extensions = Arrays.asList(MKV, AVI);
//		initMainApp();
//
//		// ACT
//		for (String ext : extensions) {
//			clickOn("#btnAddFileEx");
//			sleep(200);
//			typeString(ext.equals(MKV) ? "." + MKV : ext); // Einmal mit Punkt vorangestellt
//			push(KeyCode.ENTER);
//		}
//
//		// ASSERT
//		extensions.forEach(ext -> verifyThat("#fileExtensionList", ListViewMatchers.hasListCell(ext)));
//	}
//
//	@Test
//	public void removeExtensionTest() {
//		// ARRANGE
//		List<String> extensions = Arrays.asList(MKV, AVI);
//		initMainApp();
//
//		// ACT
//		for (String ext : extensions) {
//			clickOn("#btnAddFileEx");
//			sleep(200);
//			typeString(ext);
//			push(KeyCode.ENTER);
//		}
//
//		for (String ext : extensions) {
//			Node listEntry = from(lookup("#fileExtensionList")).lookup(ext).queryFirst();
//			clickOn(listEntry, MouseButton.PRIMARY).push(KeyCode.DELETE);
//		}
//
//		// ASSERT
//		verifyThat("#fileExtensionList", ListViewMatchers.hasItems(0));
//	}
//
//	@Test
//	public void addReplacementButtonTest() {
//		// ACT
//		clickOn("#btnAddFileReplacement");
//
//		// ASSERT
//		verifyThat("#tblReplacement", TableViewMatchers.hasItems(1));
//	}
//
//	@Test
//	public void removeReplacementTest() {
//		// ARRANGE
//		clickOn("#btnAddFileReplacement");
//		clickOn("#btnAddFileReplacement");
//
//		// ACT
//		Set<Node> replaceEntries = from(lookup("#tblReplacement"))
//				                       .lookup(Str.get("keywords.replace"))
//				                       .queryAll();
//		replaceEntries.forEach(entry -> {
//			clickOn(entry, MouseButton.PRIMARY);
//			push(KeyCode.DELETE);
//		});
//
//		// ASSERTS
//		verifyThat("#tblReplacement", TableViewMatchers.hasItems(0));
//	}
//
//	@Test
//	public void addReplacementTest() {
//		// ARRANGE
//		clickOn("#btnAddFileReplacement");
//		String replace = "asdf";
//		String with = "qwertz";
//
//		// ACT
//		Node replaceEntry = from(lookup("#tblReplacement")).lookup(Str.get("keywords.replace")).queryFirst();
//		doubleClickOn(replaceEntry, MouseButton.PRIMARY).write(replace);
//		push(KeyCode.ENTER);
//
//		Node withEntry = from(lookup("#tblReplacement")).lookup(Str.get("keywords.with")).queryFirst();
//		doubleClickOn(withEntry, MouseButton.PRIMARY).write(with);
//		push(KeyCode.ENTER);
//
//		// ASSERT
//		verifyThat("#tblReplacement", TableViewMatchers.hasItems(1));
//		verifyThat("#tblReplacement", TableViewMatchers.hasTableCell(replace));
//		verifyThat("#tblReplacement", TableViewMatchers.hasTableCell(with));
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void filterCountTest() throws Exception {
//		// ARRANGE
//		int count = 2;
//		for(int i = 0; i < count; i++) {
//			clickOn("#btnAddFileReplacement");
//			String replace = "asdf";
//			String with = "qwertz";
//
//			Node replaceEntry = from(lookup("#tblReplacement"))
//					                .lookup(Str.get("keywords.replace"))
//					                .queryFirst();
//			doubleClickOn(replaceEntry, MouseButton.PRIMARY).write(replace);
//			push(KeyCode.ENTER);
//
//			Node withEntry = from(lookup("#tblReplacement"))
//					             .lookup(Str.get("keywords.with"))
//					             .queryFirst();
//			doubleClickOn(withEntry, MouseButton.PRIMARY).write(with);
//			push(KeyCode.ENTER);
//		}
//
//		// ACT
//		clickOn("#btnUseTemplate");
//
//		// ASSERT
//		@SuppressWarnings("rawtypes")
//		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
//		Mockito.verify(fileOverviewController).updateNewFileName(argument.capture());
//		assertEquals(count + 1, argument.getValue().size());
//	}
//
//	@Override
//	public void start(Stage stage) throws Exception {
//		mainApp = mock(MainApp.class);
//		fileOverviewController = mock(FileOverviewController.class);
//
//		FXMLLoader loader = new FXMLLoader();
//		loader.setResources(Str.getBundle());
//		loader.setLocation(getClass().getResource("/view/OptionPanel.fxml"));
//		AnchorPane optionPanel = (AnchorPane) loader.load();
//
//		optController = loader.getController();
//		optController.setMainApp(mainApp);
//		optController.setFileOverview(fileOverviewController);
//
//		Scene scene = new Scene(optionPanel);
//		stage.setWidth(300);
//		stage.setHeight(600);
//		stage.setScene(scene);
//		stage.show();
//	}
//
//	private void initMainApp() {
//		doAnswer(invocation -> {
//			String extension = (String) invocation.getArguments()[0];
//			optController.addFileExtension(extension);
//			return null;
//		}).when(mainApp).addFileExtension(Mockito.any());
//
//		doAnswer(invocation -> {
//			String extension = (String) invocation.getArguments()[0];
//			optController.removeFileExtension(extension);
//			return null;
//		}).when(mainApp).removeFileExtension(Mockito.any());
//	}
//
//	private void typeString(String value) {
//		for (String s : value.split("(?!^)")) {
//			switch (s) {
//			case ".":
//				type(KeyCode.PERIOD);
//				break;
//			default:
//				type(KeyCode.valueOf(s.toUpperCase()));
//				break;
//			}
//
//		}
//	}
//
//}
