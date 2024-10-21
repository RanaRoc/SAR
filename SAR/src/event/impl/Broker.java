package event.impl;

import java.util.HashMap;
import java.util.Map;

public class Broker extends event.abst.Broker {

	BrokerManager bm;
	String name;
	Map<Integer, AcceptListener> queue ;
	
	public Broker(String name) {
		this.bm = BrokerManager.getInstance();
		this.name = name;
		this.bm.addBroker(this);
		this.queue = new HashMap<Integer,AcceptListener>();
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		if(this.queue.containsKey(port))
			return false;
		this.queue.put(port, listener);
		return true;
	}

	@Override
	public boolean unbind(int port) {
		if(this.queue.containsKey(port))
			return false;
		this.queue.remove(port);
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		Broker br = this.bm.getBroker(name);
		if(br==null) {
			listener.refused();
			return false;
		}
		br.ensureConnect(port, listener);
		return true;
	}

	private void ensureConnect(int port, ConnectListener listener) {
		if(this.queue.containsKey(port)) {
			Channel chA = new Channel();
			Channel chC = new Channel();
			
			chA.buffOut = chC.buffIn;
			chC.buffOut = chA.buffIn;
			
			chA.remoteCh = chC;
			chC.remoteCh = chA;
			
			listener.connected(chC);
			this.queue.get(port).accepted(chA);
		} else
			new Task().post(()->ensureConnect(port,listener));
	}

}
