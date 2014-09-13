package org.spigot.reticle.packets;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class KeepAlivePacket extends packet {
	public static final int ID = 0;

	private Socket sock;
	private ByteBuffer buff;

	public KeepAlivePacket(Socket sock, ByteBuffer buff) {
		this.sock = sock;
		this.buff = buff;
	}

	public byte[] Read(int len) throws IOException {
		super.input = buff;
		return super.readBytes(len);
	}

	public void Write(byte[] i) throws IOException {
		super.setOutputStream(super.getVarntCount(KeepAlivePacket.ID) + i.length);
		// Packet id
		super.writeVarInt(KeepAlivePacket.ID);
		super.writeBytes(i);
		super.Send(sock.getOutputStream());
	}
}
