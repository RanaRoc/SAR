package tests;

import code.Broker;
import code.Channel;

public class Tests {
	
public static void main(String[] args) {
	successfullyConnectedTest();
	failingConnectionTest();
	disconnectTest();
	writeTest();
	writeDisconnectTest();
	System.out.println("All tests were successfull !");
}

private static void successfullyConnectedTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
	assert(channel!=null) : " This should normally return a non-null channel, there is an error with the connect method";
	System.out.println("TEST 1 PASSED !");
}

private static void failingConnectionTest() {
	Broker client = new Broker("Client");
	Channel channel = client.connect("DoesntExist",4048);
	assert(channel==null) : " This should normally return a null channel, there is an error with the connect method";
	System.out.println("TEST 2 PASSED !");
	Channel channel1 = client.accept(4000);
	assert(channel1==null) : " This should normally return a null channel, there is an error with the accept method";
	System.out.println("TEST 3 PASSED !");

}

private static void disconnectTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
	channel.disconnect();
	assert(channel.disconnected()) : "Disconnect should normally return true, check an error in disconnect or disconnected";
	System.out.println("TEST 4 PASSED !");
	channel.disconnect();
	assert(channel.disconnected()) : "Disconnect should normally return true even after disconnecting twice, check an error in disconnect or disconnected";
	System.out.println("TEST 5 PASSED !");

}
static void writeTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
    byte[] data = new byte[10];
    int length = channel.write(data, 0, data.length);

    assert(length == data.length) : "Expected to write " + data.length + " bytes, but got " + length;
	System.out.println("TEST 6 PASSED !");

}

private static void writeDisconnectTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
	channel.disconnect();
	try {
    channel.write(new byte[10], 0, 10);
    assert false : "Expected IllegalStateException because of disconnected channel";
    } catch (IllegalStateException e) {
    	System.out.println("TEST 7 PASSED !");
    }
}

static void readTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
    byte[] data = new byte[10];
    int length = channel.read(data, 0, data.length);

    assert(length == data.length) : "Expected to read " + data.length + " bytes, but got " + length;
	System.out.println("TEST 8 PASSED !");

}

private static void readDisconnectTest() {
	Broker client = new Broker("Client");
	Broker server = new Broker("Server");
	Channel channel = client.connect("Server",2024);
	channel.disconnect();
	try {
    channel.read(new byte[10], 0, 10);
    assert false : "Expected IllegalStateException because of disconnected channel";
    } catch (IllegalStateException e) {
    	System.out.println("TEST 9 PASSED !");
    }
}
}
