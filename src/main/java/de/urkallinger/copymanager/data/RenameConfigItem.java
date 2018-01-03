package de.urkallinger.copymanager.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RenameConfigItem {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty pattern = new SimpleStringProperty();
	private final StringProperty template = new SimpleStringProperty();

	public RenameConfigItem() {
		// JAXB Constructor
	}

	public RenameConfigItem(String name, String pattern, String template) {
		setName(name);
		setPattern(pattern);
		setTemplate(template);
	}

	public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final StringProperty patternProperty() {
        return this.pattern;
    }

    public final String getPattern() {
        return this.patternProperty().get();
    }

    public final void setPattern(final String pattern) {
        this.patternProperty().set(pattern);
    }

    public final StringProperty templateProperty() {
        return this.template;
    }

    public final String getTemplate() {
        return this.templateProperty().get();
    }

    public final void setTemplate(final String template) {
        this.templateProperty().set(template);
    }

    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof RenameConfigItem) {
    		RenameConfigItem other = (RenameConfigItem) obj;
    		return name.get().equals(other.name.get())
    				&& pattern.get().equals(other.pattern.get())
    				&& template.get().equals(other.template.get());
    	}
    	return false;
    }
}
