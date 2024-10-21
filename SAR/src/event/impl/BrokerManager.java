package event.impl;

import java.util.HashMap;
import java.util.Map;


public class BrokerManager {
	private static final BrokerManager INSTANCE = new BrokerManager();
	private Map<String, Broker> brs;
	private BrokerManager() {
		this.brs = new HashMap<String,Broker>();
	}
	public static BrokerManager getInstance() {
		return INSTANCE;
	}
	
	 public synchronized Broker getBroker(String name) {
	        Broker targetBroker = (Broker) brs.get(name);
	        
	        return targetBroker;
	    }
	    
	    public synchronized void addBroker(Broker broker) {
	        
	        Broker brokerImpl = (Broker) broker;
	        String brokerName = brokerImpl.name();
	        
	        if (brs.containsKey(brokerName)) {
	            throw new IllegalStateException("Trying to add a broker that has the same name");
	        }
	        
	        this.brs.put(brokerName, broker);
	    }
}
