package code;

public abstract class MessageQueue extends Channel{
public MessageQueue(Broker br) {
		super(br);
	}
abstract void send(byte[] bytes, int offset, int length);
abstract byte[] receive();
abstract void close();
abstract boolean closed();
}