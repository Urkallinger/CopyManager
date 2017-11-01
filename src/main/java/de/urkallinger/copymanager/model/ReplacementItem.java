package de.urkallinger.copymanager.model;

import javafx.beans.property.SimpleStringProperty;

public class ReplacementItem {
	SimpleStringProperty oldValue = new SimpleStringProperty();
	SimpleStringProperty newValue = new SimpleStringProperty();

	public ReplacementItem(String oldValue, String newValue) {
		this.oldValue.set(oldValue);
		this.newValue.set(newValue);
	}
	
	public final SimpleStringProperty oldValueProperty() {
		return this.oldValue;
	}

	public final String getOldValue() {
		return this.oldValueProperty().get();
	}

	public final void setOldValue(final String oldValue) {
		this.oldValueProperty().set(oldValue);
	}

	public final SimpleStringProperty newValueProperty() {
		return this.newValue;
	}

	public final String getNewValue() {
		return this.newValueProperty().get();
	}

	public final void setNewValue(final String newValue) {
		this.newValueProperty().set(newValue);
	}
}
