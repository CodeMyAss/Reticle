package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class KeepAlivePacket extends packet {

	
	private Socket sock ;

	public KeepAlivePacket(Socket sock) {
		this.sock =sock;
	}
	
	public int Read() throws IOException {
		super.input=sock.getInputStream();
		//int resp=super.readVarInt();
		int resp=super.readByte();
		System.out.println("FOUND: "+resp);
		return resp;
	}
	
	public void Write(int i) throws IOException {
		super.setOutputStream(super.getVarntCount(i)+1);
		super.writeVarInt(i);
		super.Send(sock.getOutputStream());
	}
}
