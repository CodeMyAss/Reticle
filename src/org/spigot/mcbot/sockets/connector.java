package org.spigot.mcbot.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.botfactory.mcbot.ICONSTATE;
import org.spigot.mcbot.packets.ChatPacket;
import org.spigot.mcbot.packets.ConnectionResetPacket;
import org.spigot.mcbot.packets.HandShakePacket;
import org.spigot.mcbot.packets.Ignored_Packet;
import org.spigot.mcbot.packets.JoinGamePacket;
import org.spigot.mcbot.packets.KeepAlivePacket;
import org.spigot.mcbot.packets.LoginStartPacket;
import org.spigot.mcbot.packets.LoginSuccessPacket;
import org.spigot.mcbot.packets.packet;

public class connector extends Thread {
	private mcbot bot;
	private Socket sock;

	public connector(mcbot bot) throws UnknownHostException, IOException {
		sock = new Socket(bot.serverip, bot.serverport);
		this.bot = bot;
		sendmsg("§2Connecting");
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public void run() {
		// Main loop
		try {
			InputStream input = sock.getInputStream();
			packet reader = new packet(input);
			bot.seticon(ICONSTATE.CONNECTING);
			// First, we must send HandShake and hope for good response
			new HandShakePacket(sock).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(sock).Write(bot.username);
			new LoginSuccessPacket(sock, this).read();
			bot.seticon(ICONSTATE.CONNECTED);
			int pid;
			int len;
			int[] pack = new int[2];
			while (true) {
				pack = reader.readNext();
				len = pack[0];
				pid = pack[1];
				sendmsg("Received packet id " + pid + " LEN: " + len);

				if (pid > reader.MAXPACKETID) {
					sendmsg("§4Malformed communication");
					break;
				}

				if (reader.ValidPackets.contains(pid)) {
					if (len > 1) {
						processpacket(pid);
					}
					// We have good one
				} else {
					// We decided to ignore this one
					new Ignored_Packet(len, pid, input).Read();
				}
			}
		} catch (IOException e) {
			sendmsg("§4Disconnected");
			e.printStackTrace();
		} catch (RuntimeException e) {
			sendmsg("§4Error happened");
			e.printStackTrace();
			try {
				this.sock.close();
			} catch (IOException e1) {
			}
		}
		stopMe();
	}
	
	private void stopMe() {
		bot.seticon(ICONSTATE.DISCONNECTED);
		try {
			sock.close();
		} catch (IOException e) {
		}
		sock=null;
	}

	private packet processpacket(int pid) throws IOException {
		packet pack = null;
		switch (pid) {
			case 0:
				// Keep us alive
				pack = new KeepAlivePacket(sock);
				int resp = ((KeepAlivePacket) pack).Read();
				((KeepAlivePacket) pack).Write(resp);
			break;

			case 1:
				// join game
				pack = new JoinGamePacket(sock);
				((JoinGamePacket) pack).Read();
			break;

			case 2:
				// chat
				pack = new ChatPacket(sock);
				String msg = ((ChatPacket) pack).Read();
				sendmsg(msg);
			break;

			case 64:
				// Server closed connection
				String reason = new ConnectionResetPacket(sock.getInputStream()).read();
				sendmsg("§4Server closed connection. ("+reason+")");
		}
		return pack;
	}

	public boolean isConnected() {
		if (sock == null) {
			return false;
		} else {
			return sock.isConnected();
		}
	}

	public void sendmessage(String message) {
		sendmsg(message);
	}

	private void sendmsg(String message) {
		sendrawmsg("[Connector] " + message);
	}

	private void sendrawmsg(String message) {
		bot.logmsg(message);
	}

}
