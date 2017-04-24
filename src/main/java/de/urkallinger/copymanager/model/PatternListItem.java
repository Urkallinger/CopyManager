package de.urkallinger.copymanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PatternListItem {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty pattern = new SimpleStringProperty();
	
	public PatternListItem(String name, String pattern) {
		setName(name);
		setPattern(pattern);
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
}
