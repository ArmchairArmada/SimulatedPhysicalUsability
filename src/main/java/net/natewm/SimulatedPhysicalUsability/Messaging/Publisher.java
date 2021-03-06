package net.natewm.SimulatedPhysicalUsability.Messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Publisher for a publisher/subscriber model (not currently used).
 */
public class Publisher {
    private final List<MessageQueue> subscriberList = new ArrayList<>();

    public Publisher() {
    }

    public synchronized void subscribe(MessageQueue subscriber) {
        subscriberList.add(subscriber);
    }

    public synchronized void publish(Message message) {
        for (MessageQueue subscriber: subscriberList) {
            subscriber.receive(message);
        }
    }
}
