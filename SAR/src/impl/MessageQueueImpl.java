package impl;

import code.Broker;
import code.Channel;

public class MessageQueueImpl extends ChannelImpl{
	
	public MessageQueueImpl(Channel ch, int port) {
		super(ch.br,port);
		this.buffW = ch.getBuffW();
		this.buffR = ch.getBuffR();
		this.ch = ch.getCh();
	}
	public synchronized void send(byte[] bytes, int offset, int length) throws Exception {
        if (disconnected) {
            throw new Exception("Channel is disconnected");
        }

        if (length > buffW.m_capacity) {
            throw new Exception("Message too large for buffer");
        }

        int written = 0;

        try {
            while (written < length) {
                if (buffW.full()) {
                    synchronized (buffW) {
                        while (buffW.full()) {
                            if (disconnected || waiting) {
                                throw new Exception("Disconnected or waiting when trying to send");
                            }
                            buffW.wait();
                        }
                    }
                }

                while (written < length && !buffW.full()) {
                    buffW.push(bytes[offset + written]);
                    written++;
                }

                if (written > 0) {
                    synchronized (buffW) {
                        buffW.notifyAll();
                    }
                }
            }
        } catch (Exception e) {
            if (!disconnected) {
                disconnected = true;
                synchronized (buffR) {
                    buffR.notifyAll();
                }
            }
            throw e;
        }
    }
	public synchronized byte[] receive() throws Exception {
	    if (disconnected) {
	        throw new Exception("Channel is disconnected");
	    }

	    // Create a buffer to hold the received bytes
	    byte[] buffer = new byte[buffR.m_capacity]; // Use the capacity of the circular buffer
	    int read = 0;

	    try {
	        while (true) {
	            // Wait if the buffer is empty
	            while (buffR.empty()) {
	                if (disconnected || waiting) {
	                    throw new Exception("Disconnected or waiting when trying to receive");
	                }
	                synchronized (buffR) {
	                    buffR.wait();
	                }
	            }

	            // Read from the circular buffer
	            while (!buffR.empty() && read < buffer.length) {
	                buffer[read] = buffR.pull(); // Pull the byte from the circular buffer
	                read++;
	            }

	            // Notify any waiting senders if we have read data
	            if (read > 0) {
	                synchronized (buffR) {
	                    buffR.notifyAll();
	                }
	            }

	            // If we have received at least one byte, break the loop
	            if (read > 0) {
	                break;
	            }
	        }

	        // Create a new array to return the actual number of bytes read
	        byte[] receivedBytes = new byte[read];
	        System.arraycopy(buffer, 0, receivedBytes, 0, read);
	        return receivedBytes;
	    } catch (Exception e) {
	        if (!disconnected) {
	            disconnected = true;
	            synchronized (buffW) {
	                buffW.notifyAll();
	            }
	        }
	        throw e;
	    }
	}


	public void close() {
		super.disconnect();
	}

	public boolean closed() {
		return super.disconnected;
	}

}
