package test;

import code.QueueBroker;
import code.Broker;
import code.Broker;
import code.BrokerManager;
import code.MessageQueue;

public class EchoServerQueue extends Thread {
    private final QueueBroker broker;
    private final int port;

    public EchoServerQueue(QueueBroker broker, int port) {
        this.broker = broker;
        this.port = port;
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                MessageQueue mq = broker.accept(port);
                System.out.println("Server accepted a connection on port " + port);

                // Echo back received bytes
                for (int i = 0; i < 255; i++) {
                    byte[] receivedBytes = mq.receive();
                    if (receivedBytes.length > 0) {
                        System.out.println("Server received: " + receivedBytes[0]);
                        // Echo back the received byte
                        mq.send(receivedBytes, 0, receivedBytes.length);
                    }
                }

                mq.disconnect();
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();

        try {
            Thread.sleep(100); // Give server a moment to start

            MessageQueue mq = broker.connect("TestBroker", port);
            if (mq != null) {
                System.out.println("Client connected to the server on port " + port);

                // Send bytes from 1 to 255 and check the echo
                for (int i = 1; i <= 255; i++) {
                    byte[] bytesToSend = new byte[]{(byte) i}; // Send the byte i
                    mq.send(bytesToSend, 0, bytesToSend.length);
                    System.out.println("Client sent: " + bytesToSend[0]);

                    // Receive the echoed byte
                    byte[] echoedBytes = mq.receive();
                    if (echoedBytes.length > 0) {
                        System.out.println("Client received: " + echoedBytes[0]);
                        // Check if received byte matches the sent byte
                        if (echoedBytes[0] == bytesToSend[0]) {
                            System.out.println("Echo test passed for byte: " + i);
                        } else {
                            System.out.println("Echo test failed for byte: " + i);
                        }
                    }
                }

                mq.disconnect();
            } else {
                System.err.println("Failed to connect to the broker.");
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        BrokerManager brokerManager = new BrokerManager();
        Broker broker = new Broker(brokerManager, "TestBroker");
        QueueBroker qb = new QueueBroker(broker);
        EchoServerQueue test = new EchoServerQueue(qb, 8080);
        test.start();
    }
}
