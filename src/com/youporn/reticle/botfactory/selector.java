package org.spigot.reticle.botfactory;

import javax.swing.JTabbedPane;

import org.spigot.reticle.storage;

public class selector {
	protected tabselector sel = new tabselector(storage.gettabbedpane());

	class tabselector {
		private boolean canchange = true;
		private boolean ready = false;
		private JTabbedPane pane;
		public int index;

		protected void setPane() {
			if (pane == null) {
				pane = storage.getInstance().tabbedPane;
			}
		}

		protected tabselector(JTabbedPane pane) {
			this.pane = pane;
		}

		protected int getTabCount() {
			if (pane == null) {
				return 1;
			}
			return pane.getTabCount();
		}

		protected void Change(boolean can) {
			canchange = can;
		}

		protected boolean isReady() {
			return ready;
		}

		protected void setRead(boolean ready) {
			this.ready = ready;
		}

		protected boolean canChange() {
			return canchange;
		}
	}
}
