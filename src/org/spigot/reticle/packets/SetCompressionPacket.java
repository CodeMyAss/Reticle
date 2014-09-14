package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SetCompressionPacket extends packet {

	public static final int ID = 0x46;
	public static final int ID_out = 0x3;

	private int protocolversion;
	private packet reader;

	public SetCompressionPacket(ByteBuffer buf,packet reader, int protocolversion) {
		this.protocolversion = protocolversion;
		this.reader=reader;
		this.reader.input=buf;
	}

	public int Read() throws SerialException, IOException {
		int treshhold = 0;
		if (protocolversion >= 47) {
			treshhold = reader.readVarInt();
		}
		return treshhold;
	}
	
	public void Write() throws IOException {
		//No compression wanted
		reader.setOutputStream(reader.getVarntCount(-1)+reader.getVarntCount(ID_out));
		reader.writeVarInt(ID_out);
		reader.writeVarInt(-1);
		reader.Send();
	}

}
