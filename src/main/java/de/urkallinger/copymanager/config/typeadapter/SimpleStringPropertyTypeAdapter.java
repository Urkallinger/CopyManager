package de.urkallinger.copymanager.config.typeadapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javafx.beans.property.SimpleStringProperty;

public class SimpleStringPropertyTypeAdapter extends TypeAdapter<SimpleStringProperty> {

    @Override
    public void write(JsonWriter out, SimpleStringProperty property) throws IOException {
        out.value(property.get());
    }

    @Override
    public SimpleStringProperty read(JsonReader in) throws IOException {
        final SimpleStringProperty property = new SimpleStringProperty();
        property.set(in.nextString());
        return property;
    }

}
