package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.PlayerPositionAndLookEvent;

public class PlayerPositionAndLookPacket extends AbstractPacket {
	public static final int ID=0x08;
	private packet reader;

	public PlayerPositionAndLookPacket(packet reader, ByteBuffer buf) {
		this.reader=reader;
		this.reader.input=buf;
	}
	
	public PlayerPositionAndLookEvent Read() throws IOException, SerialException {
		double x=reader.readDouble();
		double y=reader.readDouble();
		double z=reader.readDouble();
		float yaw=reader.readFloat();
		float pitch=reader.readFloat();
		byte flags=reader.readByte();
		return new PlayerPositionAndLookEvent(reader.bot,x, y, z, pitch, yaw, flags);
	}
}
