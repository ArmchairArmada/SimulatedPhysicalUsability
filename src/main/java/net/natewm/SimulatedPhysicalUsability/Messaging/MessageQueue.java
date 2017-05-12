package net.natewm.SimulatedPhysicalUsability.Messaging;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue for storing and retrieving messages (not currently used).
 */
public class MessageQueue {
    private final Queue<Message> messageQueue = new LinkedList<>();
    private boolean newMessage = false;

    public MessageQueue() {
    }

    public synchronized void receive(Message message) {
        messageQueue.add(message);
        newMessage = true;
        notify();
    }

    public synchronized boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    public synchronized Message remove() {
        newMessage = false;
        return messageQueue.remove();
    }

    public void waitForMessages() throws InterruptedException {
        synchronized (this) {
            while (!newMessage) {
                wait();
            }
        }
    }
}
