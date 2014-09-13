package org.spigot.reticle.packets;

import java.io.IOException;
import java.net.Socket;

public class HandShakePacket extends packet {
	public Socket sock;
	public static final int ID=0;

	public HandShakePacket(Socket sock) {
		this.sock = sock;
	}

	public void Write(String ip, int port) throws IOException {
		int vint = 0;
		vint = vint + super.getVarntCount(0);
		vint = vint + super.getVarntCount(super.version);
		vint = vint + super.getStringLength(ip);
		vint = vint + super.getVarntCount(2)+packet.SIZER.SHORT.size; // 2 for short port and 0 for next state
		super.setOutputStream(vint);
		super.writeVarInt(0);
		super.writeVarInt(super.version);
		super.writeString(ip);
		super.writeShort((short) port);
		super.writeVarInt(2);
		super.Send(sock.getOutputStream());
	}
}
