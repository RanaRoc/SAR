package event.abst;

import event.impl.ChannelListener;
import mixed.impl.Message;

public abstract class Channel {
	
	public abstract void setListener(ChannelListener listener);

	public abstract boolean send(byte[] bytes);

	public abstract boolean receive(byte[] bytes);

	public abstract void close();

	public abstract boolean closed();
}