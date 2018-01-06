package de.urkallinger.copymanager.controller;

import com.google.common.eventbus.Subscribe;

import de.urkallinger.copymanager.events.TaskAddedEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class OutputTabPanelController extends UIController {

    @FXML
    private TabPane outputTabPane = new TabPane();
    @FXML
    private Tab tabConsole = new Tab();
    @FXML
    private Tab tabTasks = new Tab();

    @FXML
    public void initialize() {

    }

    public void setConsoleTab(Node node) {
        tabConsole.setContent(node);
    }

    public void setTasksTab(Node node) {
        tabTasks.setContent(node);
    }

    @Subscribe
    public void handleTaskAddedEvent(TaskAddedEvent event) {
        System.out.println("TaskAddedEvent");
        outputTabPane.getSelectionModel().select(tabTasks);
    }
}
