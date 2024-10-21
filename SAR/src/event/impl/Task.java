package event.impl;

import java.util.ArrayList;

public class Task extends event.abst.Task{

	private boolean killed;
	EventPump ep;
	ArrayList<Event> events;
	
	public Task() {
		this.killed = false;
		this.ep = EventPump.getInstance();
		this.events = new ArrayList<Event>();
	}
	@Override
	public void post(Runnable r) {
		// TODO Auto-generated method stub
		Event ev = new Event(this,r);
		this.events.add(ev);
		this.ep.post(ev);
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		this.killed = true;
	}

	@Override
	public boolean killed() {
		// TODO Auto-generated method stub
		return this.killed;
	}
	public static Task task() {
		Event e = EventPump.currEvent;
		if(e !=  null) {
			return e.getTask();
		}
		return null;
	}


}
