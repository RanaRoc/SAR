package impl;

import code.Broker;
import code.Channel;

public class MessageQueueImpl extends ChannelImpl{

	public MessageQueueImpl(Channel ch, int port) {
		super(ch.br,port);
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

        byte[] buffer = new byte[256]; 
        int read = 0;

        try {
            while (read < buffer.length) {
                if (buffR.empty()) {
                    synchronized (buffR) {
                        while (buffR.empty()) {
                            if (disconnected || waiting) {
                                throw new Exception("Disconnected or waiting when trying to receive");
                            }
                            buffR.wait();
                        }
                    }
                }

                while (read < buffer.length && !buffR.empty()) {
                    buffer[read] = buffR.pull();
                    read++;
                }

                if (read > 0) {
                    synchronized (buffR) {
                        buffR.notifyAll();
                    }
                }
            }

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
