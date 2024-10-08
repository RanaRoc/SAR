package impl;

import java.util.HashMap;
import java.util.Map;

import code.Broker;
import code.MessageQueue;
import code.QueueBroker;
import impl.events.Event;
import impl.events.UnbindEvent;
import impl.events.BindEvent;
import impl.events.ConnectEvent;
public class QueueBrokerImpl extends QueueBroker {
	
	    private QueueBrokerManager brokerManager;
	    private String name;
	    private Map<Integer, AcceptListener> accepts;

	    public QueueBrokerImpl(String name) {
	        super(name);
	        this.name = name;
	        this.brokerManager = QueueBrokerManager.getInstance();
	        this.brokerManager.registerBroker(this);
	        this.accepts = new HashMap<Integer, QueueBroker.AcceptListener>();
	    }

	    @Override
	    public boolean bind(int port, AcceptListener listener) {
	        BindEvent event = new BindEvent(this, port, listener);
	        return true;
	    }

	    public boolean assureBind(int port, AcceptListener listener) {
	        if (accepts.containsKey(port)) {
	            return false;
	        }
	        accepts.put(port, listener);
	        return true;
	    }

	    @Override
	    public boolean unbind(int port) {
	        UnbindEvent event = new UnbindEvent(this, port);
	        return true;
	    }

	    public boolean assureUnbind(int port) {
	        accepts.remove(port);
	        return true;
	    }

	    @Override
	    public boolean connect(String name, int port, ConnectListener listener) {
	        ConnectEvent event = new ConnectEvent(name, port, listener);
	        return true;
	    }

	    public boolean assureConnect(int port, ConnectListener listener) {
	        if (accepts.containsKey(port)) {
	        	BrokerImpl br = BrokerManager.getInstance().getBroker(name);
	            ChannelImpl channel1 = new ChannelImpl(br,port);
	            ChannelImpl channel2 = new ChannelImpl(br,port);

	            channel1.buffW = channel2.buffR;
	            channel2.buffW = channel1.buffR;

	            channel1.ch = channel2;
	            channel2.ch = channel1;

	            MessageQueueImpl queue1 = new MessageQueueImpl(channel1);
	            MessageQueueImpl queue2 = new MessageQueueImpl(channel2);

	            queue1.setMsq(queue2);
	            queue2.setMsq(queue1);

	            listener.connected(queue1);
	            accepts.get(port).accepted(queue2);

	            return true;
	        }
	        return false;
	    }
	


}
