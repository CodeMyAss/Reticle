package org.spigot.reticle.packets;

import java.io.IOException;

public class HandShakePacket extends packet {
	public static final int ID=0;
	private int protocolversion;
	private packet reader;

	public HandShakePacket(packet reader, int protocolversion) {
		this.reader = reader;
		this.protocolversion=protocolversion;
	}

	public void Write(String ip, int port) throws IOException {
		int vint = 0;
		vint = vint + reader.getVarntCount(ID);
		vint = vint + reader.getVarntCount(this.protocolversion);
		vint = vint + reader.getStringLength(ip);
		vint = vint + reader.getVarntCount(2)+packet.SIZER.SHORT.size; // 2 for short port and 0 for next state
		reader.setOutputStream(vint);
		reader.writeVarInt(ID);
		reader.writeVarInt(this.protocolversion);
		reader.writeString(ip);
		reader.writeShort((short) port);
		reader.writeVarInt(2);
		reader.Send();
	}
}
