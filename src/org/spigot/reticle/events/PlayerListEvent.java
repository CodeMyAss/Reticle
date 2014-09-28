package org.spigot.reticle.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spigot.reticle.botfactory.mcbot;

public class PlayerListEvent extends Event {
	public final String name;
	public final boolean online;
	public final List<String> UUIDS;
	public final List<String> Nicks;
	public final List<Boolean> Onlines;
	public final List<Boolean> Changed;
	
	public HashMap<String,String> getChangedItemsByUUID() {
		HashMap<String,String> res = new HashMap<String,String>();
		if (name == null) {
			for(int i=0,o=Nicks.size();i<o;i++) {
				if(Changed.get(i)) {
					res.put(UUIDS.get(i),Nicks.get(i));
				}
			}
		}
		return res;
	}
	
	public HashMap<String,String> getNewItemsByUUID() {
		HashMap<String,String> res = new HashMap<String,String>();
		if (name == null) {
			for(int i=0,o=Nicks.size();i<o;i++) {
				if(Onlines.get(i)) {
					res.put(UUIDS.get(i),Nicks.get(i));
				}
			}
		}
		return res;
	}

	public HashMap<String,String> getRemovedItemsByUUID() {
		HashMap<String,String> res = new HashMap<String,String>();
		if (name == null) {
			for(int i=0,o=Nicks.size();i<o;i++) {
				if(!Onlines.get(i)) {
					res.put(UUIDS.get(i),Nicks.get(i));
				}
			}
		}
		return res;
	}
	
	public List<String> getRemovedItemsByNames() {
		List<String> res = new ArrayList<String>();
		if (name == null) {
			for(int i=0,o=Nicks.size();i<o;i++) {
				if(!Onlines.get(i)) {
					res.add(Nicks.get(i));
				}
			}
		} else {
			if (!online) {
				res.add(name);
			}
		}
		return res;
	}
	
	public List<String> getAddedItemsByNames() {
		List<String> res = new ArrayList<String>();
		if (name == null) {
			for(int i=0,o=Nicks.size();i<o;i++) {
				if(Onlines.get(i)) {
					res.add(Nicks.get(i));
				}
			}
		} else {
			if (online) {
				res.add(name);
			}
		}
		return res;
	}

	public boolean newItems() {
		if (name == null) {
			// 1.8
		} else {
			// 1.7
			return online;
		}

		return false;
	}

	public PlayerListEvent(mcbot bot, String xname, boolean xonline, List<String> XUUIDS, List<String> xNicks, List<Boolean> xOnlines, List<Boolean> xChanged) {
		super(bot);
		this.name = xname;
		this.online = xonline;
		this.UUIDS = XUUIDS;
		this.Nicks = xNicks;
		this.Onlines = xOnlines;
		this.Changed = xChanged;

	}

}
