package sledgemc.dev.event;

/**
 * Base class for all events.
 */
public abstract class Event {

    private final String name;
    private boolean cancelled = false;

    protected Event() {
        this.name = getClass().getSimpleName();
    }

    protected Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isCancellable() {
        return false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        if (!isCancellable()) {
            throw new IllegalStateException("Event " + name + " is not cancellable!");
        }
        this.cancelled = cancelled;
    }

    public void cancel() {
        setCancelled(true);
    }
}
