package code;

public abstract class Channel {
	Broker br;
	public Channel(Broker br) {
		this.br = br;
	}
	public abstract int read(byte[] bytes, int offset, int length) throws Exception;
	public abstract int write(byte[] bytes, int offset, int length) throws Exception;
	public abstract void disconnect();
	public abstract boolean disconnected();
}