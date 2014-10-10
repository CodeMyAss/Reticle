package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class DestroyEntitiesPacket extends AbstractPacket {
	public static final int ID=0x13;
	private packet reader;

	public DestroyEntitiesPacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}

	public int[] Read() throws SerialException, IOException {
		if (reader.ProtocolVersion >= 47) {
			int count = reader.readVarInt();
			int[] ar=new int[count];
			for(int i=0;i<count;i++) {
				ar[i]=reader.readVarInt();
			}
			return ar;
		} else {
			byte count = reader.readByte();
			int[] ar=new int[count];
			for(int i=0;i<count;i++) {
				ar[i]=reader.readInt();
			}
			return ar;
		}
	}

}
