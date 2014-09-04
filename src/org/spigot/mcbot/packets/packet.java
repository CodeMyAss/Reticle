package org.spigot.mcbot.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class packet {
	protected InputStream input;
	private ByteBuffer output;
	protected int version = 4;
	public static List<Integer> ValidPackets = new ArrayList<Integer>(Arrays.asList(0,1,2));

	public packet() {

	}
	
	public packet(int len) {
		
	}

	public packet(int len, InputStream input) throws IOException {
		// sock = s;
		this.input = input;
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		writeVarInt(len);
		// this.output=s.getOutputStream();
	}

	/*
	 * public byte[] read() throws IOException { byte[] b = new byte[len];
	 * input.read(b, 0, len); return b; }
	 */

	public void Send(OutputStream sockoutput) throws IOException {
		sockoutput.write(output.array());
	}
	
	protected void setOutputStream(int len) throws IOException {
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		writeVarInt(len);
	}

	protected void readAndIgnore(int length) throws IOException {
		input.read(new byte[length],0,length);
	}
	
	protected int readVarInt() throws IOException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = (byte) input.read();
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

	protected int readInt() throws IOException {
		return (input.read() << 12) + (input.read() << 8) + (input.read() << 4) + input.read();

	}

	protected void writeInt(int i) throws IOException {
		output.put((byte) ((i & 0xff000000) >> 12));
		output.put((byte) ((i & 0x00ff0000) >> 8));
		output.put((byte) ((i & 0x0000ff00) >> 4));
		output.put((byte) ((i & 0x000000ff)));
	}

	protected byte readByte() throws IOException {
		return (byte) input.read();
	}

	protected void writeByte(byte b) throws IOException {
		output.put((byte) b);
	}

	protected short readShort() throws IOException {
		return (short) ((input.read() << 4) + input.read());
	}

	protected String readString() throws IOException {
		int len = readVarInt();
		byte[] b = new byte[len];
		input.read(b, 0, len);
		return new String(b);
	}

	protected void writeString(String str) throws IOException {
		int len = str.length();
		writeVarInt(len);
		output.put(str.getBytes());
	}

	protected void writeShort(short b) throws IOException {
		output.put(((byte) (b >> 2)));
		output.put((byte) (b & 0xff));
	}

	protected long readLong() throws IOException {
		return (((long) readInt()) << 16) + ((long) readInt());
	}

	protected void writeLong(long l, OutputStream output) throws IOException {
		writeInt((int) l >> 16);
		writeInt((int) l & 0xFFFFFFF);
	}

	protected boolean readBoolean() throws IOException {
		int i = input.read();
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
		return new DataInputStream(input).readFloat();
	}

	protected void writeFloat(float f) throws IOException {
		output.putFloat(f);
		// new DataOutputStream(output).writeFloat(f);
	}

	protected double readDouble() throws IOException {
		return new DataInputStream(input).readDouble();
	}

	protected void writeDouble(double d) throws IOException {
		output.putDouble(d);
		// new DataOutputStream(output).writeDouble(d);
	}

	protected void writeVarInt(int value) throws IOException {
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

	protected int getVarntCount(int value) throws IOException {
		int i = 0;
		while (true) {
			value >>>= 7;
			if (value != 0) {
			}
			i++;
			if (value == 0) {
				break;
			}
		}
		return i;
	}
}
