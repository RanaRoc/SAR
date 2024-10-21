package event.impl;

import java.util.ArrayList;

public class EventPump {
	static final EventPump INSTANCE = new EventPump();
	
	ArrayList<Event> events;
	static Event currEvent;
	
	EventPump(){
		this.events = new ArrayList<Event>();
	}
	
	public static EventPump getInstance() {
		return INSTANCE;
	}
	public void post(Event e) {
		this.events.add(e);
	}
}
