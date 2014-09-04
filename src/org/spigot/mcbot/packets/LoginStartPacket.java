package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class LoginStartPacket extends packet {
	private Socket sock;
	
	public LoginStartPacket(Socket sock) {
		this.sock=sock;
	}
	
	
	public void Write(String username) throws IOException {
		super.setOutputStream(super.getStringLength(username)+super.getVarntCount(0));
		//Packet id
		super.writeVarInt(0);
		//Username
		super.writeString(username);
		super.Send(sock.getOutputStream());
	}
	
}
