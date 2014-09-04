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
				

				while(pid > packet.MAXPACKETID) {
					pack = reader.readNext();
					len = pack[0];
					pid = pack[1];
				}
				
				//sendmsg("Received packet id " + pid + " LEN: " + len);
				if (pid > packet.MAXPACKETID) {
					sendmsg("§4Malformed communication");
					break;
				}

				if (packet.ValidPackets.contains(pid)) {
					if (len > 1) {
						//We shall serve this one
						processpacket(pid,len);
					}
					// We have good one
				} else {
					// We decided to ignore this one
					new Ignored_Packet(len, pid, input).Read();
				}
			}
		} catch (IOException e) {
			sendmsg("§4Disconnected");
		} catch (RuntimeException e) {
			sendmsg("§4Error happened. Error log written into main tab. Please report this.");
			e.printStackTrace();
			try {
				this.sock.close();
			} catch (IOException e1) {
			}
		}
		stopMe();
	}
	
	public synchronized boolean sendtoserver(String msg) {
		try {
			new ChatPacket(this.sock).Write(msg);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private void stopMe() {
		bot.seticon(ICONSTATE.DISCONNECTED);
		try {
			sock.close();
		} catch (IOException e) {
		}
		sock=null;
	}

	private packet processpacket(int pid, int len) throws IOException {
		packet pack = null;
		switch (pid) {
			default:
			sendmsg("§4§l§nUnhandled packet "+pid);
			new Ignored_Packet(len, pid, sock.getInputStream()).Read();
			break;
			
			case 0:
				// Keep us alive
				pack = new KeepAlivePacket(sock);
				byte[] resp = ((KeepAlivePacket) pack).Read(len-1);
				((KeepAlivePacket) pack).Write(resp);
			break;

			//Never served (We don't really care about the data here (yet)
			case 1:
				// join game
				pack = new JoinGamePacket(sock);
				((JoinGamePacket) pack).Read();
			break;
/*
			case 2:
				// chat
				pack = new ChatPacket(sock);
				String msg = ((ChatPacket) pack).Read();
				sendmsg(msg);
			break;
*/
			case 64:
				// Server closed connection
				String reason = new ConnectionResetPacket(sock.getInputStream()).read();
				sendmsg("§4Server closed connection. ("+reason+")");
		}
		return pack;
	}
	
	public String parsechat() {
		return null;
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
