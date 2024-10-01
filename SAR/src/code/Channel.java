package code;

import impl.ChannelImpl;
import impl.CircularBuffer;

public abstract class Channel {
	public Broker br;
	public int port;
	protected CircularBuffer buffR;
	protected CircularBuffer buffW;
	protected ChannelImpl ch;

	public Channel(Broker br) {
		this.br = br;
	}
	public abstract int read(byte[] bytes, int offset, int length) throws Exception;
	public abstract int write(byte[] bytes, int offset, int length) throws Exception;
	public abstract void disconnect();
	public abstract boolean disconnected();
	public CircularBuffer getBuffW() {
		return this.buffW;
	}
	public CircularBuffer getBuffR() {
		return this.buffR;
	}
	public ChannelImpl getCh() {
		return this.ch;
	}
}