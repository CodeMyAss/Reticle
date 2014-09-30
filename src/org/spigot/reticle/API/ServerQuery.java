package org.spigot.reticle.API;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.spigot.reticle.botfactory.mcbot;

public class ServerQuery {

	private DatagramSocket sock;
	private String ip;
	private int port;
	private byte[] session;
	private byte[] challenge;
	private final byte[] handshakeseq = new byte[] { (byte) 0xfe, (byte) 0xfd, 0x09, 0x00, 0x00, 0x00, 0x01 };
	private InetAddress addr;

	public ServerQuery(mcbot bot) {
		this.ip = bot.serverip;
		this.port = bot.serverport;
	}

	public ServerQuery(String ip, int Port) {
		this.ip = ip;
		this.port = Port;
	}

	private void readFirstResponse() throws Exception {
		byte[] b = new byte[64];
		DatagramPacket pack = new DatagramPacket(b, b.length);
		sock.receive(pack);
		byte[] res = pack.getData();
		session = new byte[] { res[1], res[2], res[3], res[4] };
		int chal = Integer.parseInt(readString(res, 5));
		challenge = ByteBuffer.allocate(4).putInt(chal).array();
	}

	private String readString(byte[] buf, int len) throws Exception {
		StringBuilder sb = new StringBuilder();
		int r;
		while (true) {
			r = buf[len];
			len++;
			if (r == 0) {
				break;
			} else {
				sb.append((char) r);
			}
		}
		return sb.toString();
	}

	private void send(byte[] ar) throws IOException {
		sock.send(new DatagramPacket(ar, ar.length, addr, port));
	}

	public QueryResponse Execute() {
		try {
			addr = InetAddress.getByName(ip);
			sock = new DatagramSocket();
			sock.connect(addr, port);
			send(handshakeseq);
			readFirstResponse();
			writeRequest();
			QueryResponse resp = readStatus();
			sock.close();
			return resp;
		} catch (PortUnreachableException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public class QueryResponse {
		public final String[] plugins;
		public final String server;
		public final String[] players;

		protected QueryResponse(String[] plugins, String server, String[] players) {
			this.plugins = plugins;
			this.players = players;
			this.server = server;
		}
	}

	private QueryResponse readStatus() throws Exception {
		byte[] b = new byte[1024];
		DatagramPacket pack = new DatagramPacket(b, b.length);
		sock.receive(pack);
		byte[] res = pack.getData();
		int i = 15;
		boolean tobreak = false;
		boolean pl = false;
		StringBuilder sb = new StringBuilder();
		String plr = "";
		while (true) {
			i++;
			if(i>=res.length) {
				break;
			}
			if (res[i] == 0) {
				if (tobreak) {
					break;
				} else {
					if (sb.toString().equals("plugins")) {
						pl = true;
					} else if (pl) {
						pl = false;
						plr = sb.toString();
					}
					sb = new StringBuilder();
					tobreak = true;
				}
			} else {
				tobreak = false;
				sb.append((char) res[i]);
			}
		}
		i += 10;
		List<String> nicks = new ArrayList<String>();
		sb = new StringBuilder();
		while (true) {
			i++;
			if(i>=res.length) {
				break;
			}
			if (res[i] == 0) {
				if (tobreak) {
					break;
				} else {
					nicks.add(sb.toString());
					sb = new StringBuilder();
					tobreak = true;
				}
			} else {
				tobreak = false;
				sb.append((char) b[i]);
			}
		}
		String server = "Unknown";
		if (plr.contains(":")) {
			String[] rr = plr.split(": ");
			server = rr[0];
			if (rr.length > 1) {
				plr = rr[1];
			}
		}

		String[] plugins = null;
		if (plr.contains("; ")) {
			plugins = plr.split("; ");
		}
		if (plugins == null) {
			plugins = new String[0];
		}
		String[] players = new String[nicks.size()];
		nicks.toArray(players);
		QueryResponse resp = new QueryResponse(plugins, server, players);
		return resp;
	}

	private void writeRequest() throws IOException {
		byte[] sup = new byte[] { (byte) 0xfe, (byte) 0xfd, 0x00 };
		byte[] sup2 = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(sup);
		outputStream.write(session);
		outputStream.write(challenge);
		outputStream.write(sup2);
		byte[] fina = outputStream.toByteArray();
		DatagramPacket pack = new DatagramPacket(fina, fina.length);
		sock.send(pack);
	}
}
