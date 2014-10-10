package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.packets.packetStruct;

public class PacketReceiveEvent extends CancellableEvent {
	private final packetStruct packet;
	private final boolean login;

	public PacketReceiveEvent(mcbot bot, packetStruct packet, boolean isLoginPacket) {
		super(bot);
		this.packet=packet;
		this.login=isLoginPacket;
	}
	
	public boolean isLoginPacket() {
		return login;
	}
	
	public packetStruct getPacket() {
		return packet;
	}

}
