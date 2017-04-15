package de.urkallinger.copymanager.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExtensionListItem {
	private final BooleanProperty chb = new SimpleBooleanProperty();
	private final StringProperty extension = new SimpleStringProperty();
	
	public final BooleanProperty chbProperty() {
        return this.chb;
    }

    public final boolean isChecked() {
        return this.chbProperty().get();
    }

    public final void setChecked(final boolean checked) {
        this.chbProperty().set(checked);
    }
    
    public final StringProperty extensionProperty() {
        return this.extension;
    }

    public final String getExtension() {
        return this.extensionProperty().get();
    }

    public final void setExtension(final String ext) {
        this.extensionProperty().set(ext);
    }

}
