package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.urkallinger.copymanager.utils.Str;

public class ConfigurationManager {

	private static final String CONFIG_XML = "config.xml";
	
	private ConfigurationManager() {
	}
	
	public static boolean configurationExists() {
		return new File(CONFIG_XML).exists();
	}
	
	public static Configuration loadConfiguration() {
		File cfgFile = new File(CONFIG_XML);
		Configuration config = null;
		if (cfgFile.exists()) {
			try {
				JAXBContext context = JAXBContext.newInstance(Configuration.class);
				Unmarshaller m = context.createUnmarshaller();
				config = (Configuration) m.unmarshal(cfgFile);
			} catch (JAXBException e) {
				MainApp.getLogger().error(Str.get("ConfigurationManager.load_config_err"));
				MainApp.getLogger().error(e.getMessage());
			}
		}
		if(config == null) config = new Configuration();
		return config;
	}
	
	public static void createNewConfiguration() {
		saveConfiguration(new Configuration());
	}

	public static void saveConfiguration(Configuration cfg) {
		File cfgFile = new File(CONFIG_XML);
		try {
			if(!cfgFile.exists()) cfgFile.createNewFile();
			
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(cfg, cfgFile);
		} catch (JAXBException | IOException e) {
			MainApp.getLogger().error(Str.get("ConfigurationManager.save_config_err"));
			MainApp.getLogger().error(e.getMessage());
		}
	}
}
