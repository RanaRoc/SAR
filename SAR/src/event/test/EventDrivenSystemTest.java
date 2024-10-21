package event.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import event.impl.Broker;
import event.impl.Channel;
import event.impl.ChannelListener;
import event.impl.ConnectListener;
import event.impl.Task;
import mixed.impl.Message;

public class EventDrivenSystemTest {

    @Test
    public void testBrokerBindAndAccept() {
        Broker broker = new Broker("TestBrokerBindAndAccept");  // Use a unique name
        final boolean[] isAccepted = {false};

        broker.bind(8080, queue -> {
            isAccepted[0] = true;
        });

        broker.connect("localhost", 8080, new ConnectListener() {
            @Override
            public void connected(event.impl.Channel queue) {
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

        channel.setListener(new ChannelListener() {
            @Override
            public void received(byte[] bytes) {
                assertEquals("TestMessage", new String(bytes), "Received message should match the sent message.");
                messageReceived[0] = true;
            }

            @Override
            public void closed() {}

            @Override
            public void sent(byte[] bytes) {}
        });

        channel.send("TestMessage".getBytes());

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
        Broker broker = new Broker("TestBrokerConnectAndSendMessage");  // Use a unique name
        final boolean[] messageReceived = {false};

        broker.bind(8080, queue -> {
            queue.setListener(new ChannelListener() {
                @Override
                public void received(byte[] bytes) {
                    assertEquals("Hello", new String(bytes), "Broker should receive the correct message.");
                    messageReceived[0] = true;
                }

                @Override
                public void closed() {}

                @Override
                public void sent(byte[] bytes) {}
            });
        });

        broker.connect("localhost", 8080, new ConnectListener() {
            @Override
            public void connected(event.impl.Channel queue) {
                queue.send("Hello".getBytes());
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
