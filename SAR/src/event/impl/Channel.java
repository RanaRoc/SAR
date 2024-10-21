package event.impl;

import java.util.ArrayList;

import mixed.impl.Message;
import threaded.impl.CircularBuffer;

public class Channel extends event.abst.Channel{

	boolean disconnected;
	boolean waiting;
	Channel remoteCh;
	CircularBuffer buffIn, buffOut;
	ChannelListener l;
	ArrayList<byte[]> buffW, buffR;
	@Override
	public void setListener(ChannelListener listener) {
		// TODO Auto-generated method stub
		this.l = listener;
	}
	public Channel() {
		this.disconnected = false;
		this.waiting = false;
		this.buffIn = new CircularBuffer(128);
		this.buffR = new ArrayList<byte[]>();
		this.buffW = new ArrayList<byte[]>();

	}
	@Override
	public boolean send(byte[] bytes) {
		// TODO Auto-generated method stub
		if(this.l==null)
			throw new IllegalStateException("Listener is not set !");
		else if(buffW.size() + bytes.length > 256)
			return false;
		
		this.buffW.add(bytes);
		if(this.buffW.size() <2) {
			if( this.waiting || this.disconnected) 
				ensureSend( bytes,0);
		}
		return true;
		
	}

	private void ensureSend(byte[] bytes, int offset) {
			if(this.disconnected || this.waiting)
				return;
			else if(this.buffOut.full()) {
				new Task().post(()-> ensureSend(bytes, offset));
				return;
			}
			int sent = 0;
			while(sent < bytes.length -offset && !this.buffOut.full()) {
				this.buffOut.push(bytes[sent++]);
			}
			if(sent >= bytes.length - offset) {
				this.l.sent(this.buffW.isEmpty() ? null: this.buffW.remove(0));
				byte [] b = this.buffW.isEmpty() ? null : this.buffW.get(0);
				if(b!=null) {
					new Task().post(()->ensureSend(b, 0));
				}
				return;
			}
			final int newOffset = sent + offset;
			new Task().post(()->ensureSend(bytes,newOffset));
				
	}
	@Override
	public boolean receive(byte[] bytes) {
		// TODO Auto-generated method stub
		if(this.l==null)
			throw new IllegalStateException("Listener is not set !");
		else if(buffR.size() + bytes.length > 256)
			return false;
		
		this.buffR.add(bytes);
		if(this.buffR.size() <2) {
				ensureReceive( bytes,0);
		}
		return true;
		
	}

	private void ensureReceive(byte[] bytes, int offset) {
			if(this.disconnected )
				return;
			else if(this.buffIn.empty()) {
				new Task().post(()-> ensureSend(bytes, offset));
				return;
			}
			int received = 0;
			while(received < bytes.length - offset && !this.buffIn.empty()) {
				bytes[received+ offset]= this.buffIn.pull();
				received++;
			}
			if(received >= bytes.length - offset) {
				this.l.received(this.buffR.isEmpty() ? null: this.buffR.remove(0));
				byte [] b = this.buffR.isEmpty() ? null : this.buffR.get(0);
				if(b!=null) {
					new Task().post(()->ensureSend(b, 0));
				}
				return;
			}
			final int newOffset = received + offset;
			new Task().post(()->ensureSend(bytes, newOffset));
				
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.disconnected = true;
		if(this.l==null)
			throw new IllegalStateException("Listener is not set !");
		this.l.closed();
	}

	@Override
	public boolean closed() {
		// TODO Auto-generated method stub
		return this.disconnected;
	}

}
