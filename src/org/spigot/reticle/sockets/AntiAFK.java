package org.spigot.reticle.sockets;

public class AntiAFK extends Thread {
	private connector con;

	
	public AntiAFK(connector connector) {
		this.con = connector;
	}

	@Override
	public void run() {
		// Necessary semaphore to wait on
		Object sync = new Object();
		synchronized (sync) {
			try {
				while (true) {
					sync.wait(1000 * con.getantiafkperiod());
					String[] cmds=con.getafkcommands();
					for(String cmd:cmds) {
						con.sendToServer(cmd,true);
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
