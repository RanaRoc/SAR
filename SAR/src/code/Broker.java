package code;

import java.util.HashMap;
import java.util.Map;

public class Broker {
public String Name;
public Map<Integer,Rdv> Rdvs = new HashMap<>();
public BrokerManager bm;
public Broker(BrokerManager bm,String Name) {
	this.Name = Name;
	this.bm = bm;
	bm.registerBroker(this);
}


public Channel accept(int port) throws InterruptedException {
	Rdv rdv = getRdv(this,null, port);
	return rdv.accept();
}

public Channel connect(String Name, int port) throws InterruptedException {
	Broker br = this.bm.getBroker(Name);
	if(br==null) {
		return null;
	}
	Rdv rdv = getRdv(this,br,port);
	return rdv.connect();
}
private synchronized Rdv getRdv(Broker bc, Broker ba, int port) {
	return Rdvs.computeIfAbsent(port, p -> new Rdv(bc,ba, p));
	
}
}
