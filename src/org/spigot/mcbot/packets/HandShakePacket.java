package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class HandShakePacket extends packet {
	public String ip;
	public int port;
	public String username;
	public Socket sock;

	public HandShakePacket(String ip, int port, String username, Socket sock) throws IOException {
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.sock = sock;
		super.writeVarInt(0);
		super.writeVarInt(0);
		super.writeVarInt(version);
		super.writeString(ip);
		writeShort((short) (port & 0xFFFF));
		writeVarInt(2);
	}

	public void Write() throws IOException {
		int vint = 0;
		vint += super.getVarntCount(super.version);
		vint += super.getVarntCount(ip.length());
		vint += super.getVarntCount(3); // 2 for short and 1 for login
		super.setOutputStream(vint);
		super.writeVarInt(super.version);
		super.writeString(ip);
		super.writeShort((short)port);
		super.writeVarInt(2);
		super.Send(sock.getOutputStream());
	}
}
