package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SetCompressionPacket extends packet {

	public static final int ID = 0x3;
	public static final int ID2 = 0x46;
	
	public static final int ID_out = 0x3;

	private int protocolversion;
	private packet reader;

	public SetCompressionPacket(ByteBuffer buf,packet reader, int protocolversion) {
		this.protocolversion = protocolversion;
		this.reader=reader;
		this.reader.input=buf;
	}

	public void Read() throws SerialException, IOException {
		int treshhold = 0;
		if (protocolversion >= 47) {
			treshhold = reader.readVarInt();
			reader.Threshold=treshhold;
			reader.compression=true;
		}
	}
	
	public void Write(int tres) throws IOException {
		//No compression wanted
		reader.setOutputStream(reader.getVarntCount(-1)+reader.getVarntCount(ID_out));
		reader.writeVarInt(ID_out);
		reader.writeVarInt(reader.Threshold);
		reader.Send();
		reader.Threshold=tres;
		reader.compression=true;
	}
	
	public void Write() throws IOException {
		Write(reader.Threshold);
	}

}
