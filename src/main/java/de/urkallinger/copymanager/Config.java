package de.urkallinger.copymanager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {

	private static final String CONFIG_XML = "config.xml";

	private static final Config instance = new Config();
	
	private String lastSrcDir;
	private String lastDestDir;
	private Map<String, String> pattern;
	
	private Config() {
		this.lastSrcDir = "";
		this.lastDestDir = "";
		this.pattern = new HashMap<>();
	}
	
	public static Config getInstance() {
		return instance;
	}
	
	public void loadConfig() {
		File cfgFile = new File(CONFIG_XML);
		if (cfgFile.exists()) {
			try {
				JAXBContext context = JAXBContext.newInstance(Config.class);
				Unmarshaller m = context.createUnmarshaller();
				Config cfg = (Config) m.unmarshal(cfgFile);
				copy(cfg);
			} catch (JAXBException e) {
				MainApp.getLogger().error("an error occured while loading the configuration.");
				MainApp.getLogger().error(e.getMessage());
			}
		}
	}
	
	private void copy(Config cfg) {
		this.lastSrcDir = cfg.lastSrcDir;
		this.lastDestDir = cfg.lastDestDir;
		this.pattern = new HashMap<>(cfg.pattern);
	}

	public void saveConfig() {
		File cfgFile = new File(CONFIG_XML);
		try {
			if(!cfgFile.exists()) cfgFile.createNewFile();
			
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(this, cfgFile);
		} catch (JAXBException | IOException e) {
			MainApp.getLogger().error("an error occured while saving the configuration.");
			MainApp.getLogger().error(e.getMessage());
		}
	}

	public String getLastSrcDir() {
		return lastSrcDir;
	}

	public void setLastSrcDir(String lastSrcDir) {
		this.lastSrcDir = lastSrcDir;
	}

	public String getLastDestDir() {
		return lastDestDir;
	}

	public void setLastDestDir(String lastDestDir) {
		this.lastDestDir = lastDestDir;
	}

	public Map<String, String> getPattern() {
		return pattern;
	}

	public void addPattern(String name, String pattern) {
		this.pattern.put(name, pattern);
	}
}
