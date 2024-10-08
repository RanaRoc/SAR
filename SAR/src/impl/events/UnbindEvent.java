package impl.events;

import impl.QueueBrokerImpl;

public class UnbindEvent extends Event{
	private QueueBrokerImpl br;
	private int port;
	
	public UnbindEvent(QueueBrokerImpl broker, int port) {
		super();
		this.br = broker;
		this.port = port;
		super.post(this);
	}

	@Override
	public void run() {
		if(this.br.assureUnbind(port)) {
			this.kill();
		}
	}

}
