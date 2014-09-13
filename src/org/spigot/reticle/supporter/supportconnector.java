package org.spigot.reticle.supporter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

import org.spigot.reticle.storage;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.settings.team_struct;

import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.NickListEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.IRCEventListener;

public class supportconnector implements IRCEventListener {
	private boolean connected = false;
	public String username;
	private ConnectionManager manager;
	private mcbot bot;
	private Session session;
	private Channel chan;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> emptymap1 = new HashMap<String, String>();
	private HashMap<String, team_struct> emptymap2 = new HashMap<String, team_struct>();

	public supportconnector(mcbot bot) {
		this.bot = bot;
		Connect();
	}

	public void Disconnect() {
		if (session != null) {
			session.close("");
		}
		bot.resettablist();
		sendmsg("Disconnected");
		this.connected = false;
	}

	public boolean isConnected() {
		return connected;
	}

	public void Connect() {
		bot.resettablist();
		Tablist = new ArrayList<String>();
		this.username = storage.getSupportNick();
		this.connected = true;
		sendmsg("Connecting to support server...");
		manager = new ConnectionManager(new Profile(obfuscatemessage(username)));
		session = manager.requestConnection(storage.supportserver);
		session.addIRCEventListener(this);
	}

	@Override
	public void receiveEvent(IRCEvent event) {
		Type type = event.getType();
		if (type == Type.CONNECT_COMPLETE) {
			event.getSession().join(storage.supportchannel);
		} else if (type == Type.JOIN_COMPLETE) {
			sendmsg("Welcome to §1§nReticle support");
			sendmsg("§4§l§nThis server has no authentication so please do not post personal data here!");
			chan = ((JoinCompleteEvent) event).getChannel();
			bot.setTabSize(15, 2);
		} else if (type == Type.CHANNEL_MESSAGE || type==Type.PRIVATE_MESSAGE) {
			MessageEvent chatevent = (MessageEvent) event;
			String msgr = deobfuscatemessage(chatevent.getMessage());
			if (msgr.length() > 0) {
				sendchatmsg(getNick(chatevent.getNick()) + ": " + msgr);
			}
		} else if (type == Type.NICK_LIST_EVENT) {
			NickListEvent nlevent = (NickListEvent) event;
			for (String nick : nlevent.getNicks()) {
				addToTablist(nick, false);
			}
			refreshtablist();
		} else if (type == Type.JOIN) {
			JoinEvent joinevent = (JoinEvent) event;
			addToTablist(joinevent.getNick(), true);
			sendmsg("§0User §n"+getNick(joinevent.getNick())+"§r§0 has joined support server");
		} else if (type == Type.QUIT || type == Type.KICK_EVENT) {
			QuitEvent quitevent = (QuitEvent) event;
			removeFromTablist(quitevent.getNick(), true);
			sendmsg("§0User §n"+getNick(quitevent.getNick())+"§r§0 has left support server");
		} else if (type == Type.CONNECTION_LOST) {
			event.getSession().close("");
			Disconnect();
		}
	}

	private void removeFromTablist(String nick, boolean update) {
		nick = getNick(nick);
		if (Tablist.contains(nick)) {
			Tablist.remove(nick);
			if (update) {
				refreshtablist();
			}
		}
	}

	private void addToTablist(String nick, boolean update) {
		nick = getNick(nick);
		if (nick.length() > 0) {
			if (!Tablist.contains(nick)) {
				Tablist.add(nick);
			}
			if (update) {
				refreshtablist();
			}
		}
	}

	private void refreshtablist() {
		bot.refreshtablist(Tablist, emptymap1, emptymap2);
	}

	private String getNick(String nick) {
		return "§0" + deobfuscatemessage(nick);
	}

	private String base64decode(String message) {
		try {
			return new String(DatatypeConverter.parseBase64Binary(message), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(DatatypeConverter.parseBase64Binary(message));
		}
	}

	private String base64encode(String message) {
		try {
			return new String(DatatypeConverter.printBase64Binary(message.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return new String(DatatypeConverter.printBase64Binary(message.getBytes()));
		}
	}

	private String obfuscatemessage(String origmsg) {
		String res;
		res = base64encode(reverse(base64encode(origmsg)));
		return res;
	}

	private String deobfuscatemessage(String message) {
		String res;
		res = base64decode(reverse(base64decode(message)));
		return res;
	}

	private String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}

	public void SendMessage(String message) {
		sendrawchatmsg(message);
		sendtoserverraw(obfuscatemessage(message));
	}

	private void sendtoserverraw(String message) {
		if (session.isConnected()) {
			session.sayChannel(message, chan);
		}
	}

	private void sendrawchatmsg(String msg) {
		if (!msg.equals("")) {
			sendrawmsg("[CHAT] " + username + ": " + msg);
		}
	}

	private void sendchatmsg(String msg) {
		if (!msg.equals("")) {
			sendrawmsg("[CHAT] " + msg);
		}
	}

	private void sendmsg(String msg) {
		sendrawmsg("[Support] " + msg);
	}

	private void sendrawmsg(String msg) {
		bot.logmsg(msg);
	}

}
