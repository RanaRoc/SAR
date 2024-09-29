package test;

import code.Broker;
import code.Channel;
import impl.BrokerImpl;
import impl.BrokerManager;

public class EchoServer extends Thread {
    private final BrokerImpl broker;
    private final int port;

    public EchoServer(BrokerImpl broker, int port) {
        this.broker = broker;
        this.port = port;
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                Channel channel = broker.accept(port);
                System.out.println("Server accepted a connection on port " + port);
                
                byte[] buffer = new byte[256];
                int bytesRead;
                while ((bytesRead = channel.read(buffer, 0, buffer.length)) > 0) {
                    channel.write(buffer, 0, bytesRead);  
                }
                Thread.sleep(1000);

                channel.disconnect(); 
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }).start();

        try {
            Thread.sleep(100); 
            Channel channel = broker.connect("TestBroker", port);
            if (channel != null) {
                System.out.println("Client connected to the server on port " + port);

                for (int i = 1; i <= 255; i++) {
                    byte[] bytesToSend = new byte[]{(byte) i};
                    channel.write(bytesToSend, 0, bytesToSend.length);
                    
                    byte[] buffer = new byte[1];
                    int bytesRead = channel.read(buffer, 0, buffer.length);
                    
                    if (bytesRead == 1 && buffer[0] == (byte) i) {
                        System.out.println("Echoed: " + (i));
                    } else {
                        System.err.println("Error: Expected " + i + " but got " + (bytesRead == 1 ? buffer[0] : "nothing"));
                    }
                }
                Thread.sleep(1000);
                channel.disconnect();   } else {
                System.err.println("Failed to connect to the broker.");
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        BrokerManager brokerManager = new BrokerManager();
        BrokerImpl broker = new BrokerImpl(brokerManager, "TestBroker");
        EchoServer test = new EchoServer(broker, 8080);
        test.start();
    }
}
