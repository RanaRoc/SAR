package code;

public abstract class MessageQueue extends Channel{
public MessageQueue(Broker br) {
		super(br);
	}
public abstract void send(byte[] bytes, int offset, int length);
public abstract byte[] receive();
abstract void close();
abstract boolean closed();
}