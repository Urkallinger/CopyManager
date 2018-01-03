package de.urkallinger.copymanager.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import de.urkallinger.copymanager.config.typeadapter.SimpleStringPropertyTypeAdapter;
import de.urkallinger.copymanager.config.typeadapter.StringPropertyTypeAdapter;
import de.urkallinger.copymanager.utils.Str;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final String CONFIG_FILE_NAME = "config.dat";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(StringProperty.class, new StringPropertyTypeAdapter())
            .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyTypeAdapter())
            .create();

    private ConfigurationManager() {
    }

    public static boolean configurationExists() {
        return new File(CONFIG_FILE_NAME).exists();
    }

    public static Configuration loadConfiguration() {
        Configuration config;
        try (JsonReader reader = new JsonReader(new FileReader(CONFIG_FILE_NAME))) {
            config = GSON.fromJson(reader, Configuration.class);
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            LOGGER.error(Str.get("ConfigurationManager.load_config_err"));
            LOGGER.error(e.getMessage());
            config = new Configuration();
        }

        return config;
    }

    public static void createNewConfiguration() {
        saveConfiguration(new Configuration());
    }

    public static void saveConfiguration(Configuration cfg) {
        String json = GSON.toJson(cfg, Configuration.class);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_FILE_NAME))) {
            bw.write(json);
        } catch (IOException e) {
            LOGGER.error(Str.get("ConfigurationManager.save_config_err"));
            LOGGER.error(e.getMessage());
        }
    }
}
