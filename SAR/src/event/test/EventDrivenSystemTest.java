package event.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import event.impl.Broker;
import event.impl.Channel;
import event.impl.Task;
import mixed.impl.Message;

public class EventDrivenSystemTest {

    @Test
    public void testBrokerBindAndAccept() {
        Broker broker = new Broker("TestBroker");
        final boolean[] isAccepted = {false};

        broker.bind(8080, queue -> {
            isAccepted[0] = true;  
        });

        broker.connect("localhost", 8080, new Broker.ConnectListener() {
            @Override
            public void connected(event.abst.Channel queue) {
            }

            @Override
            public void refused() {
                fail("Connection was refused.");
            }
        });

        assertTrue(isAccepted[0], "Connection should be accepted by the broker.");
    }

    @Test
    public void testChannelSendAndReceive() {
        Channel channel = new Channel();
        final boolean[] messageReceived = {false};

        channel.setListener(new Channel.Listener() {
            @Override
            public void received(byte[] bytes) {
                assertEquals("TestMessage", new String(bytes), "Received message should match the sent message.");
                messageReceived[0] = true;
            }

            @Override
            public void closed() {
            }

            @Override
            public void sent(Message message) {
            }
        });

        Message message = new Message("TestMessage".getBytes());
        channel.send(message);

        assertTrue(messageReceived[0], "The message should be received through the channel.");
    }

    @Test
    public void testTaskPostAndKill() {
        Task task = new Task();
        final boolean[] taskRan = {false};

        task.post(() -> {
            taskRan[0] = true;  
        });

        assertTrue(taskRan[0], "The task should have run the posted Runnable.");

        task.kill();
        assertTrue(task.killed(), "The task should be marked as killed.");
    }

    @Test
    public void testBrokerConnectAndSendMessage() {
        Broker broker = new Broker("TestBroker");
        final boolean[] messageReceived = {false};

        broker.bind(8080, queue -> {
            queue.setListener(new Channel.Listener() {
                @Override
                public void received(byte[] bytes) {
                    assertEquals("Hello", new String(bytes), "Broker should receive the correct message.");
                    messageReceived[0] = true;
                }

                @Override
                public void closed() {}

                @Override
                public void sent(Message message) {}
            });
        });

        broker.connect("localhost", 8080, new Broker.ConnectListener() {
            @Override
            public void connected(event.abst.Channel queue) {
                Message message = new Message("Hello".getBytes());
                queue.send(message);
            }

            @Override
            public void refused() {
                fail("Connection was refused.");
            }
        });

        assertTrue(messageReceived[0], "The message should be received by the broker after connection.");
    }

    public static void main(String[] args) {
        EventDrivenSystemTest testSuite = new EventDrivenSystemTest();
        testSuite.testBrokerBindAndAccept();
        testSuite.testChannelSendAndReceive();
        testSuite.testTaskPostAndKill();
        testSuite.testBrokerConnectAndSendMessage();
        System.out.println("All tests passed!");
    }
}
