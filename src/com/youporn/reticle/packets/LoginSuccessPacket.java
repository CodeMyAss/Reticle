package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.LoginSuccessEvent;

public class LoginSuccessPacket extends AbstractPacket {
	public static final int ID = 0x02;
	private packet reader;

	public LoginSuccessPacket(ByteBuffer buf, packet reader) throws IOException {
		this.reader = reader;
		this.reader.input = buf;
	}

	public LoginSuccessEvent Read() throws IOException, SerialException {
		// UUID
		String uuid = reader.readString();
		//String uuid="???";
		// Username
		String username = reader.readString();
		//String username="Skipped";
		return new LoginSuccessEvent(reader.bot,username,uuid);
	}
}
