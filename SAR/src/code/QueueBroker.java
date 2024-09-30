package code;

public class QueueBroker extends Broker{
 public QueueBroker(Broker broker) {
	 super(null, "");
 }
 String name() {
	return Name;
	 
 }
public  MessageQueue accept(int port) {
	return null;
	
}
public  MessageQueue connect(String name, int port) {
	return null;
}
}