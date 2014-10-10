package org.spigot.reticle.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.botfactory.mcbot;

public class packet {
	protected ByteBuffer input;
	private ByteBuffer output;
	protected int version = 4;
	private InputStream sockinput;
	public static int MAXPACKETID = 0x40;
	public List<Integer> ValidPackets = new ArrayList<Integer>();
	private OutputStream sockoutput;
	private boolean encrypted = false;
	protected int Threshold = 0;
	public boolean compression;
	public int ProtocolVersion;
	public final mcbot bot;

	public enum SIZER {
		BOOLEAN(1), BYTE(1), SHORT(2), INT(4), LONG(8), FLAT(4), DOUBLE(8);
		public int size;

		SIZER(int siz) {
			this.size = siz;
		}
	}

	/**
	 * Very unsafe to use. Never use this!
	 * 
	 * @param buf
	 *            ByteBuffer to set as input stream
	 */
	public void OverrideInput(ByteBuffer buf) {
		this.input = buf;
	}

	public void setEncryptedStreams(InputStream cis, OutputStream cos) {
		this.sockinput = cis;
		this.sockoutput = cos;
	}

	public void setEncrypted() {
		this.encrypted = true;
	}

	public packet(mcbot bot) {
		this.bot = bot;
	}

	public boolean isEncrypted() {
		return this.encrypted;
	}

	public packet(mcbot bot, InputStream inputStream, OutputStream outputs) {
		this.bot = bot;
		this.sockinput = inputStream;
		this.sockoutput = outputs;
	}

	public packet(mcbot bot, InputStream inputStream) {
		this.sockinput = inputStream;
		this.bot = bot;
	}

	public packet(mcbot bot, int len, ByteBuffer input) throws IOException {
		this.bot = bot;
		this.input = input;
		int vcount = getVarIntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		this.output.order(ByteOrder.BIG_ENDIAN);
		writeVarInt(len);
	}

	public String readUUID() throws SerialException, IOException {
		return this.readLong() + "" + this.readLong();
	}

	public int[] readNext() throws IOException, SerialException {
		int[] res = new int[2];
		// The length of the packet
		res[0] = readInnerVarInt();
		// Id of packet
		res[1] = readInnerVarInt();
		return res;
	}

	public packetStruct readNexter() throws IOException, SerialException, DataFormatException {
		packetStruct struct;
		if (this.compression) {
			struct = readNextCompressed();
		} else {
			// Length of packet
			int len = readInnerVarInt();
			// Id of packet
			int id = readInnerVarInt();
			// Packet data
			byte[] data = readInnerBytes(len - this.getVarIntCount(id));
			struct = new packetStruct(len, id, data, this);
		}
		return struct;
	}

	public byte[] readInnerBytes(int len) throws SerialException, IOException {
		byte[] b = new byte[len];
		int read = 0;
		do {
			read += this.sockinput.read(b, read, len - read);
		} while (read < len);
		return b;
	}

	public int getCompressedLen(byte[] packer) {
		return packer.length;
	}

	public int getCompressedID(byte[] packer) throws SerialException, IOException {
		return packet.readCompressedVarInt(packer, 0);
	}

	protected int getCompressedPacketLength(byte[] packer) throws SerialException, IOException {
		return packet.readCompressedVarInt(packer, 0);
	}

	protected byte[] shiftArray(byte[] ar, int length) {
		return Arrays.copyOfRange(ar, length, ar.length);
	}

	private packetStruct readNextCompressed() throws SerialException, IOException, DataFormatException {
		packetStruct struct;
		byte[] data;
		int packetstructid;
		int packetstructlen;
		int plen = readInnerVarInt();
		if (plen < 0) {
			System.err.println("Fatal compression error");
			return null;
		}
		int len = readInnerVarInt();
		byte[] newdata;
		if (len == 0) {
			packetstructid = this.readInnerVarInt();
			int pidlen = this.getVarIntCount(packetstructid);
			newdata = this.readInnerBytes(plen - 1 - pidlen);
			packetstructlen = newdata.length + pidlen;
		} else {
			int purelength = getVarIntCount(len);
			int reslen = plen - purelength;
			byte[] out = this.readInnerBytes(reslen);
			Inflater decompresser = new Inflater();
			decompresser.setInput(out, 0, reslen);
			byte[] result = new byte[len];
			try {
				decompresser.inflate(result);
				decompresser.end();
				data = result;
			} catch (DataFormatException e) {
				System.err.println("Compression error (" + len + ")");
				return null;
			}
			packetstructid = getCompressedID(data);
			packetstructlen = data.length;
			newdata = Arrays.copyOfRange(data, getVarIntCount(packetstructid), packetstructlen);
		}
		struct = new packetStruct(packetstructlen, packetstructid, newdata, this);
		return struct;
	}

