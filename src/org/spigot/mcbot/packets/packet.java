package org.spigot.mcbot.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class packet {
	protected InputStream input;
	private ByteBuffer output;
	protected int version = 4;
	public static int MAXPACKETID = 64;
	public static List<Integer> ValidPackets = new ArrayList<Integer>(Arrays.asList(0, 2, 64));

	public enum SIZER {
		BOOLEAN(1), BYTE(1), SHORT(2), INT(4), LONG(8), FLAT(4), DOUBLE(8);
		public int size;

		SIZER(int siz) {
			this.size = siz;
		}
	}

	public packet() {

	}

	public packet(InputStream input) {
		this.input = input;
	}

	public packet(int len, InputStream input) throws IOException {
		// sock = s;
		this.input = input;
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		this.output.order(ByteOrder.BIG_ENDIAN);
		writeVarInt(len);
		// this.output=s.getOutputStream();
	}

	/*
	 * public byte[] read() throws IOException { byte[] b = new byte[len];
	 * input.read(b, 0, len); return b; }
	 */

	public int[] readNext() throws IOException {
		int[] res = new int[2];
		// The length of the packet
		res[0] = readVarInt();
		// Id of packet
		res[1] = readVarInt();
		return res;
	}
	
	public byte[] readBytes(int len) throws IOException {
		byte[] b=new byte[len];
		this.input.read(b,0,len);
		return b;
	}
	
	public void writeBytes(byte[] b) {
		this.output.put(b);
	}
	
	public int getStringLength(String s) throws IOException {
		return s.getBytes("UTF-8").length + (getVarntCount(s.getBytes("UTF-8").length));
	}

	public void Send(OutputStream sockoutput) throws IOException {
		output.position(0);
		if(output.array().length==3) {
			byte[] arr=output.array();
			System.out.println("B0: "+arr[0]);
			System.out.println("B1: "+arr[1]);
			System.out.println("B2: "+arr[2]);
		}
		sockoutput.write(output.array());
	}

	protected void setOutputStream(int len) throws IOException {
		int vcount = getVarntCount(len);
		this.output = ByteBuffer.allocate(len + vcount);
		writeVarInt(len);
	}

	protected void readAndIgnore(int length) throws IOException {
		input.skip(length);
	}

	protected int readVarInt() throws IOException {
		/*
		 * int out = 0; int bytes = 0; byte in; while (true) { in = readByte();
		 * out |= (in & 0x7F) << (bytes++ * 7); if (bytes > 6) { throw new
		 * RuntimeException("VarInt too big"); } if ((in & 0x80) != 0x80) {
		 * break; } } return out;
		 */

		int value = 0;
		int i = 0;
		int b;
		while (((b = readByte()) & 0x80) != 0) {
			value |= (b & 0x7F) << i;
			i += 7;
			if (i > 35) {
				throw new IllegalArgumentException("Variable length quantity is too long");
			}
		}
		return value | (b << i);

	}

	protected int readInt() throws IOException {
		return (readByte() << 24) + (readByte() << 16) + (readByte() << 8) + readByte();

	}

	protected void writeInt(int i) throws IOException {
		output.putInt(i);
	}

	protected byte readByte() throws IOException {
		int byter = input.read();
		if (byter == -1) {
			throw new IOException();
		}
		return (byte) byter;
	}

	protected void writeByte(byte b) throws IOException {
		output.put((byte) b);
	}

	protected short readShort() throws IOException {
		return (short) ((readByte() << 4) + readByte());
	}

	protected String readString() throws IOException {
		int len = readVarInt();
		byte[] b = new byte[len];
		input.read(b, 0, len);
		return new String(b,"UTF-8");
	}

	protected void writeString(String str) throws IOException {
		byte[] utfstr=str.getBytes("UTF-8");
		writeVarInt(utfstr.length);
		output.put(str.getBytes("UTF-8"));
	}

	protected void writeShort(short b) throws IOException {
		output.put(((byte) (b >> 1 * 8)));
		output.put((byte) (b & 0xff));
	}

	protected long readLong() throws IOException {
		return (((long) readInt()) << 16) + ((long) readInt());
	}

	protected void writeLong(long l, OutputStream output) throws IOException {
		writeInt((int) l >> 4 * 8);
		writeInt((int) l & 0xFFFFFFFF);
	}

	protected boolean readBoolean() throws IOException {
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
		if(value==0) {
			writeByte((byte)0);
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
