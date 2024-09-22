package code;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {
Map<String,Broker> brokers;

public BrokerManager() {
	this.brokers = new HashMap<>();
}
public void registerBroker(Broker br) {
	this.brokers.put(br.Name, br);
}

public void removeBroker(Broker br) {
	this.brokers.remove(br.Name);
}

public Broker getBroker(String Name) {
	return brokers.get(Name);
}
}
