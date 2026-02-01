package sledgemc.dev.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple event bus for dispatching events to listeners.
 */
public class EventBus {

    private final String name;
    private final Map<Class<? extends Event>, List<RegisteredListener>> listeners;
    private final Set<Object> registeredObjects;

    public EventBus() {
        this("default");
    }

    public EventBus(String name) {
        this.name = name;
        this.listeners = new ConcurrentHashMap<>();
        this.registeredObjects = ConcurrentHashMap.newKeySet();
    }

    public void register(Object listener) {
        if (registeredObjects.contains(listener))
            return;

        registeredObjects.add(listener);

        for (Method method : listener.getClass().getDeclaredMethods()) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe == null)
                continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                throw new IllegalArgumentException("Invalid event handler: " + method);
            }

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) params[0];

            method.setAccessible(true);
            RegisteredListener reg = new RegisteredListener(listener, method, subscribe.priority(),
                    subscribe.receiveCancelled());

            listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(reg);
            sortListeners(eventClass);
        }
    }

    public void unregister(Object listener) {
        if (!registeredObjects.remove(listener))
            return;

        for (List<RegisteredListener> list : listeners.values()) {
            list.removeIf(l -> l.instance == listener);
        }
    }

    public <T extends Event> T post(T event) {
        List<RegisteredListener> handlers = listeners.get(event.getClass());
        if (handlers == null)
            return event;

        for (RegisteredListener listener : handlers) {
            if (event.isCancelled() && !listener.receiveCancelled)
                continue;

            try {
                listener.method.invoke(listener.instance, event);
            } catch (Exception e) {
                System.err.println("[SledgeMC] Event handler error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return event;
    }

    public String getName() {
        return name;
    }

    public int getListenerCount() {
        return listeners.values().stream().mapToInt(List::size).sum();
    }

    private void sortListeners(Class<? extends Event> eventClass) {
        List<RegisteredListener> list = listeners.get(eventClass);
        if (list != null) {
            list.sort(Comparator.comparingInt(l -> l.priority.getValue()));
        }
    }

    private static class RegisteredListener {
        final Object instance;
        final Method method;
        final Priority priority;
        final boolean receiveCancelled;

        RegisteredListener(Object instance, Method method, Priority priority, boolean receiveCancelled) {
            this.instance = instance;
            this.method = method;
            this.priority = priority;
            this.receiveCancelled = receiveCancelled;
        }
    }
}
