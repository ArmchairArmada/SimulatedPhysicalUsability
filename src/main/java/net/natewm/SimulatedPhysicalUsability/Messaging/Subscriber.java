package net.natewm.SimulatedPhysicalUsability.Messaging;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Nathan on 4/11/2017.
 */
public class Subscriber {
    Queue<Message> messageQueue = new LinkedList<>();

    public Subscriber() {
    }

    public synchronized void receive(Message message) {
        messageQueue.add(message);
    }

    public synchronized boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    public synchronized Message remove() {
        return messageQueue.remove();
    }
}
