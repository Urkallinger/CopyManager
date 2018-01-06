package de.urkallinger.copymanager;

import java.util.ArrayList;
import java.util.List;

import de.urkallinger.copymanager.data.Task;

public final class TaskManager {
    private static List<TaskView> taskViews = new ArrayList<>();

    public static void registerTaskView(TaskView taskView) {
        taskViews.add(taskView);
    }

    public static void addTask(Task task) {
        for(TaskView taskView : taskViews) {
            taskView.addTask(task);
        }
    }

    public static void updateTask(Task task) {
        for(TaskView taskView : taskViews) {
            taskView.updateTask(task);
        }
    }
}
