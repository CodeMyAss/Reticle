package org.spigot.reticle.API;

import java.lang.reflect.Method;

import org.spigot.reticle.events.ContextReceiveEvent;

public class ContextMenuItem {
	public final Object o;
	public final Method m;

	public ContextMenuItem(Object o, String m) throws Exception {
		this.o = o;
		Method m2 = null;
		m2 = o.getClass().getMethod(m, ContextReceiveEvent.class);
		this.m = m2;
	}
}
