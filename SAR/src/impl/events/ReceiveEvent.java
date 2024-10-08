package impl.events;

import code.MessageQueue;
import impl.MessageQueueImpl;

public class ReceiveEvent extends Event {
private MessageQueueImpl mq;
	
	public ReceiveEvent(MessageQueueImpl q) {
		super();
		this.mq = q;
		super.post(this);
	}

	@Override
	public void run() {
		if(this.mq.assureReceive()) {
			this.kill();
		}
	}
}
