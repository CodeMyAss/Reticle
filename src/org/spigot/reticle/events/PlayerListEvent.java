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
	private final HashMap<String, String> changedbyuuid;
	private final HashMap<String, String> newbyuuid;
	private final HashMap<String, String> removedbyuuid;
	private final List<String> removedbyname;
	private final List<String> newbyname;

	public HashMap<String, String> getChangedItemsByUUID() {
		return this.changedbyuuid;
	}

	public HashMap<String, String> getNewItemsByUUID() {
		return this.newbyuuid;
	}

	public HashMap<String, String> getRemovedItemsByUUID() {
		return this.removedbyuuid;
	}

	public List<String> getRemovedItemsByNames() {
		return this.removedbyname;
	}

	public List<String> getNewItemsByNames() {
		return this.newbyname;
	}

	private HashMap<String, String> gen_getChangedItemsByUUID() {
		int size = UUIDS.size();
		HashMap<String, String> res = new HashMap<String, String>();
		if (name == null) {
			for (int i = 0, o = Nicks.size(); i < o; i++) {
				if (Changed.get(i)) {
					if (size > i) {
						res.put(UUIDS.get(i), Nicks.get(i));
					}
				}
			}
		}
		return res;
	}

	private HashMap<String, String> gen_getNewItemsByUUID() {
		int size = UUIDS.size();
		HashMap<String, String> res = new HashMap<String, String>();
		if (name == null) {
			for (int i = 0, o = Nicks.size(); i < o; i++) {
				if (Onlines.get(i)) {
					if (size > i) {
						res.put(UUIDS.get(i), Nicks.get(i));
					}
				}
			}
		}
		return res;
	}

	private HashMap<String, String> gen_getRemovedItemsByUUID() {
		int size = UUIDS.size();
		HashMap<String, String> res = new HashMap<String, String>();
		if (name == null) {
			for (int i = 0, o = Nicks.size(); i < o; i++) {
				if (!Onlines.get(i)) {
					if (size > i) {
						res.put(UUIDS.get(i), Nicks.get(i));
					}
				}
			}
		}
		return res;
	}

	private List<String> gen_getRemovedItemsByNames() {
		List<String> res = new ArrayList<String>();
		if (name == null) {
			for (int i = 0, o = Nicks.size(); i < o; i++) {
				if (!Onlines.get(i)) {
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

	private List<String> gen_getNewItemsByNames() {
		List<String> res = new ArrayList<String>();
		if (name == null) {
			for (int i = 0, o = Nicks.size(); i < o; i++) {
				if (Onlines.get(i) && !Changed.get(i)) {
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

	public PlayerListEvent(mcbot bot, String xname, boolean xonline, List<String> XUUIDS, List<String> xNicks, List<Boolean> xOnlines, List<Boolean> xChanged) {
		super(bot);
		this.name = xname;
		this.online = xonline;
		if(XUUIDS==null) {
			XUUIDS=new ArrayList<String>();
		}
		this.UUIDS = XUUIDS;
		this.Nicks = xNicks;
		this.Onlines = xOnlines;
		this.Changed = xChanged;
		this.newbyuuid = gen_getNewItemsByUUID();
		this.changedbyuuid = gen_getChangedItemsByUUID();
		this.removedbyuuid = gen_getRemovedItemsByUUID();
		this.removedbyname = gen_getRemovedItemsByNames();
		this.newbyname = gen_getNewItemsByNames();
	}
}
