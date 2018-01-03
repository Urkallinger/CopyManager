package de.urkallinger.copymanager.config.typeadapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringPropertyTypeAdapter extends TypeAdapter<StringProperty> {

    @Override
    public void write(JsonWriter out, StringProperty property) throws IOException {
        out.value(property.get());
    }

    @Override
    public StringProperty read(JsonReader in) throws IOException {
        final StringProperty property = new SimpleStringProperty();
        property.set(in.nextString());
        return property;
    }

}
