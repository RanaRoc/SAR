package code;

public abstract class QueueBroker {
	
	private String name;
	
	public QueueBroker(String name) {
		this.name = name;
	}
	
	public  String name() {
		return name;
	}
    
    public interface AcceptListener {
        void accepted(MessageQueue queue);
    }
    
    public interface ConnectListener {
        void connected(MessageQueue queue);
        void refused();
    }
    
    public abstract boolean unbind(int port);
    
    public abstract boolean bind(int port, AcceptListener listener);
    
    public abstract boolean connect(String name, int port, ConnectListener listener);

}
