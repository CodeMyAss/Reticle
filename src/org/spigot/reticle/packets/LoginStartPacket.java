package org.spigot.reticle.packets;

import java.io.IOException;

public class LoginStartPacket extends AbstractPacket {
	public static final int ID=0;
	private packet reader;
	
	public LoginStartPacket(packet reader) {
		this.reader=reader;
	}
	
	
	public void Write(String username) throws IOException {
		reader.setOutputStream(reader.getStringLength(username)+reader.getVarIntCount(LoginStartPacket.ID));
		//Packet id
		reader.writeVarInt(LoginStartPacket.ID);
		//Username
		reader.writeString(username);
		reader.Send();
	}
	
}
