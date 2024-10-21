package event.abst;

import event.impl.AcceptListener;
import event.impl.ConnectListener;

public abstract class Broker {
	public abstract String name();


	public abstract boolean bind(int port, AcceptListener listener);

	public abstract boolean unbind(int port);

	public abstract boolean connect(String name, int port, ConnectListener listener);
}