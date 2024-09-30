package impl;

import code.Broker;
import code.Channel;

public class QueueBrokerImpl extends BrokerImpl{
	public Broker br;
	protected QueueBrokerImpl(BrokerManager bm,Broker br) {
		super(bm, "Queue" +br.Name);
		this.br = br;
		this.bm = bm;
	}

	
	public MessageQueueImpl accept(int port) {
		ChannelImpl ch =  (MessageQueueImpl) super.accept(port);
		return new MessageQueueImpl(ch, ch.port);
	}

	@Override
	public Channel connect(String name, int port) {
	Channel ch =  super.connect(name, port);
	return new MessageQueueImpl(ch, ch.port);

	}

}
