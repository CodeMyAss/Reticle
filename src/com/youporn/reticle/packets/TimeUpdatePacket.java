package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class TimeUpdatePacket extends AbstractPacket {
	public static final int ID=0x03;
	private packet reader;
	
	public TimeUpdatePacket(ByteBuffer buf, packet reader) {
		this.reader=reader;
		this.reader.input=buf;
	}
	
	public void Read() throws SerialException, IOException {
		//Age of world
		reader.readLong();
		// Time of day
		reader.readLong();
	}
}
