package org.spigot.reticle.sockets;

import java.util.ArrayList;
import java.util.List;

public class ChatThread extends Thread {
	private List<ChatJob> jobs = new ArrayList<ChatJob>();

	@Override
	public void run() {
		try {
			mainloop();
		} catch (InterruptedException e) {
		}
	}

	private void mainloop() throws InterruptedException {
		Object w = new Object();
		synchronized (w) {
			while (!interrupted()) {
				w.wait(500);
				executeJobs();
			}
		}
	}

	/**
	 * Add chat job to be executed by this ChatThread
	 * @param job
	 */
	public synchronized void addJob(ChatJob job) {
		this.jobs.add(job);
	}

	private synchronized void executeJobs() {
		if (jobs.size() > 0) {
			List<ChatJob> newjobs = new ArrayList<ChatJob>();
			long current = System.currentTimeMillis() / 1000;
			for (ChatJob job : jobs) {
				if (job.delay <= current) {
					job.Execute();
				} else {
					newjobs.add(job);
				}
			}
			jobs = newjobs;
		}
	}

}
