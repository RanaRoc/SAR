package impl;

import code.Broker;
import code.Channel;

public class ChannelImpl extends Channel {
	String name;
	
	protected boolean disconnected = false;
	protected boolean waiting;

	public ChannelImpl(Broker br, int port) {
		super(br);
		this.port = port;
		this.buffR = new CircularBuffer(64);
	}

	void connect(ChannelImpl ch, String name) {
		this.ch = ch;
		ch.ch = this;
		this.buffW = ch.buffR;
		ch.buffW = this.buffR;
		this.name = name;
	}

	public synchronized int read(byte[] bytes, int offset, int length) throws Exception {
		if (disconnected) {
			throw new Exception(" Channel is disconnected");
		}
		int read = 0;
		try {
		while (read == 0) {
			if (buffR.empty()) {
				synchronized (buffR) {
					while (buffR.empty()) {
						if (disconnected || waiting)
							throw new Exception(" Disconnected or waiting when trying to read");
						try {
							buffR.wait();
						} catch (InterruptedException e) {

						}
					}
				}
			}
			while (read < length && !buffR.empty()) {
				byte b = buffR.pull();
				bytes[offset + read] = b;
				read++;
			}
			if (read != 0)
				synchronized (buffR) {
					buffR.notify();
				}
		}
		}catch(Exception e ) {
			if(!disconnected) {
				disconnected = true;
				synchronized(buffW) {
					buffW.notifyAll();
				}
			}
		throw e;
		}
		return read;
	}
	public synchronized int write(byte[] bytes, int offset, int length) throws Exception {
	    if (disconnected) {
	        throw new Exception("Channel is disconnected");
	    }
	    int written = 0;

	    try {
	        while (written < length) {
	            if (buffW.full()) {
	                synchronized (buffW) {
	                    while (buffW.full()) {
	                        if (disconnected || waiting) {
	                            throw new Exception("Disconnected or waiting when trying to write");
	                        }
	                        try {
	                            buffW.wait();
	                        } catch (InterruptedException e) {
	                            // Handle the interruption as needed
	                        }
	                    }
	                }
	            }

	            while (written < length && !buffW.full()) {
	                buffW.push(bytes[offset + written]);
	                written++;
	            }

	            if (written > 0) {
	                synchronized (buffW) {
	                    buffW.notify();
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

	    return written;
	}


	public void disconnect() {
		synchronized (this) {
			if (disconnected)
				return;
			disconnected = true;
			ch.waiting = true;

		}
		synchronized (buffW) {
			buffW.notifyAll();
		}
		synchronized (buffR) {
			buffR.notifyAll();
		}
	}

	@Override
	public boolean disconnected() {
		return disconnected;
	}
}
