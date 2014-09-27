package org.spigot.reticle;

import java.util.ArrayList;
import java.util.List;

public class helpCommandsKeeper {
	private List<entry> Entries = new ArrayList<entry>();

	public void addEntry(String command, String description, String[] params) {
		if(!CommandExists(command)) {
			Entries.add(new entry(command, description, params));
		}
	}
	
	protected helpCommandsKeeper() {
		
	}
	
	/**
	 * Checks whether or not the command already exists
	 * @param Command
	 * @return True if command exists, false if otherwise
	 */
	public boolean CommandExists(String command) {
		for(entry ent:Entries) {
			if(ent.com.equalsIgnoreCase(command)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return Formated help string
	 */
	public String getHelpString() {
		StringBuilder sb = new StringBuilder();
		for(entry ent:Entries) {
			sb.append("\n§o§6"+ent.com+" "+ storage.implode(" ", ent.params) + "§r §f- "+ent.desc);
		}
		if(Entries.size()==0) {
			sb.append("\n§o");
		}
		return sb.toString().substring(3);
	}

	protected class entry {

		protected final String com;
		protected final String desc;
		protected final String[] params;

		protected entry(String command, String desc, String[] params) {
			this.com = command;
			this.desc = desc;
			this.params=params;
		}
	}
}
