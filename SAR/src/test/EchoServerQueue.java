package test;

import code.QueueBroker;
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
                
                byte[] buffer = new byte[256];
                int bytesRead;
                while ((bytesRead = mq.read(buffer, 0, buffer.length)) > 0) {
                    mq.write(buffer, 0, bytesRead);  
                }
                Thread.sleep(1000);

                mq.disconnect(); 
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();

        try {
            Thread.sleep(100); 
            MessageQueue mq = broker.connect("TestBroker", port);
            if (mq != null) {
                System.out.println("Client connected to the server on port " + port);

                for (int i = 1; i <= 255; i++) {
                    byte[] bytesToSend = new byte[]{(byte) i};
                    mq.write(bytesToSend, 0, bytesToSend.length);
                    
                    byte[] buffer = new byte[1];
                    int bytesRead = mq.read(buffer, 0, buffer.length);
                    
                    if (bytesRead == 1 && buffer[0] == (byte) i) {
                        System.out.println("Echoed: " + (i));
                    } else {
                        System.err.println("Error: Expected " + i + " but got " + (bytesRead == 1 ? buffer[0] : "nothing"));
                    }
                }
                Thread.sleep(1000);
                mq.disconnect();   } else {
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
