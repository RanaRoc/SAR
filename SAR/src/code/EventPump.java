package code;

interface Event {
void react();
}
abstract class EventPump extends Thread {
public abstract void post(Event e);
public abstract void start();
public abstract void kill();
}