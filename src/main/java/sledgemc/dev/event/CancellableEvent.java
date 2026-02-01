package sledgemc.dev.event;

/**
 * Base class for events that can be cancelled.
 */
public abstract class CancellableEvent extends Event {

    protected CancellableEvent() {
        super();
    }

    protected CancellableEvent(String name) {
        super(name);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }
}
