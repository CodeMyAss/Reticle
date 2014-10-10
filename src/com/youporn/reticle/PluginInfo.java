package org.spigot.reticle;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import org.spigot.reticle.API.Plugin;

public class PluginInfo {

	public final String Author;
	public final File FileName;
	public final String Version;
	private boolean isEnabled = false;
	private Plugin instance;
	private final Class<?> Class;
	public final String Name;
	private final URLClassLoader loader;

	public ClassLoader getLoader() {
		return loader;
	}
	
	protected PluginInfo(URLClassLoader clazzL, String author, File fileEntry, String version, String name, Class<?> Class) {
		this.Author = author;
		this.FileName = fileEntry;
		this.Version = version;
		this.Class = Class;
		this.Name = name;
		this.loader=clazzL;
	}

	protected void closeLoader() {
		try {
			loader.close();
		} catch (IOException e) {
		}
	}
	
	protected Plugin getInstance() {
		if (instance == null) {
			try {
				instance = (Plugin) Class.newInstance();
			} catch (Exception e) {
				System.err.println("Failed to create instance of plugin "+this.Name);
				e.printStackTrace();
			}
		}
		return instance;
	}

	protected void unLoad() {
		instance = null;
	}

	protected boolean isEnabled() {
		return isEnabled;
	}

	protected void Enable() {
		isEnabled = true;
	}

	protected void Disable() {
		isEnabled = false;
	}
}
