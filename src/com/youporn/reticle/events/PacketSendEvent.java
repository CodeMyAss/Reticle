package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.packets.packetStruct;

public class PacketSendEvent extends CancellableEvent {
	private final packetStruct packet;

	public PacketSendEvent(mcbot bot, packetStruct packet) {
		super(bot);
		this.packet=packet;
	}
	
	public packetStruct getPacket() {
		return packet;
	}

}
