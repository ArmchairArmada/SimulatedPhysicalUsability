package net.natewm.SimulatedPhysicalUsability.Messaging;

/**
 * Created by Nathan on 4/19/2017.
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