	public byte[] readArray() throws Exception {
		byte len = readByte();
		if (len >= Byte.MAX_VALUE) {
			throw new Exception("Byte array error (" + len + " <= " + Short.MAX_VALUE + ")");
		}
		byte[] ret = readBytes(len);
		return ret;
	}

	/*
	 * public byte[] readInnerBytes1(int len) { byte[] b=new byte[len];
	 * if(encrypted) { cis.readFully(b, 0, len); } else {
	 * sockinput.read(b,0,len); } return b; }
	 */
	public byte[] readBytes(int len) throws IOException {
		byte[] b = new byte[len];
		this.input.get(b);
		return b;
	}

	public byte[] readLegacyArray() throws SerialException, IOException {
		return readBytes(readVarInt());
	}

	public byte[] readInnerLegacyArray() throws SerialException, IOException {
		return readInnerBytes(readInnerVarInt());
	}

	public void writeBytes(byte[] b) {
		this.output.put(b);
	}

	public int getStringLength(String s) throws IOException {
		return s.getBytes("UTF-8").length + (getVarIntCount(s.getBytes("UTF-8").length));
	}

	public void Send() throws IOException {
		if(output.position()!=output.limit()) {
			System.err.println("Buffer underflow");
			new Exception().printStackTrace();
		}
		output.position(0);
		if (compression) {
			// We should compress the packet
			byte[] compresspacket = null;
			int packtotallen = 0;
			int uncompressedlen = output.array().length;
			if (uncompressedlen >= Threshold) {
				// Compression required
				compresspacket = compressPacket(output.array());
			} else {
				// Compression not required
				compresspacket = output.array();
				uncompressedlen = 0;
			}
			packtotallen = this.getVarIntCount(uncompressedlen) + compresspacket.length;
			sockoutput.write(this.getVarint(packtotallen));
			sockoutput.write(this.getVarint(uncompressedlen));
			sockoutput.write(compresspacket);
		} else {
			sockoutput.write(output.array());
		}
	}

	static final String HEXES = "0123456789ABCDEF";

