package org.spigot.reticle.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class packetStruct {
	public final int packetID;
	public final int packetLength;
	public final byte[] data;
	public final byte[] packet;
	public final byte[] packetLengthVarint;
	public final byte[] packetIDVarint;
	public final int dataLength;

	public ByteBuffer generateBuffer() {
		return ByteBuffer.wrap(data);
	}
	
	protected packetStruct(int packetlength, int packetID, byte[] data, packet reader) throws IOException {
		this.data = data;
		this.packetID = packetID;
		this.packetLength = packetlength;
		this.packetLengthVarint = reader.getVarint(packetlength);
		this.packetIDVarint = reader.getVarint(packetID);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(packetLengthVarint);
		outputStream.write(packetIDVarint);
		outputStream.write(data);
		packet = outputStream.toByteArray();
		dataLength = data.length;
	}

	public void Send(packet reader) throws IOException {
		reader.Send(this);
	}
}
