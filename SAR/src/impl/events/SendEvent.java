package impl.events;

import impl.Message;
import impl.MessageQueueImpl;

public class SendEvent extends Event {
	private MessageQueueImpl q;
	private Message msg;
	
	public SendEvent(MessageQueueImpl queue, Message msg) {
		super();
		this.q = queue;
		this.msg = msg;
		super.post(this);

	}

	@Override
	public void run() {
		if(this.q.assureSend(this.msg)) {
			this.kill();
		}
	}
}
