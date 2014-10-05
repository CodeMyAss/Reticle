package org.spigot.reticle.packets;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.Authenticator;

public class EncryptionRequestPacket extends AbstractPacket {
	public static final int ID = 0x01;
	private packet reader;
	private byte[] verify;
	private String serverId;
	private byte[] sharedSecret;
	private SecretKey sharedKey;

	public EncryptionRequestPacket(ByteBuffer buf, packet reader) {
		this.reader = reader;
		this.reader.input = buf;
	}

	public SecretKey getSecret() {
		return sharedKey;
	}

	public void Read() throws SerialException, IOException, BufferUnderflowException {
		// Server id
		String serverid = reader.readString();
		// Length of public key
		int pkl;
		if (reader.ProtocolVersion >= 47) {
			pkl = reader.readVarInt();
		} else {
			pkl = reader.readShort();
		}
		// Public key
		byte[] publicKeyBytes = reader.readBytes(pkl);
		// Length of verify token
		int vtl;
		if (reader.ProtocolVersion >= 47) {
			vtl = reader.readVarInt();
		} else {
			vtl = reader.readShort();
		}
		// Verify token
		verify = reader.readBytes(vtl);
		// Shared secret

		PublicKey publicKey = decodekey(publicKeyBytes);
		sharedKey = newkey();
		sharedSecret = encryptdata(publicKey, sharedKey.getEncoded());
		verify = encryptdata(publicKey, verify);
		serverId = (new BigInteger(getserverid(serverid.trim(), publicKey, sharedKey))).toString(16);
	}

	public void Write(mcbot bot) throws IOException {
		// First of all, lets send verifycation to mojang that we are going
		// online
		String username = bot.getMUsername();
		String access = bot.getAccessToken();
		String id = serverId;
		if (bot.isOnlineMode()) {
			if (id != null && access != null) {
				Authenticator auth = Authenticator.forJoinPurpose(username, id, access);
				if (auth.sendJoin(bot)) {
					bot.connector.sendMessage("§2Logged to Mojang servers");
				} else {
					bot.connector.sendMessage("§4Failed to login to Mojang!");
				}
			} else {
				bot.connector.sendMessage("§4Failed to load session data!");
			}
		}
		int len1 = sharedSecret.length + reader.getVarIntCount(sharedSecret.length);
		int len2 = verify.length + reader.getVarIntCount(verify.length);
		int len3 = reader.getVarIntCount(1);
		reader.setOutputStream(len1 + len2 + len3);
		// Packet ID
		reader.writeVarInt(1);
		// shared secret length
		if (reader.ProtocolVersion >= 47) {
			reader.writeVarInt(sharedSecret.length);
		} else {
			reader.writeShort((short) sharedSecret.length);
		}
		// shared secret
		reader.writeBytes(sharedSecret);
		// shared verify length
		if (reader.ProtocolVersion >= 47) {
			reader.writeVarInt(verify.length);
		} else {
			reader.writeShort((short) verify.length);
		}
		// verify
		reader.writeBytes(verify);
		reader.Send();
	}

	private PublicKey decodekey(byte[] key) {
		try {
			return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
		} catch (Exception e) {
			System.err.println("Failed to decode public key!");
			return null;
		}
	}

	private byte[] encryptdata(Key key, byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(1, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			System.err.println("Failed to encrypt data!");
			return null;
		}
	}

	private byte[] getserverid(String ServerId, PublicKey publicKey, SecretKey secretKey) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[][] params = new byte[][] { secretKey.getEncoded(), publicKey.getEncoded() };
			int len = params.length;
			for (int i = 0; i < len; ++i) {
				byte[] encr = params[i];
				digest.update(encr);
			}
			return digest.digest();
		} catch (Exception e) {
			System.err.println("Failed to generate server ID!");
			return null;
		}
	}

	private SecretKey newkey() {
		try {
			KeyGenerator gen = KeyGenerator.getInstance("AES");
			gen.init(128);
			return gen.generateKey();
		} catch (Exception e) {
			System.err.println("Failed to generate key!");
			return null;
		}
	}

}