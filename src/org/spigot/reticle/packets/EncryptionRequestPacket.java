package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sql.rowset.serial.SerialException;

public class EncryptionRequestPacket extends packet {
	public static final int ID = 0x01;
	private packet reader;
	private byte[] secret;
	private byte[] verify;
	private byte[] mykey;

	public EncryptionRequestPacket(ByteBuffer buf, packet reader) {
		this.reader = reader;
		this.reader.input = buf;
	}

	public byte[] getSecret() {
		return mykey;
	}

	public void Read() throws SerialException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeySpecException {
		// Server id
		reader.readString();
		// Length of public key
		int pkl = reader.readVarInt();
		// Public key
		secret = reader.readBytes(pkl);
		// Length of verify token
		int vtl = reader.readVarInt();
		// Verify token
		verify = reader.readBytes(vtl);
		// secret = javaHexDigest(new String(pk)).getBytes();
		mykey = new byte[] { 0x1, 0x2, 0x3, 0x3, 0x3, 0x2, 0x3, 0x3, 0x2, 0x4, 0x3, 0x8, 0x3, 0x4, 0x3, 0x9 };
		Key key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(secret));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		verify = cipher.doFinal(verify);
		secret = cipher.doFinal(mykey);
	}

	public void Write() throws IOException {
		int len1 = secret.length + reader.getVarntCount(secret.length);
		int len2 = verify.length + reader.getVarntCount(verify.length);
		int len3 = reader.getVarntCount(1);
		reader.setOutputStream(len1 + len2 + len3);
		// Packet ID
		reader.writeVarInt(1);
		// shared secret length
		reader.writeVarInt(secret.length);
		// shared secret
		reader.writeBytes(secret);
		// shared verify length
		reader.writeVarInt(verify.length);
		// verify
		reader.writeBytes(verify);
		reader.Send();
	}
}