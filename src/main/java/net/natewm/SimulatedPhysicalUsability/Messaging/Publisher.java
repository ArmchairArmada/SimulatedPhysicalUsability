package net.natewm.SimulatedPhysicalUsability.Messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 4/11/2017.
 */
public class Publisher {
    List<Subscriber> subscriberList = new ArrayList<>();

    public Publisher() {
    }

    public synchronized void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
    }

    public synchronized void publish(Message message) {
        for (Subscriber subscriber: subscriberList) {
            subscriber.receive(message);
        }
    }
}
