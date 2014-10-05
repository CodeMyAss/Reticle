package org.spigot.reticle.coresp;

import java.io.IOException;
import java.util.HashMap;

import org.spigot.reticle.packets.packet;
import org.spigot.reticle.packets.packetStruct;

public class ChunkCollection {
	private HashMap<Integer, HashMap<Integer, packetStruct>> lst = new HashMap<Integer, HashMap<Integer, packetStruct>>();

	public void add(int x, int z, packetStruct packet) {
		if (!lst.containsKey(x)) {
			lst.put(x, new HashMap<Integer, packetStruct>());
		}
		if (!lst.get(x).containsKey((z))) {
			lst.get(x).put(z, packet);
		} else {
			lst.get(x).put(z, packet);
		}
	}

	public void remove(int x, int z) {
		if (lst.containsKey(x)) {
			if (lst.get(x).containsKey(z)) {
				lst.get(x).remove(z);
				if (lst.get(x).size() == 0) {
					lst.remove(x);
				}
			}
		}
	}

	public void reSendChunks(packet reader) throws IOException {
		for (Integer x : lst.keySet()) {
			HashMap<Integer, packetStruct> chunk = lst.get(x);
			for (packetStruct pack : chunk.values()) {
				reader.Send(pack);
			}
		}
	}
}
