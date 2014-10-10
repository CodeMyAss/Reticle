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
	
	/**
	 * Unsafe raw construcotr
	 * @param PacketID
	 * @param PacketLenght
	 * @param Data
	 * @param Packet
	 * @param PacketLengthVarint
	 * @param PacketIDVarint
	 * @param DataLength
	 * @param DataByteBuffer
	 */
	public packetStruct(int PacketID,int PacketLenght, byte[] Data, byte[] Packet, byte[] PacketLengthVarint,byte[] PacketIDVarint, int DataLength, ByteBuffer DataByteBuffer) {
		this.data=Data;
		this.packetID=PacketID;
		this.packetLength=PacketLenght;
		this.packetLengthVarint = PacketLengthVarint;
		this.packet=Packet;
		this.dataLength=DataLength;
		this.packetIDVarint=PacketIDVarint;
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
