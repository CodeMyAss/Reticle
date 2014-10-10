package org.spigot.reticle.events;

import java.util.LinkedHashMap;

import javax.swing.JMenuItem;

import org.spigot.reticle.storage;
import org.spigot.reticle.API.Plugin;
import org.spigot.reticle.botfactory.mcbot;

public class PluginMenuOpenEvent extends Event {
	private LinkedHashMap<String, LinkedHashMap<String, JMenuItem>> items;

	public PluginMenuOpenEvent(mcbot mcbot, LinkedHashMap<String, LinkedHashMap<String, JMenuItem>> it) {
		super(mcbot);
		this.items = it;
	}

	/**
	 * Add item to plugin menu
	 * 
	 * @param Plugin
	 *            Plugin to handle
	 * @param item
	 *            Item to add
	 */
	public void addItem(Plugin Plugin, JMenuItem item) {
		String plname=storage.pluginManager.getPluginInfo(Plugin).Name;
		if (!items.containsKey(plname)) {
			items.put(plname, new LinkedHashMap<String, JMenuItem>());
		}
		this.items.get(plname).put(item.getText(), item);
	}

}
