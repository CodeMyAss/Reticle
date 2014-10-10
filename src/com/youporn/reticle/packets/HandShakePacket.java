package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class HandShakePacket extends AbstractPacket {
	public static final int ID = 0;
	private packet reader;

	public HandShakePacket(packet reader) {
		this.reader = reader;
	}

	public String ReadStatus() throws SerialException, IOException {
		int[] pack = reader.readNext();
		int len = pack[0];
		int id = pack[1];
		int len2 = len - reader.getVarIntCount(id);
		byte[] b = reader.readInnerBytes(len2);
		ByteBuffer buf = ByteBuffer.wrap(b);
		reader.input = buf;
		if (id == 0) {
			String json = reader.readString();
			return json;
		}
		return null;
	}

	public void Write(String ip, int port) throws IOException {
		Write(ip, port, 2);
	}

	public void Write(String ip, int port, int mode) throws IOException {
		if (mode == 1) {
			byte[] seq = new byte[] {0x0F, 0x00, 0x47, 0x09, 0x31, 0x32, 0x37, 0x2e, 0x30, 0x2e, 0x30, 0x2e, 0x31, 0x63, (byte) 222, 0x01, 0x01, 0x00 };
			reader.setOutputStream(seq);
			reader.writeBytes(seq);
			reader.Send();
		} else {
			int vint = 0;
			vint = vint + reader.getVarIntCount(ID);
			vint = vint + reader.getVarIntCount(reader.ProtocolVersion);
			vint = vint + reader.getStringLength(ip);
			vint = vint + reader.getVarIntCount(2) + packet.SIZER.SHORT.size; // 2
																				// for
																				// short
																				// port
																				// and
																				// 0
																				// for
																				// next
																				// state
			reader.setOutputStream(vint);
			reader.writeVarInt(ID);
			reader.writeVarInt(reader.ProtocolVersion);
			reader.writeString(ip);
			reader.writeShort((short) port);
			reader.writeVarInt(mode);
			reader.Send();
		}
	}
}
