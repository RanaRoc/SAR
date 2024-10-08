package code;

public class QueueBroker extends Broker{
 public QueueBroker(String name) {
	 super(null, "");
 }
 
 public interface AcceptListener {
	 void accepted(MessageQueue queue);
 }
  public boolean bind(int port, AcceptListener listener) {
	  return false;
  }
  boolean unbind(int port) {
	 return false;
 }

 public interface ConnectListener {
	 void connected(MessageQueue queue);
	 void refused();
 }
  public boolean connect(String name, int port, ConnectListener listener) {
	  return false;
  }
}