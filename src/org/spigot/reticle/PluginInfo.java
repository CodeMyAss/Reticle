package org.spigot.reticle;

import java.io.File;

import org.spigot.reticle.API.Plugin;

public class PluginInfo {

	public final String Author;
	public final File FileName;
	public final String Version;
	private boolean isEnabled = false;
	private Plugin instance;
	private Class<?> Class;
	public final String Name;

	protected PluginInfo(String author, File fileEntry, String version, String name, Class<?> Class) {
		this.Author = author;
		this.FileName = fileEntry;
		this.Version = version;
		this.Class = Class;
		this.Name = name;
	}

	protected boolean Load() {
		try {
			instance = (Plugin) Class.newInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected Plugin getInstance() {
		if (instance == null) {
			try {
				instance = (Plugin) Class.newInstance();
			} catch (Exception e) {
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
