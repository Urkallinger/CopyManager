package de.urkallinger.copymanager.events;

import java.io.File;

public class RefreshFilesEvent {
    private final File currentDir;

    public RefreshFilesEvent(File currentDir) {
        this.currentDir = currentDir;
    }

    public final File getCurrentDir() {
        return currentDir;
    }
}
