package code;



public class MessageQueue extends Channel{
public MessageQueue(Broker br) {
		super(br);
	}

public interface Listener {
	void received(byte[] msg);
	void sent(Message msg);
	void closed();
}
 public void setListener(Listener l) {
	 
 }
 public boolean send(Message msg) {
	 return false;
 }
 public void close() {
	 
 }
 public boolean closed() {
	 return false;
 }
@Override
public int read(byte[] bytes, int offset, int length) throws Exception {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public int write(byte[] bytes, int offset, int length) throws Exception {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void disconnect() {
	// TODO Auto-generated method stub
	
}
@Override
public boolean disconnected() {
	// TODO Auto-generated method stub
	return false;
}
}