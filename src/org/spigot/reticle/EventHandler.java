package org.spigot.reticle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.spigot.reticle.events.event;

public class EventHandler {
	private Collection<Method> handlers = new ArrayList<Method>();
	
	public void addHandler(Method handler) {
		handlers.add(handler);
	}

	public void removeHandler(Method handler) {
		handlers.remove(handler);
	}
	

	public void dispatchEvent(event event) {
		for (Object handler : handlers) {
			dispatchEventTo(event, handler);

		}
	}
	
	private Collection<Method> findMatchingEventHandlerMethods(Object handler, String eventName) {
		Method[] methods = handler.getClass().getDeclaredMethods();
		Collection<Method> result = new ArrayList<Method>();
		for (Method method : methods) {
			if (canHandleEvent(method, eventName)) {
				result.add(method);
			}
		}
		return result;
	}
	

	private boolean canHandleEvent(Method method, String eventName) {
		HandleEvent handleEventAnnotation = method.getAnnotation(HandleEvent.class);
		if (handleEventAnnotation != null) {
			String[] values = handleEventAnnotation.value();
			return Arrays.asList(values).contains(eventName);
		}
		return false;
	}
	
	protected void dispatchEventTo(event event, Object handler) {
		Collection<Method> methods = findMatchingEventHandlerMethods(handler, event.getEventName());
		for (Method method : methods) {
			try {
				// Make sure the method is accessible (JDK bug ?)
				method.setAccessible(true);

				if (method.getParameterTypes().length == 0)
					method.invoke(handler);
				if (method.getParameterTypes().length == 1)
					method.invoke(handler, event);
				if (method.getParameterTypes().length == 2)
					method.invoke(handler, this, event);
			} catch (Exception e) {
				System.err.println("Could not invoke event handler!");
				e.printStackTrace(System.err);
			}
		}
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HandleEvent {
		String[] value();
	}
	
}
