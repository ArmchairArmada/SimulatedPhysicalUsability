package net.natewm.SimulatedPhysicalUsability;

import net.natewm.SimulatedPhysicalUsability.Messaging.Message;
import net.natewm.SimulatedPhysicalUsability.Messaging.MessageQueue;
import net.natewm.SimulatedPhysicalUsability.Messaging.Publisher;

/**
 * Created by Nathan on 4/21/2017.
 */
public class TestMain {
    private class Producer implements Runnable {
        public Publisher publisher = new Publisher();

        public Producer() {
        }

        @Override
        public void run() {
            System.out.println("Producer");
            publisher.publish(new Message(Message.MsgType.SIM_PLAY, null));
            publisher.publish(new Message(Message.MsgType.AGENT_ARRIVED, "Agent"));
            publisher.publish(new Message(Message.MsgType.SIM_STOP, null));
        }
    }


    private class Consumer implements Runnable {
        MessageQueue messageQueue = new MessageQueue();
        String label;

        public Consumer(String label, Publisher publisher) {
            this.label = label;
            publisher.subscribe(messageQueue);
        }

        @Override
        public void run() {
            boolean keep_going = true;
            try {
                while (keep_going) {
                    messageQueue.waitForMessages();

                    while (!messageQueue.isEmpty()) {
                        Message message = messageQueue.remove();

                        switch (message.getMsgType()) {
                            case SIM_PLAY:
                                System.out.println(label + ": Playing");
                                break;

                            case SIM_STOP:
                                System.out.println(label + ": Done");
                                keep_going = false;
                                break;

                            case AGENT_ARRIVED:
                                System.out.println(label + ": Agent: " + (String)message.getData());
                                break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public TestMain() {
        Producer producer = new Producer();
        Consumer consumer = new Consumer("A", producer.publisher);
        Consumer consumer2 = new Consumer("B", producer.publisher);
        Thread tConsumer = new Thread(consumer);
        Thread tConsumer2 = new Thread(consumer2);
        Thread tProducer = new Thread(producer);

        tConsumer.start();
        tConsumer2.start();
        tProducer.start();

        try {
            tConsumer.join();
            tConsumer2.join();
            tProducer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        TestMain main = new TestMain();
    }
}
