package org.spigot.reticle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.spigot.reticle.API.Plugin;
import org.spigot.reticle.events.ConsoleCommandEvent;
import org.spigot.reticle.events.Event;

public class PluginManager {
	private HashMap<Plugin, PluginInfo> Plugins = new HashMap<Plugin, PluginInfo>();
	private HashMap<Class<?>, HashMap<Plugin, HashMap<Method, Object>>> methods_by_plugins = new HashMap<Class<?>, HashMap<Plugin, HashMap<Method, Object>>>();

	protected PluginManager() {

	}

	public List<String> getAllPluginNames() {
		Object[] pl = this.getPluginInfos().toArray();
		List<String> res = new ArrayList<String>();
		for (Object plinfo : pl) {
			res.add(((PluginInfo) plinfo).Name);
		}
		return res;
	}

	/**
	 * @param Plugin
	 *            The plugin
	 * @return PluginInfo for selected plugin
	 */
	public PluginInfo getPluginInfo(Plugin Plugin) {
		return Plugins.get(Plugin);
	}

	/**
	 * Finds plugin based on its file name
	 * 
	 * @param pl
	 * @return plugin
	 */
	public Plugin getPluginByFileName(String pl) {
		for (PluginInfo info : Plugins.values()) {
			if (info.FileName.getName().equalsIgnoreCase(pl) || info.FileName.getName().equalsIgnoreCase(pl + ".jar")) {
				return info.getInstance();
			}
		}
		return null;
	}

	/**
	 * Finds plugin specified by name
	 * 
	 * @param PluginName
	 * @return Plugin or null if not found
	 */
	public Plugin getPluginByName(String PluginName) {
		for (PluginInfo info : Plugins.values()) {
			if (info.Name.equalsIgnoreCase(PluginName)) {
				return info.getInstance();
			}
		}
		return null;
	}

	protected void unloadAllPlugins() {
		Object[] Pluginss = Plugins.keySet().toArray();
		for (Object pl : Pluginss) {
			unloadPlugin((Plugin) pl);
		}
	}

	/**
	 * Unloads plugin
	 * 
	 * @param Plugin
	 */
	public void unloadPlugin(Plugin Plugin) {
		if (this.pluginExists(Plugin)) {
			Plugin.onUnload();
			this.RemoveAllMethodsForPlugin(Plugin);
			PluginInfo plinfo = Plugins.get(Plugin);
			plinfo.closeLoader();
			Plugins.remove(Plugin);
		}
	}

	/**
	 * Parse all enabled plugins
	 * 
	 * @return Array of loaded plugins
	 */
	public Collection<PluginInfo> getPluginInfos() {
		return Plugins.values();
	}

	private void registerPlugin(PluginInfo plug) {
		Plugin pl = plug.getInstance();
		if (!pluginExists(pl) && pl != null) {
			Plugins.put(pl, plug);
			pl.onLoad();
		}
	}

	private void enablePlugins() {
		for (Plugin pl : Plugins.keySet()) {
			enablePlugin(pl);
		}
	}

	private void enablePlugin(Plugin pl) {
		PluginInfo plinfo = Plugins.get(pl);
		storage.conlog("§bEnabling §f" + plinfo.Name + "§b version §f" + plinfo.Version + " §bmade by §f" + plinfo.Author);
		pl.onEnable();
	}

	public boolean loadPlugin(String filename) {
		PluginInfo pl = tryLoadPlugin(new File("plugins/" + filename));
		if (pl == null) {
			pl = tryLoadPlugin(new File("plugins/" + filename + ".jar"));
		}
		if (pl == null) {
			return false;
		} else {
			registerPlugin(pl);
			enablePlugin(pl.getInstance());
			return true;
		}
	}

	private PluginInfo tryLoadPlugin(File fileEntry) {
		try {
			ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
			URLClassLoader ClazzL = new URLClassLoader(new URL[] { fileEntry.toURI().toURL() }, currentThreadClassLoader);
			BufferedReader in = new BufferedReader(new InputStreamReader(ClazzL.getResourceAsStream("plugin.yml")));
			String author = null;
			String name = null;
			String main = null;
			String version = null;
			while (in.ready()) {
				String s = in.readLine();
				String opt = s.split(": ")[0].toLowerCase();
				String param = s.substring(opt.length() + 2);
				if (opt.equals("author")) {
					author = param;
				} else if (opt.equals("name")) {
					name = param;
				} else if (opt.equals("main")) {
					main = param;
				} else if (opt.equals("version")) {
					version = param;
				}
			}
			in.close();
			if (main == null || author == null || name == null || version == null) {
				ClazzL.close();
				return null;
			}
			if (this.getPluginByName(name) != null) {
				ClazzL.close();
				return null;
			}
			Class<?> c = ClazzL.loadClass(main);
			return new PluginInfo(ClazzL, author, fileEntry, version, name, c);
		} catch (Exception e) {
			return null;
		}
	}

