package org.spigot.reticle.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.spigot.reticle.PluginInfo;
import org.spigot.reticle.storage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Config handler. This object should make it easier to manage config
 * 
 * @author Encorn
 * 
 */
public class ConfigHandler {
	private final PluginInfo plinfo;
	private final String ConfigFile;

	public ConfigHandler(Plugin plugin) {
		this.plinfo = storage.pluginManager.getPluginInfo(plugin);
		this.ConfigFile = "Plugins/" + plinfo.Name + "/config.txt";
	}
	

	/**
	 * Reads config stored in file
	 * @return Returns object stored in file or null
	 */
	public Object ReadConfig() {
		Object o = null;
		Scanner scanner = null;
		try {
			scanner = new Scanner(getFile(), "UTF-8");
			scanner.useDelimiter("\\A");
			String xml = scanner.next();
			XStream xstream = new XStream(new DomDriver());
			o = xstream.fromXML(xml);
		} catch (FileNotFoundException e) {
			System.err.println("Failed to read config file");
		}
		if (scanner != null) {
			scanner.close();
		}
		return o;
	}

	/**
	 * Writes config object to file
	 * @param ConfigObject The object to be written
	 */
	public void WriteConfig(Object ConfigObject) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias(ConfigObject.getClass().getSimpleName(), ConfigObject.getClass());
		String xml = xstream.toXML(ConfigObject);
		init();
		File file = getFile();
		if (file == null) {
			System.err.println("Failed to write to config file");
		} else {
			PrintWriter writer;
			try {
				writer = new PrintWriter(file, "UTF-8");
				xml = xstream.toXML(ConfigObject);
				writer.println(xml);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				System.err.println("Failed to write to config file");
			}
		}
	}

	private File getFile() {
		if (init()) {
			return new File(ConfigFile);
		} else {
			return null;
		}
	}

	/**
	 * Creates plugin config.txt file in plugin folder
	 * 
	 * @return True if successful, False if otherwise
	 */
	public boolean init() {
		if (mde("Plugins")) {
			if (mde("Plugins/" + plinfo.Name)) {
				if (mfe(ConfigFile)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean mfe(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			if (f.isFile()) {
				return true;
			}
		}
		return (f.mkdirs());
	}

	private boolean mde(String dirname) {
		File f = new File(dirname);
		if (f.exists()) {
			if (f.isDirectory()) {
				return true;
			}
		}
		return (f.mkdirs());
	}
}
