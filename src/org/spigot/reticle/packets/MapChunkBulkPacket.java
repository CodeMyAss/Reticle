package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.coresp.ChunkDataInfo;

public class MapChunkBulkPacket extends AbstractPacket {
	public static final int ID = 0x26;
	private packet reader;
	int x;
	int z;

	public MapChunkBulkPacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}

	public ChunkDataInfo[] Read() throws SerialException, IOException {
		ChunkDataInfo[] data;
		if (reader.ProtocolVersion >= 47) {
			reader.readBoolean();
			int len = reader.readVarInt();
			data=new ChunkDataInfo[len];
			for (int i = 0; i < len; i++) {
				int tx = reader.readInt();
				int tz = reader.readInt();
				data[i]=new ChunkDataInfo(tx,tz,true);
				reader.readShort();
			}
		} else {
			int len=reader.readShort();
			int datalen=reader.readInt();
			reader.readBoolean();
			if(datalen<=0) {
				return null;
			}
			reader.readBytes(datalen);
			data=new ChunkDataInfo[len];
			for (int i = 0; i < len; i++) {
				int tx = reader.readInt();
				int tz = reader.readInt();
				data[i]=new ChunkDataInfo(tx,tz,true);
				reader.readShort();
				reader.readShort();
			}
		}
		return data;
	}
}
