package impl.events;

import impl.EventPump;

public abstract class Event implements Runnable {
	
	private boolean killed;
	protected EventPump eventPump;
	
	public Event() {
		eventPump = EventPump.getInstance();
		killed = false;
	}
	

	public void post(Runnable r) {
		eventPump.post(r);
	}

	public void kill() {
		this.killed = true;
	}

	public boolean killed() {
		return killed;
	}

	public static Event getTask() {
		return (Event) EventPump.getCurrentTask();
	}
	
}