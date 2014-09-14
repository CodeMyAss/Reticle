package org.spigot.reticle.packets;

import java.io.IOException;

public class LoginStartPacket extends packet {
	public static final int ID=0;
	private packet reader;
	
	public LoginStartPacket(packet reader, int protocolversion) {
		this.reader=reader;
	}
	
	
	public void Write(String username) throws IOException {
		reader.setOutputStream(reader.getStringLength(username)+reader.getVarntCount(LoginStartPacket.ID));
		//Packet id
		reader.writeVarInt(LoginStartPacket.ID);
		//Username
		reader.writeString(username);
		reader.Send();
	}
	
}