	public String tohex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F))).append(" ");
		}
		return hex.toString();
	}

	private byte[] compressPacket(byte[] input) {
		Deflater compresser = new Deflater();
		compresser.setInput(input, 0, input.length);
		byte[] result = new byte[input.length];
		int realen = compresser.deflate(result);
		compresser.end();
		return Arrays.copyOfRange(result, 0, realen);
	}

	protected void setOutputStream(int len) throws IOException {
		if (compression) {
			this.output = ByteBuffer.allocate(len);
			this.output.position(0);
		} else {
			int vcount = getVarIntCount(len);
			this.output = ByteBuffer.allocate(len + vcount);
			writeVarInt(len);
		}
	}

	protected void readAndIgnore(int length) throws IOException, SerialException {
		this.readInnerBytes(length);
	}

	private final int readInnerVarInt() throws SerialException, IOException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			int ir = this.sockinput.read();
			if (ir == -1) {
				throw new SerialException();
			} else {
				in = (byte) ir;
			}
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;

		/*
		 * int value = 0; int i = 0; int b; while (((b = this.sockinput.read())
		 * & 0x80) != 0) { value |= (b & 0x7F) << i; i += 7; if (i > 35) { throw
		 * new IllegalArgumentException("Variable length quantity is too long");
		 * } } return value | (b << i);
		 */
	}

	public static int readStaticVarInt(byte[] byter, int pos) throws IOException, SerialException {
		int out = 0;
		int bytes = 0;
		byte in;
		int i = pos - 1;
		while (true) {
			i++;
			in = byter[i];
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	protected static int readCompressedVarInt(byte[] byter, int pos) throws IOException, SerialException {
		int out = 0;
		int bytes = 0;
		byte in;
		int i = pos - 1;
		while (true) {
			i++;
			in = byter[i];
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	protected int readVarInt() throws IOException, SerialException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = readByte();
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new RuntimeException("VarInt too big");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;

		/*
		 * int value = 0; int i = 0; int b; while (((b = readByte()) & 0x80) !=
		 * 0) { value |= (b & 0x7F) << i; i += 7; if (i > 35) { throw new
		 * IllegalArgumentException("Variable length quantity is too long"); } }
		 * return value | (b << i);
		 */
	}

	protected int readInt() throws IOException, SerialException {
		return (readByte() << 24) + (readByte() << 16) + (readByte() << 8) + readByte();

	}

	protected void writeInt(int i) throws IOException {
		output.putInt(i);
	}

	protected byte readByte() throws IOException, SerialException {
		try {
			int byter = input.get();
			return (byte) byter;
		} catch (BufferUnderflowException e) {
		}
		return 0;
	}

	protected void writeByte(byte b) throws IOException {
		output.put((byte) b);
	}

	protected short readShort() throws IOException {
		return input.getShort();
	}

	protected String readInnerString() throws IOException, SerialException {
		int len = readInnerVarInt();
		if (len > 10240) {
			System.err.println("Can't read " + len);
			new IOException().printStackTrace();
			throw new IOException();
		}
		byte[] b = new byte[len];
		sockinput.read(b, 0, len);
		return new String(b, "UTF-8");
	}

	protected String readString() throws IOException, SerialException {
		int len = readVarInt();
		if (len > 32000) {
			System.err.println("Can't read " + len + " Encryption: " + this.encrypted);
			throw new IOException();
		}
		byte[] b = new byte[len];
		try {
			input.get(b);
		} catch (BufferUnderflowException e) {
			// Should never happen again
		}
		return new String(b, "UTF-8");
	}

	protected void writeString(String str) throws IOException {
		byte[] utfstr = str.getBytes("UTF-8");
		writeVarInt(utfstr.length);
		output.put(str.getBytes("UTF-8"));
	}

	protected void writeShort(short b) throws IOException {
		output.putShort(b);
	}

	protected long readLong() throws IOException, SerialException {
		return input.getLong();
	}

	protected void writeLong(long l) throws IOException {
		output.putLong(l);
	}

	protected boolean readBoolean() throws IOException, SerialException {
		int i = readByte();
		return (i != 0);
	}

	protected void writeBoolean(boolean b) throws IOException {
		if (b) {
			writeByte((byte) 1);
		} else {
			writeByte((byte) 0);
		}
	}

	protected float readFloat() throws IOException {
		return input.getFloat();
	}

	protected void writeFloat(float f) throws IOException {
		output.putFloat(f);
	}

	protected double readDouble() throws IOException {
		return input.getDouble();
	}

	protected void writeDouble(double d) throws IOException {
		output.putDouble(d);
	}

	protected byte[] getVarint(int value) {
		if (value == 0) {
			return new byte[] { 0 };
		}
		int part;
		byte[] res = new byte[getVarIntCount(value)];
		int i = -1;
		while (true) {
			i++;
			part = value & 0x7F;
			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}
			res[i] = ((byte) part);
			if (value == 0) {
				break;
			}
		}
		return res;
	}

	protected void writeVarInt(int value) throws IOException {
		if (value == 0) {
			writeByte((byte) 0);
			return;
		}
		int part;
		while (true) {
			part = value & 0x7F;
			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}
			output.put((byte) part);
			if (value == 0) {
				break;
			}
		}
	}

	public int getVarIntCount(int value) {
		int i = 0;
		while (true) {
			value >>>= 7;
			i++;
			if (value == 0) {
				break;
			}
		}
		return i;
	}

	public void setOutputStream(byte[] seq) throws IOException {
		output = ByteBuffer.wrap(seq);
	}

	public void Send(packetStruct packet) throws IOException {
		this.output = ByteBuffer.wrap(packet.packet);
		this.output.position(this.output.limit());
		this.Send();
	}
}
