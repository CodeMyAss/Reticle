package org.spigot.reticle.events;

import java.util.HashMap;

import org.spigot.reticle.API.ContextMenuItem;
import org.spigot.reticle.botfactory.mcbot;

/**
 * Fired when user right clicks on Player list item
 * @author Encorn
 *
 */
public class PlayerListContextMenuEvent extends Event {
	private HashMap<String, ContextMenuItem> methods = new HashMap<String, ContextMenuItem>();
	private final String text;
	
	public PlayerListContextMenuEvent(mcbot bot, HashMap<String, ContextMenuItem> hash, String text) {
		super(bot);
		this.methods=hash;
		this.text=text;
	}
	
	/**
	 * Returns clicked text
	 * @return Returns clicked text
	 */
	public String getClickedText() {
		return text;
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
