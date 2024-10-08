package impl.events;

import code.QueueBroker;
import code.QueueBroker.ConnectListener;
import impl.BrokerImpl;
import impl.QueueBrokerImpl;
import impl.QueueBrokerManager;

public class ConnectEvent extends Event {
		
		private String brName;
		private int port;
		private ConnectListener l;
		
		public ConnectEvent(String brokerName, int port, ConnectListener listener) {
			super();
			this.brName = brokerName;
			this.port = port;
			this.l = listener;
			super.post(this);

		}

		@Override
		public void run() {
			QueueBrokerImpl br = QueueBrokerManager.getInstance().getBroker(this.brName);
			if(br == null) {
				this.l.refused();
				this.kill();
			}else if(br.assureConnect(port, l)) {
					this.kill();
				}
			}
			
		

	
}
