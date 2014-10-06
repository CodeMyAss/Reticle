package org.spigot.reticle.coresp;

import java.io.IOException;
import java.util.HashMap;

import org.spigot.reticle.packets.PlayerStreamSetSlotPacket;
import org.spigot.reticle.packets.packet;
import org.spigot.reticle.packets.packetStruct;

public class InventoryCollection {
	public HashMap<Integer, packetStruct> lst = new HashMap<Integer, packetStruct>();

	public void addItem(int slot, packetStruct packet) {
		lst.put(slot, packet);
	}

	public void removeItem(int slot) {
		if (lst.containsKey(slot)) {
			lst.remove(slot);
		}
	}
	
	public void writeItems(packet reader) throws IOException {
		for(packetStruct slot:lst.values()) {
			slot.Send(reader);
		}
	}

	public void sendInventoryReset(packet reader) throws IOException {
		new PlayerStreamSetSlotPacket(null,reader).WriteReset();
	}
	
}
