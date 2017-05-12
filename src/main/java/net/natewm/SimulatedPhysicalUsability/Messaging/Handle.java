package net.natewm.SimulatedPhysicalUsability.Messaging;

/**
 * Handle for referring to some resource (not currently used)
 *
 * @param <T> Type of resource stored in handle
 */
public class Handle<T> {
    private T object = null;

    public void set(T object) {
        this.object = object;
        notify();
    }

    public T get() {
        return object;
    }

    public boolean isReady() {
        return object != null;
    }

    public void waitForReady() throws InterruptedException {
        synchronized (this) {
            while (object == null) {
                wait();
            }
        }
    }
}
