package org.spigot.reticle.events;

import java.util.HashMap;

import org.spigot.reticle.storage;
import org.spigot.reticle.API.ContextMenuItem;
import org.spigot.reticle.botfactory.mcbot;

public class BotContextMenuEvent extends Event {
	private HashMap<String, ContextMenuItem> methods = new HashMap<String, ContextMenuItem>();
	private final String text;
	
	
	
	public BotContextMenuEvent(mcbot bot, HashMap<String, ContextMenuItem> hash, String text) {
		super(bot);
		this.methods = hash;
		this.text = text;
	}
	
	

	/**
	 * Returns selected bot tab name
	 * @return Returns selected bot tab name
	 */
	public String getClickedBotTabName() {
		return text;
	}
	
	/**
	 * Returns selected bot
	 * @return Returns selected bot
	 */
	public mcbot getClickedBot() {
		return storage.getInstance().settin.bots.get(text);
	}
	
	/**
	 * 
	 * @param o Instance of receiver class
	 * @param contextMenuItemName Name of item in context menu
 	 * @param methodName Handling method in receiver class
	 * @return Returns True if successful, False if name already exists or method is not found 
	 */
	public boolean addEntry(Object o,String contextMenuItemName, String methodName) {
		if (methods.containsKey(contextMenuItemName)) {
			return false;
		}
		try {
			methods.put(contextMenuItemName,new ContextMenuItem(o,methodName));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
