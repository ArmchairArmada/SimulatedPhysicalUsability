package net.natewm.SimulatedPhysicalUsability.Messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/11/2017.
 */
public class Publisher {
    List<MessageQueue> subscriberList = new ArrayList<>();

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
