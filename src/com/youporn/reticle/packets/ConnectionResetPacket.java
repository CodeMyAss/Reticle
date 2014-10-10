package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.ConnectionResetEvent;



public class ConnectionResetPacket extends AbstractPacket {
	private packet reader;
	public static final int ID=0x40;
	public static final int ID2=0x0;
	
	public ConnectionResetPacket(ByteBuffer s,packet reader) {
		reader.input=s;
		this.reader=reader;
	}
	
	public ConnectionResetEvent Read() throws IOException, SerialException {
		return new ConnectionResetEvent(reader.bot,reader.readString());
	}
}
