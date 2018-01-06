package de.urkallinger.copymanager.events;

import de.urkallinger.copymanager.data.Task;

public class TaskAddedEvent {
    private Task task;

    public TaskAddedEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
