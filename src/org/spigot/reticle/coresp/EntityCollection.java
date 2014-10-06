package org.spigot.reticle.coresp;

import java.io.IOException;
import java.util.HashMap;

import org.spigot.reticle.packets.PlayerStreamEntityDestroyPacket;
import org.spigot.reticle.packets.packet;
import org.spigot.reticle.packets.packetStruct;

public class EntityCollection {
	private HashMap<Integer, HashMap<Integer, packetStruct>> lst = new HashMap<Integer, HashMap<Integer, packetStruct>>();

	public void reSendEntities(packet reader) throws IOException {
		for (Integer entity : lst.keySet()) {
			HashMap<Integer, packetStruct> ent = lst.get(entity);
			for (packetStruct pack : ent.values()) {
				reader.Send(pack);
			}
			// new PlayerStreamInitEntityPacket(null, reader).Write(entity);
		}

	}

	public void destroyEntities(packet read) throws IOException {
		int len = lst.size();
		int[] entities = new int[len];
		int i = 0;
		for (Integer ent : lst.keySet()) {
			entities[i] = ent;
		}
		new PlayerStreamEntityDestroyPacket(null, read).Write(entities);
	}

	public void update(packetStruct pack, int entity) {
		try {
			if (!lst.containsKey(entity)) {
				lst.put(entity, new HashMap<Integer, packetStruct>());
			}
			lst.get(entity).put(pack.packetID, pack);
		} catch (Exception e) {
		}
	}

	public void remove(packetStruct pack, int[] listing) {
		for (int id : listing) {
			if (lst.containsKey(id)) {
				lst.remove(id);
			} else {
				System.out.println("Entity not found!");
			}
		}
	}
}
