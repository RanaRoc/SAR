package impl;

import code.Broker;
import code.Channel;

public class QueueBrokerImpl extends BrokerImpl{
	public Broker br;
	public QueueBrokerImpl(BrokerManager bm,Broker br) {
		super(bm, br.Name);
		this.br = br;
		this.bm = bm;
	}

	
	public MessageQueueImpl accept(int port) {
		Channel ch =  super.accept(port);
		return new MessageQueueImpl(ch, ch.port);
	}

	@Override
	public MessageQueueImpl connect(String name, int port) {
	Channel ch =  super.connect(name, port);
	return new MessageQueueImpl(ch, ch.port);

	}

}
