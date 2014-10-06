package org.spigot.reticle.coresp;

public class InventorySlotResponse {
	public final boolean isInventory;
	public final short slotNumber;
	public final boolean isAdded;

	public InventorySlotResponse(byte windowid, short slot, byte[] data) {
		this.isInventory = windowid == 0;
		this.slotNumber=slot;
		if(data.length==2) {
			if(data[0]==data[1]) {
				isAdded=false;
			} else {
				isAdded=true;
			}
		} else {
			isAdded=true;
		}
	}
}
