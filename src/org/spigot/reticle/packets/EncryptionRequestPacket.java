package org.spigot.reticle.packets;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.Authenticator;
import org.spigot.reticle.sockets.CryptManager;

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
		int pkl = reader.readVarInt();
		// Public key
		byte[] publicKeyBytes = reader.readBytes(pkl);
		// Length of verify token
		int vtl = reader.readVarInt();
		// Verify token
		verify = reader.readBytes(vtl);
		// Shared secret

		PublicKey publicKey = CryptManager.decodePublicKey(publicKeyBytes);
		sharedKey = CryptManager.createNewSharedKey();
		sharedSecret = CryptManager.encryptData(publicKey, sharedKey.getEncoded());
		verify = CryptManager.encryptData(publicKey, verify);
		serverId = (new BigInteger(CryptManager.getServerIdHash(serverid.trim(), publicKey, sharedKey))).toString(16);
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
				if (auth.sendJoin()) {
					bot.connector.sendMessage("§2Logged to Mojang servers");
				} else {
					bot.connector.sendMessage("§4Failed to login to Mojang!");
				}
			} else {
				bot.connector.sendMessage("§4Failed to load session data!");
			}
		}
		int len1 = sharedSecret.length + reader.getVarntCount(sharedSecret.length);
		int len2 = verify.length + reader.getVarntCount(verify.length);
		int len3 = reader.getVarntCount(1);
		reader.setOutputStream(len1 + len2 + len3);
		// Packet ID
		reader.writeVarInt(1);
		// shared secret length
		reader.writeVarInt(sharedSecret.length);
		// shared secret
		reader.writeBytes(sharedSecret);
		// shared verify length
		reader.writeVarInt(verify.length);
		// verify
		reader.writeBytes(verify);
		reader.Send();
	}

}