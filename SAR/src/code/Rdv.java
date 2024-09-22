package code;

public class Rdv {
Broker ba,bc;
int port;
Channel channel;
private boolean isConnected=true;
public Rdv(Broker ba, Broker bc, int port) {
	this.ba = ba;
	this.bc = bc;
	this.port = port;
}

public synchronized Channel connect() throws InterruptedException  {
	while(channel==null) {
		wait();
	}
	isConnected = true;
	notifyAll();
	return channel;
}
public synchronized Channel accept() throws InterruptedException {
	if(channel!=null) {
		throw new IllegalStateException("Already connected !");
	}
	
	channel = new Channel(new CircularBuffer(255), new CircularBuffer(255) );
	notifyAll();
	while(!isConnected) {
		wait();
	}
	return channel;
}
}
