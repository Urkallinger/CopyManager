package de.urkallinger.copymanager.events;

import java.util.List;

import de.urkallinger.copymanager.files.filter.FileNameFilter;

public class NewFileNameFilterEvent {
    private final List<FileNameFilter> filters;

    public NewFileNameFilterEvent(final List<FileNameFilter> filters) {
        this.filters = filters;
    }

    public final List<FileNameFilter> getFilters() {
        return filters;
    }
}
