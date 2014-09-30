package org.spigot.reticle.API;

import java.net.Socket;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.packets.HandShakePacket;
import org.spigot.reticle.packets.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerInfo {
	private final String serverip;
	private final int serverport;
	private final int mode=1;
	private final int protocolversion;
	
	@Deprecated
	public ServerInfo(String ip, int port, int protocolversion) {
		this.serverip=ip;
		this.serverport=port;
		this.protocolversion=protocolversion;
	}
	
	public ServerInfo(mcbot bot) {
		this.serverip=bot.serverip;
		this.serverport=bot.serverport;
		this.protocolversion=bot.getprotocolversion();
	}
	
	public StatusResponse Execute() {
		try {
		Socket sock=new Socket(serverip, serverport);
		packet reader = new packet(null, sock.getInputStream(), sock.getOutputStream());
		reader.ProtocolVersion = protocolversion;
		HandShakePacket packet = new HandShakePacket(reader);
		packet.Write(serverip, serverport, mode);
		String str=packet.ReadStatus();
		sock.close();
		Gson gson = new GsonBuilder().create();
		StatusResponse response = gson.fromJson(str, StatusResponse.class);
		return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
