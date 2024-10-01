package impl;

import java.util.HashMap;
import java.util.Map;
import code.Broker;
import code.Channel;
public class BrokerImpl extends Broker {

public Map<Integer,Rdv> Rdvs = new HashMap<>();
public BrokerManager bm;

public BrokerImpl(BrokerManager bm,String Name) {
super(Name);
this.bm = bm;
	bm.registerBroker(this);
}

@Override
public Channel accept(int port) {
	Rdv rdv = null;
	System.out.println("Attempting accept");
	synchronized(Rdvs) {
		rdv = Rdvs.get(port);
		if(rdv!=null) {
			throw new IllegalStateException("Already accepting from port : "+port);
			
		}
		rdv = new Rdv();
		Rdvs.put(port, rdv);

		Rdvs.notifyAll();
	}
	Channel ch;
	ch = rdv.accept(this,port);
	return ch;
}

public Channel connect(String Name, int port) {
	BrokerImpl br = (BrokerImpl) this.bm.getBroker(Name);
	if(br==null) {
		return null;
	}
	System.out.println("attempting first connect");
	return br.subConnect(this, port);
}
private Channel subConnect(BrokerImpl br, int port) {
Rdv rdv = null;
synchronized (Rdvs) {
	rdv = Rdvs.get(port);
	while(rdv==null) {
		try {
			Rdvs.wait();
		} catch(Exception e) {
	}
		rdv =  Rdvs.get(port);
		System.out.println("trying to get Rdv");
}
	Rdvs.remove(port);
}
return rdv.connect(br,port);
}
}
