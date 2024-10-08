package impl;

import java.util.HashMap;

public class QueueBrokerManager {
		private static final QueueBrokerManager INSTANCE = new QueueBrokerManager();
		
		private HashMap<String, QueueBrokerImpl> _brokers;
		
		private QueueBrokerManager() {
			_brokers = new HashMap<String, QueueBrokerImpl>();
		}
		
		public static QueueBrokerManager getInstance() {
	        return INSTANCE;
	    }
		
		public QueueBrokerImpl getBroker(String name) {
			QueueBrokerImpl broker =  _brokers.get(name);
			
			return broker;
		}
		
		public void registerBroker(QueueBrokerImpl broker) {
			
			String name = broker.name();
			
			if(_brokers.containsKey(name)) {
				throw new IllegalStateException("Two Brokers with the same name");
			}
			
			this._brokers.put(name, broker);
		

	}

}
