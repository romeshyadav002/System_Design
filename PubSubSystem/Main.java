package PubSubSystem;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static class Message {
        private final String payload;

        Message(String payload) {
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }
    }

    public static interface Subscriber {
        public void consume(Message message);
    }

    public static class PrintSubscriber implements Subscriber {
        private final String name;

        PrintSubscriber(String name) {
            this.name = name;
        }

        public void consume(Message message) {
            System.out.println("printSubscriber " + name + " message " + message.getPayload());
        }
    }

    public static class LoggingSubscriber implements Subscriber {
        private final String name;

        public LoggingSubscriber(String name) {
            this.name = name;
        }

        @Override
        public void consume(Message message) {
            System.out.println("[LOG] " + name + " received: " + message.getPayload());
        }
    }

    public static class Topic {
        private final String name;
        private Set<Subscriber> subscribers;

        Topic(String name) {
            this.name = name;
            subscribers = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public void addSubscriber(Subscriber subscriber) {
            subscribers.add(subscriber);
        }

        public void removeSubscriber(Subscriber subscriber) {
            subscribers.remove(subscriber);
        }

        public void broadcast(Message message) {
            for (Subscriber subscriber : subscribers) {
                Dispatcher.dispatch(subscriber, message);
            }
        }
    }

    public static class Dispatcher {
        public static void dispatch(Subscriber subscriber, Message message) {
            subscriber.consume(message);
        }
    }

    public static class Publisher {
        private final String id;
        private final String name;
        private final Broker broker;

        Publisher(String name,Broker broker) {
            this.name = name;
            this.id = UUID.randomUUID().toString();
            this.broker = broker;
        }

        public void publish(String topic, String payload) {
            broker.publish(topic, new Message(payload));
        }

        public String getId() {
            return id;
        }
    }

    public static class Broker {
        private final Map<String, Topic> topics = new ConcurrentHashMap<>();

        public void createTopic(String name) {
            topics.putIfAbsent(name, new Topic(name));
        }

        public void subscribe(String topicName, Subscriber subscriber) {
            Topic topic = topics.get(topicName);
            if (topic == null)
                throw new IllegalArgumentException("Topic not found: " + topicName);
            topic.addSubscriber(subscriber);
        }

        public void unsubscribe(String topicName, Subscriber subscriber) {
            Topic topic = topics.get(topicName);
            if (topic != null)
                topic.removeSubscriber(subscriber);
        }

        public void publish(String topicName, Message message) {
            Topic topic = topics.get(topicName);
            if (topic == null)
                throw new IllegalArgumentException("Topic not found: " + topicName);
            topic.broadcast(message);
        }
    }
    
    public static void main(String[] args) {
        // Create Broker
        Broker broker = new Broker();

        // Create Topics
        broker.createTopic("topic1");
        broker.createTopic("topic2");

        // Create publishers
        Publisher publisher1 = new Publisher("publisher1", broker);
        Publisher publisher2 = new Publisher("publisher2", broker);

        // Create subscribers
        Subscriber subscriber1 = new PrintSubscriber("PrintSubscriber1");
        Subscriber subscriber2 = new PrintSubscriber("PrintSubscriber2");
        Subscriber subscriber3 = new LoggingSubscriber("LoggingSubscriber3");

        // Subscribe to topics
        broker.subscribe("topic1", subscriber1);
        broker.subscribe("topic1", subscriber2);
        broker.subscribe("topic2", subscriber3);

        // Publish messages
        publisher1.publish("topic1", "Message1 for Topic1");
        publisher1.publish("topic1", "Message2 for Topic1");
        publisher1.publish("topic2", "Message1 for Topic2");

        // Unsubscribe from a topic
        broker.unsubscribe("topic1", subscriber2);

        // Publish more messages
        publisher1.publish("topic1", "Message3 for Topic1");
        publisher2.publish("topic2", "Message2 for Topic2");

       
    }
}
