package code;

public class Channel {

	private CircularBuffer buffR;
	private CircularBuffer buffW;
	private boolean disconnected = false;

	public Channel(CircularBuffer buffR, CircularBuffer buffW) {
		this.buffR = buffR;
		this.buffW = buffW;
	}
	public synchronized int read(byte[] bytes, int offset, int length) throws Exception {
		if(disconnected) {
			throw new Exception(" Channel is disconnected");
		}
		int read = 0;
		try {
			for(int i=0;i<length;i++) {
				while(buffR.empty()) {
					wait();
				}
				bytes[offset+i] = buffR.pull();
				read++;
				notifyAll();
			}
		} catch(IllegalStateException e) {
			throw new Exception("Buffer is empty ");
		}
		return read;
	}
	
	public synchronized int write(byte[] bytes, int offset, int length) throws Exception {
		if(disconnected) {
			throw new Exception(" Channel is disconnected");
		}
		int written = 0;
		try {
			for(int i=0;i<length;i++) {
				while(buffW.full()) {
					wait();
				}
				bytes[offset+i] = buffW.pull();
				written++;
				notifyAll();
			}
		} catch(IllegalStateException e) {
			throw new Exception("Buffer is full ");
		}
		return written;
	}
	public synchronized void disconnect() {
		disconnected = true;
		notifyAll();
	}
	public synchronized boolean disconnected() {
		return disconnected;
	}
}
