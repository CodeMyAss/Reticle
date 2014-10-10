package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SetCompressionPacket extends AbstractPacket {

	public static final int ID = 0x3;
	public static final int ID2 = 0x46;
	
	public static final int ID_out = 0x3;

	private packet reader;

	public SetCompressionPacket(ByteBuffer buf,packet reader) {
		this.reader=reader;
		this.reader.input=buf;
	}

	public void Read() throws SerialException, IOException {
		int treshhold = 0;
		if (reader.ProtocolVersion >= 47) {
			treshhold = reader.readVarInt();
			reader.Threshold=treshhold;
			reader.compression=true;
		}
	}
	
	public void Write(int tres) throws IOException {
		//No compression wanted
		reader.setOutputStream(reader.getVarIntCount(-1)+reader.getVarIntCount(ID_out));
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
