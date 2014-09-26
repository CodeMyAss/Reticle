package org.spigot.reticle.API;

import java.lang.reflect.Method;

import org.spigot.reticle.storage;

public class Plugin {
	

	/**
	 * Called when plugin is loaded
	 * (Before enabling)
	 */
	public void onLoad() {

	}

	/**
	 * Called when all plugins are loaded
	 */
	public void onEnable() {

	}

	/**
	 * Called before plugin is unloaded
	 */
	public void onDisable() {

	}

	/**
	 * Never invoked directly
	 */
	public void onUnload() {
	}

	private final boolean isListener(Class<?> Class) {
		return Listener.class.isAssignableFrom(Class);
	}

	/**
	 * Register event listener
	 * @param Plugin
	 * @param Instance
	 */
	protected final void addEventListener(Plugin Plugin, Object Instance) {
		Class<?> cls = Instance.getClass();
		if (isListener(cls)) {
			Method[] methods = cls.getMethods();
			String MasterClasses = "org.spigot.reticle.events.";
			for (Method method : methods) {
				if (!method.isAnnotationPresent(EventHandler.class)) {
					continue;
				}
				Class<?>[] types = method.getParameterTypes();
				if (types.length == 1) {
					Class<?> type = types[0];
					try {
						if (type.getName().toLowerCase().startsWith(MasterClasses)) {
							if (Class.forName(type.getName()) != null) {
								storage.pluginManager.addMethod(Plugin, method, type, Instance);
							}
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
}
