package org.spigot.reticle.sockets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatLogger {
	private FileOutputStream out;
	private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	private File dir = new File("logs");
	private String currentformat;
	private String botname;

	/**
	 * Chat logger class.
	 * Everything related to chat logging is here
	 * @param botName
	 * @throws FileNotFoundException
	 */
	public ChatLogger(String botName) throws FileNotFoundException {
		this.botname=botName;
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		File mdir = new File("logs/" + botName);
		if (!mdir.exists() || !mdir.isDirectory()) {
			mdir.mkdir();
		}
		currentformat = "logs/" + botName + "/" + format.format(new Date()) + ".txt";
		out = new FileOutputStream(new File(currentformat),true);
	}

	/**
	 * Close chat logger
	 * @throws IOException
	 */
	public void Close() throws IOException {
		out.close();
	}

	private void rereshFile() throws IOException {
		String filename = "logs/" + botname + "/" + format.format(new Date()) + ".txt";
		if (!currentformat.equals(filename)) {
			currentformat = filename;
			out.close();
			out = new FileOutputStream(new File(currentformat),true);
		}
	}

	/**
	 * Log message
	 * @param message
	 * @throws IOException
	 */
	public void Log(String message) throws IOException {
		rereshFile();
		out.write((message + System.lineSeparator()).getBytes());
	}

}
