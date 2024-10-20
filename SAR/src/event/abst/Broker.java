package event.abst;

public abstract class Broker {
	public Broker(String name) {
	}
	public abstract String name();

	public interface AcceptListener {
		void accepted(Channel queue);
	}

	public abstract boolean bind(int port, AcceptListener listener);

	public abstract boolean unbind(int port);

	public interface ConnectListener {
		void connected(Channel queue);

		void refused();
	}

	public abstract boolean connect(String name, int port, ConnectListener listener);
}