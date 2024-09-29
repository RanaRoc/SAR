package impl;

import code.Broker;
import code.Channel;

public class Rdv {
Broker ba,bc;
int port;
ChannelImpl channel1;
ChannelImpl channel2;


public synchronized Channel connect(Broker br, int port)  {
	System.out.println("trying to connect in rdv");
	this.bc = br;
	this.channel1 = new ChannelImpl(br,port);
	if(channel2!=null) {
		channel2.connect(channel1,bc.getName());
		notify();
	}else {
		CWait();
	}
	return channel1;
}
public synchronized Channel accept(Broker br, int port)  {
	this.ba = br;
	this.channel2 = new ChannelImpl(br,port);
	if(channel1!=null) {
		channel2.connect(channel1,bc.getName());
		notify();
	}else {
		CWait();
	}
	return channel2;
}
private void CWait() {
	while(channel1==null || channel2==null) {
		try {
			wait();
		}catch(InterruptedException e) {
			
		}
	}
}
}
