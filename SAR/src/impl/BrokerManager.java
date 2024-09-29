package impl;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {
Map<String,BrokerImpl> brokers;

public BrokerManager() {
	this.brokers = new HashMap<>();
}
public void registerBroker(BrokerImpl br) {
	this.brokers.put(br.Name, br);
}

public void removeBroker(BrokerImpl br) {
	this.brokers.remove(br.Name);
}

public BrokerImpl getBroker(String Name) {
	return brokers.get(Name);
}
}
