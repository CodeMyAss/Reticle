package org.spigot.reticle.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.storage;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.coresp.MyEntity;
import org.spigot.reticle.coresp.PlayerStreamHandshakeResponse;
import org.spigot.reticle.packets.*;

public class PlayerStream extends Thread {
	private OutputStream out;
	private InputStream in;
	private ServerSocket Sockfactory;
	private Socket sock;
	private boolean enabled = true;
	private packet reader;
	private boolean online = false;
	private connector bundle;
	private final String StatusResponseString = "{\"version\": {\"name\": \"Reticle server\",\"protocol\": %reticle_version%},\"players\": {\"max\": 1,\"online\": 0,\"sample\":[]},\"description\": {\"text\":\"Reticle abstraction\"}}";
	private int port;
	private boolean projection = false;

	public void setProjection(boolean p) {
		this.projection = p;
	}

	public boolean isProjection() {
		return projection;
	}

	public boolean isBundle(connector con) {
		return bundle == con;
	}

	protected void sendIfAvailable(packetStruct packet) throws IOException {
		if (online) {
			if (packet.packetID == 0 || packet.packetID == 0x38 || (packet.packetID >= 0x3b && packet.packetID <= 0x3e)) {
				return;
			}
			reader.Send(packet);
		}
	}

	public void setBundle(mcbot bot) throws IOException {
		if (bot.connector != bundle) {
			if (online) {
				online = false;
				bundle.getEntities().destroyEntities(reader);
				//bundle.getInventory().sendInventoryReset(reader);
				MyEntity entity = bot.connector.MyEntity;
				MyEntity ent = new MyEntity();
				ent.levelType = entity.levelType;
				if (entity.Dimension == 0) {
					ent.Dimension = 1;
				} else {
					ent.Dimension = 0;
				}
				ent.Difficulty = entity.Difficulty;
				ent.Gamemode = entity.Gamemode;
				new PlayerStreamRespawnPacket(null, reader).Write(ent);
				new PlayerStreamRespawnPacket(null, reader).Write(entity);
				bot.connector.getChunks().reSendChunks(reader);
				new PlayerStreamPlayerPositionAndLookPacket(null, reader).Write(entity.x, entity.y, entity.z, entity.pitch, entity.yaw, entity.onGround);
				bot.connector.getEntities().reSendEntities(reader);
				bot.connector.getInventory().writeItems(reader);
				online = true;
			}
			this.bundle = bot.connector;
			storage.conlog("Bundled to " + bot.gettabname());
		}
	}

	public PlayerStream() {
	}

	@Override
	public void run() {
		port = storage.getBundlePort();
		storage.conlog("Starting in port " + port);
		try {
			Sockfactory = new ServerSocket(port, 10);
			while (enabled) {
				try {
					online = false;
					sock = Sockfactory.accept();
					online = false;
					handle();
					online = false;
					in.close();
					out.close();
					sock.close();
				} catch (Exception e) {
					online = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handle() throws IOException, SerialException, DataFormatException {
		this.in = sock.getInputStream();
		this.out = sock.getOutputStream();
		// Read handshake
		reader = new packet(null, in, out);
		int[] next = reader.readNext();
		if (next[1] != 0) {
			// Not handshake
			return;
		}
		ByteBuffer buf = makepacket(next);
		PlayerStreamHandShakePacket playerhandshake = new PlayerStreamHandShakePacket(buf, reader);
		PlayerStreamHandshakeResponse handshakeresp = playerhandshake.Read();
		if (handshakeresp.nextState == 1) {
			// Empty packet
			next = reader.readNext();
			String fs = this.StatusResponseString.replace("%reticle_version%", handshakeresp.Version + "");
			playerhandshake.WriteStatus(fs);
			// Status
			// StatusResponseString
			handlePing();
			return;
		}
		if (handshakeresp.nextState != 2) {
			return;
		}
		next = reader.readNext();
		if (next[1] != 0) {
			// Not login start as expected
			return;
		}
		MyEntity MyEntity = bundle.MyEntity;
		buf = makepacket(next);
		PlayerStreamLoginStartPacket loginreq = new PlayerStreamLoginStartPacket(buf, reader);
		String user = loginreq.Read();
		reader.ProtocolVersion = handshakeresp.Version;
		storage.conlog("Connecting: " + user + " protocol version " + handshakeresp.Version);
		loginreq.WriteLoginSuccess(MyEntity.UUID, MyEntity.Username);
		new PlayerStreamJoinGamePacket(null, reader).Write(MyEntity.EntityId, MyEntity.Gamemode, MyEntity.Dimension, MyEntity.Difficulty, MyEntity.MaxPlayers, MyEntity.levelType);
		new PlayerStreamSpawnPositionPacket(null, reader).Write(0, 0, 0);
		MyEntity.AbilitiesPacket.Send(reader);
		new PlayerStreamPlayerPositionAndLookPacket(null, reader).Write(MyEntity.x, MyEntity.y, MyEntity.z, MyEntity.pitch, MyEntity.yaw, MyEntity.onGround);
		bundle.getChunks().reSendChunks(reader);
		bundle.getEntities().reSendEntities(reader);
		bundle.getInventory().writeItems(reader);
		online = true;
		mainloop();
		online = false;
	}

	private void mainloop() throws IOException, SerialException, DataFormatException {
		while (true) {
			packetStruct packet = reader.readNexter();
			if (bundle != null) {
				if (!projection) {
					bundle.sendIfAvailable(packet);
				}
			}
		}
	}

	private void handlePing() throws SerialException, IOException {
		int[] next = reader.readNext();
		if (next[1] != 1) {
			// Not ping
			return;
		}
		ByteBuffer buf = makepacket(next);
		PlayerStreamLoginRequest pingpacket = new PlayerStreamLoginRequest(buf, reader);
		pingpacket.Read();
		pingpacket.Write();
	}

	protected ByteBuffer makepacket(int[] pack) throws SerialException, IOException {
		byte[] packer = reader.readInnerBytes(pack[0] - reader.getVarIntCount(pack[1]));
		return ByteBuffer.wrap(packer);
	}
}
