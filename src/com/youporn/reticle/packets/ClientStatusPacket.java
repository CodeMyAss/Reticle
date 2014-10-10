package org.spigot.reticle.packets;

import java.io.IOException;

public class ClientStatusPacket extends AbstractPacket {
	private static final int ID_out = 0x16;
	private packet reader;

	public ClientStatusPacket(packet reader) {
		this.reader = reader;
	}

	public void Write(CLIENT_STATUS stat) throws IOException {
		byte stid = stat.id;
		reader.setOutputStream(reader.getVarIntCount(ID_out)+1);
		reader.writeVarInt(ID_out);
		reader.writeByte(stid);
		reader.Send();
	}
	
	public enum CLIENT_STATUS {
		PERFORM_RESPAWN(0), REQUEST_STATS(1), OPEN_INV_ACHIEV(2);
		public byte id;
		CLIENT_STATUS(int id) {
			this.id = (byte)id;
		}
	};

}

