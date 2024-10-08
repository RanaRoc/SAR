package test;

import code.BrokerManager;
import code.Message;
import code.MessageQueue;
import code.QueueBroker;

public class QueueBrokerTest {

    private BrokerManager brokerManager;
    private QueueBroker queueBroker;
    private MessageQueue messageQueue;

    public static void main(String[] args) {
        QueueBrokerTest test = new QueueBrokerTest();
        test.setUp();
        test.runTests();
    }

    public void setUp() {
        brokerManager = new BrokerManager();
        queueBroker = new QueueBroker("TestBroker");
        messageQueue = new MessageQueue(queueBroker);
    }

    public void runTests() {
        testQueueBrokerBinding();
        testMessageQueueSend();
        testMessageQueueClose();
        testQueueBrokerConnect();
    }

    public void testQueueBrokerBinding() {
        class TestAcceptListener implements QueueBroker.AcceptListener {
            boolean acceptedCalled = false;

            @Override
            public void accepted(MessageQueue queue) {
                acceptedCalled = true;
            }
        }

        TestAcceptListener listener = new TestAcceptListener();
        boolean bound = queueBroker.bind(8080, listener);
        assert bound : "Binding failed!";
        assert listener.acceptedCalled : "Accept listener not called!";
        System.out.println("QueueBroker binding test passed.");
    }

    public void testMessageQueueSend() {
        class TestListener implements MessageQueue.Listener {
            boolean receivedCalled = false;

            @Override
            public void received(byte[] msg) {
                receivedCalled = true;
            }

           
            @Override
            public void closed() {
                // Not used in this test
            }

			@Override
			public void sent(Message msg) {
				// TODO Auto-generated method stub
				
			}
        }

        TestListener listener = new TestListener();
        messageQueue.setListener(listener);

        Message message = new Message();
        message.bytes = "Hello".getBytes();
        boolean sent = messageQueue.send(message);

        assert sent : "Message sending failed!";
        assert listener.receivedCalled : "Received listener not called!";
        System.out.println("MessageQueue send test passed.");
    }

    public void testMessageQueueClose() {
        class TestListener implements MessageQueue.Listener {
            boolean closedCalled = false;

            @Override
            public void received(byte[] msg) {
                // Not used in this test
            }

            @Override
            public void sent(Message msg) {
                // Not used in this test
            }

            @Override
            public void closed() {
                closedCalled = true;
            }
        }

        TestListener listener = new TestListener();
        messageQueue.setListener(listener);
        messageQueue.close();

        assert listener.closedCalled : "Close listener not called!";
        assert messageQueue.closed() : "MessageQueue not closed properly!";
        System.out.println("MessageQueue close test passed.");
    }

    public void testQueueBrokerConnect() {
        class TestConnectListener implements QueueBroker.ConnectListener {
            boolean connectedCalled = false;

            @Override
            public void connected(MessageQueue queue) {
                connectedCalled = true;
            }

            @Override
            public void refused() {
                // Not used in this test
            }
        }

        TestConnectListener listener = new TestConnectListener();
        boolean connected = queueBroker.connect("TestBroker", 8080, listener);
        assert connected : "Connection failed!";
        assert listener.connectedCalled : "Connect listener not called!";
        System.out.println("QueueBroker connect test passed.");
    }
}
