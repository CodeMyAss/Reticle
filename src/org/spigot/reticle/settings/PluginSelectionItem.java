package org.spigot.reticle.settings;

import javax.swing.JCheckBox;

public class PluginSelectionItem {
	private JCheckBox box;
	private String plname;
	private boolean enabled;

	protected PluginSelectionItem(String pl, boolean en) {
		this.plname = pl;
		this.enabled = en;
	}

	private void createCheckBox() {
		this.box = new JCheckBox(plname,enabled);
	}

	protected String getName() {
		return plname;
	}
	
	protected JCheckBox getBox() {
		if (this.box == null) {
			createCheckBox();
		}
		return this.box;
	}
}
