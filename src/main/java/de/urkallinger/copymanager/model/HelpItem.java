package de.urkallinger.copymanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HelpItem {
	private final StringProperty topic = new SimpleStringProperty();
	private final StringProperty descr = new SimpleStringProperty();
	
    public final StringProperty topicProperty() {
        return this.topic;
    }

    public final String getTopic() {
        return this.topicProperty().get();
    }

    public final void setTopic(final String topic) {
        this.topicProperty().set(topic);
    }
    
    public final StringProperty descrProperty() {
        return this.descr;
    }

    public final String getDescr() {
        return this.descrProperty().get();
    }

    public final void setDescr(final String descr) {
        this.descrProperty().set(descr);
    }
}
