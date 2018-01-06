package de.urkallinger.copymanager.controller;

import de.urkallinger.copymanager.TaskView;
import de.urkallinger.copymanager.data.Task;
import de.urkallinger.copymanager.events.TaskAddedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class TasksController extends UIController implements TaskView {

    @FXML
    TableView<Task> taskTable = new TableView<>();
    @FXML
    TableColumn<Task, Node> colGraphic = new TableColumn<>();
    @FXML
    TableColumn<Task, String> colText = new TableColumn<>();

    @FXML
    public void initialize() {
        hideTableHeaders();
        colText.setCellValueFactory(cellData -> cellData.getValue().textProperty());
        colGraphic.setCellValueFactory(new PropertyValueFactory<Task, Node>("graphic"));
    }

    private void hideTableHeaders() {
        taskTable.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
                Pane header = (Pane) taskTable.lookup("TableHeaderRow");
                if (header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                }
            }
        });
    }

    @Override
    public void addTask(Task task) {
        globalEventBus.post(new TaskAddedEvent(task));
        execOnFxAppThread(() -> {
            taskTable.getItems().add(task);
            taskTable.scrollTo(taskTable.getItems().size() - 1);
        });
    }

    @Override
    public void updateTask(Task task) {
        execOnFxAppThread(() -> {
            int idx = taskTable.getItems().indexOf(task);
            if (idx != -1) {
                taskTable.getItems().set(idx, task);
            }
        });
    }
}
