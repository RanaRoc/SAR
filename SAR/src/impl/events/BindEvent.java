package impl.events;

import code.QueueBroker;
import code.QueueBroker.AcceptListener;

public class BindEvent extends Event{
	
	private QueueBroker br;
	private int port;
	private AcceptListener l;
	
	public BindEvent(QueueBroker br, int port, AcceptListener listener) {
		super();
		this.br = br;
		this.port = port;
		this.l = listener;
		super.post(this);
	}

	@Override
	public void run() {
		if(this.br.bind(port, l)) {
			this.kill();
		}
	}

}