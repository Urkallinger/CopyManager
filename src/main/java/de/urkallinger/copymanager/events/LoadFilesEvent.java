package de.urkallinger.copymanager.events;

import java.io.File;

public class LoadFilesEvent {
    private final File currentDir;

    public LoadFilesEvent(File currentDir) {
        this.currentDir = currentDir;
    }

    public final File getCurrentDir() {
        return currentDir;
    }
}
