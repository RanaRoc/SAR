package code;

public class Channel {
	public int read(byte[] bytes, int offset, int length) throws IllegalStateException {
		return 1;
	}
    public int write(byte[] bytes, int offset, int length) throws IllegalStateException{
    	return 1;
    }
    public void disconnect() {
    	
    }
    public boolean disconnected() {
    	return false;
    }

}
