package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class KeepAlivePacket extends packet {

	
	private Socket sock ;

	public KeepAlivePacket(Socket sock) {
		this.sock =sock;
	}
	
	public byte[] Read(int len) throws IOException {
		super.input=sock.getInputStream();
		//Protocol neutral (LOL)
		return super.readBytes(len);
	}
	
	public void Write(byte[] i) throws IOException {
		super.setOutputStream(super.getVarntCount(0)+i.length);
		//Packet id
		super.writeVarInt(0);
		//Keep alive data - Protocol neutral (LOL)
		super.writeBytes(i);
		super.Send(sock.getOutputStream());
	}
}
