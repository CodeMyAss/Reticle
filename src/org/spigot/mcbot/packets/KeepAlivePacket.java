package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class KeepAlivePacket extends packet {
	public static final int ID=0;
	
	private Socket sock ;

	public KeepAlivePacket(Socket sock) {
		this.sock =sock;
	}
	
	public byte[] Read(int len) throws IOException {
		super.input=sock.getInputStream();
		return super.readBytes(len);
	}
	
	public void Write(byte[] i) throws IOException {
		super.setOutputStream(super.getVarntCount(KeepAlivePacket.ID)+i.length);
		//Packet id
		super.writeVarInt(KeepAlivePacket.ID);
		super.writeBytes(i);
		super.Send(sock.getOutputStream());
	}
}
