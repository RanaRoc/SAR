package event.impl;

public class Event implements Runnable{

	private final Task t;
	private final Runnable r;
	public Event(Task t, Runnable r) {
		this.t=t;
		this.r=r;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.r.run();
	}

	public Task getTask() {
		return this.t;
	}
	
}
