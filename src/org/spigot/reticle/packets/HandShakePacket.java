package org.spigot.reticle.packets;

import java.io.IOException;
import java.net.Socket;

public class HandShakePacket extends packet {
	public Socket sock;
	public static final int ID=0;
	private int protocolversion;

	public HandShakePacket(Socket sock, int protocolversion) {
		this.sock = sock;
		this.protocolversion=protocolversion;
	}

	public void Write(String ip, int port) throws IOException {
		int vint = 0;
		vint = vint + super.getVarntCount(ID);
		vint = vint + super.getVarntCount(this.protocolversion);
		vint = vint + super.getStringLength(ip);
		vint = vint + super.getVarntCount(2)+packet.SIZER.SHORT.size; // 2 for short port and 0 for next state
		super.setOutputStream(vint);
		super.writeVarInt(ID);
		super.writeVarInt(this.protocolversion);
		super.writeString(ip);
		super.writeShort((short) port);
		super.writeVarInt(2);
		super.Send(sock.getOutputStream());
	}
}
