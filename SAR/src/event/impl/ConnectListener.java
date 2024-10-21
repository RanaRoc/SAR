package event.impl;

public interface ConnectListener {
	public void connected(Channel channel);
	public void refused();
}
