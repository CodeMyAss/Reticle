package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.coresp.InventorySlotResponse;

public class PlayerStreamSetSlotPacket extends AbstractPacket {
	public static final int ID=0x2f;
	private packet reader;

	public PlayerStreamSetSlotPacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}
	
	public InventorySlotResponse Read() throws SerialException, IOException {
		byte wid=reader.readByte();
		short slot=reader.readShort();
		int datalen=reader.input.array().length-3;
		byte[] data=reader.readBytes(datalen);
		return new InventorySlotResponse(wid,slot,data);
	}
	
	public void WriteReset() throws IOException {
		int max=45;
		byte[] empty=new byte[]{(byte) 0xff,(byte) 0xff};
		int packlen=5;
		for(int i=0;i<max;i++) {
			reader.setOutputStream(packlen);
			reader.writeVarInt(ID);
			reader.writeByte((byte) 0);
			reader.writeBytes(empty);
			reader.Send();
		}
	}
}