	protected void loadAllPlugins() {
		String pluginsdir = "plugins";
		File pldir = new File(pluginsdir);
		if (!pldir.exists() || !pldir.isDirectory()) {
			if (!pldir.mkdirs()) {
				storage.conlog("§4Fatal error while loading plugins");
				return;
			}
		}
		for (File fileEntry : pldir.listFiles()) {
			if (!fileEntry.isDirectory()) {
				PluginInfo plug = tryLoadPlugin(fileEntry);
				if (plug != null) {
					storage.conlog("§2Loaded plugin " + plug.FileName);
					registerPlugin(plug);
				} else {
					storage.conlog("§4Failed to load plugin " + fileEntry.getPath());
				}
			}
		}
		enablePlugins();
	}

	protected boolean pluginExists(Plugin Plugin) {
		return Plugins.containsKey(Plugin);
	}

	protected boolean pluginHasMethod(Plugin Plugin, Method Method, Class<?> Class) {
		if (methods_by_plugins.containsKey(Class)) {
			if (methods_by_plugins.get(Class).containsKey(Plugin)) {
				if (methods_by_plugins.get(Class).get(Plugin).containsKey(Method)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected boolean pluginIsHandled(Plugin Plugin, Class<?> Class) {
		if (methods_by_plugins.containsKey(Class)) {
			return methods_by_plugins.get(Class).containsKey(Plugin);
		} else {
			return false;
		}
	}

	protected boolean ClassIsHandled(Class<?> Class) {
		return methods_by_plugins.containsKey(Class);
	}

	/**
	 * Invoked when event is being dispatched to listeners
	 * 
	 * @param e
	 *            Event to be dispatcher
	 * @param list
	 *            List of enabled plugins
	 */
	public void invokeEvent(Event e, List<String> list) {
		invokeEvent(e, list, false);
	}

	public void invokeEvent(ConsoleCommandEvent event, boolean override) {
		invokeEvent(event, null, true);
	}

	private void invokeEvent(Event e, List<String> list, boolean override) {
		Class<?> cls = e.getClass();
		if (ClassIsHandled(cls)) {
			for (Plugin plugin : methods_by_plugins.get(cls).keySet()) {
				if (!override && list != null) {
					if (!list.contains(Plugins.get(plugin).Name)) {
						continue;
					}
				}
				Set<Method> methods = methods_by_plugins.get(cls).get(plugin).keySet();
				for (Method method : methods) {
					try {
						Object instance = methods_by_plugins.get(cls).get(plugin).get(method);
						method.invoke(instance, e);
					} catch (IllegalAccessException e1) {
					} catch (IllegalArgumentException e1) {
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private void RemoveAllMethodsForPlugin(Plugin pl) {
		for (Class<?> cls : methods_by_plugins.keySet()) {
			if (methods_by_plugins.get(cls).containsKey(pl)) {
				methods_by_plugins.get(cls).remove(pl);
			}
		}
	}

	/**
	 * Called when method is added to dispatcher
	 * 
	 * @param Plugin
	 * @param Method
	 * @param Class
	 * @param Instance
	 */
	public void addMethod(Plugin Plugin, Method Method, Class<?> Class, Object Instance) {
		if (!methods_by_plugins.containsKey(Class)) {
			methods_by_plugins.put(Class, new HashMap<Plugin, HashMap<Method, Object>>());
		}
		if (!methods_by_plugins.get(Class).containsKey(Plugin)) {
			methods_by_plugins.get(Class).put(Plugin, new HashMap<Method, Object>());
		}
		if (!methods_by_plugins.get(Class).get(Plugin).containsKey(Method)) {
			methods_by_plugins.get(Class).get(Plugin).put(Method, Instance);
		}
	}
}
