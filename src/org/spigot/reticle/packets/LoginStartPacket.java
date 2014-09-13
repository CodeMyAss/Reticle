package org.spigot.reticle.packets;

import java.io.IOException;
import java.net.Socket;

public class LoginStartPacket extends packet {
	public static final int ID=0;
	private Socket sock;
	
	public LoginStartPacket(Socket sock) {
		this.sock=sock;
	}
	
	
	public void Write(String username) throws IOException {
		super.setOutputStream(super.getStringLength(username)+super.getVarntCount(LoginStartPacket.ID));
		//Packet id
		super.writeVarInt(LoginStartPacket.ID);
		//Username
		super.writeString(username);
		super.Send(sock.getOutputStream());
	}
	
}
