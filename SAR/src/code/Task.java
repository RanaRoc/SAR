package code;

import code.Broker;

public class Task extends Thread {
    private final Broker broker;
    private final Runnable task;

    public Task(Broker broker, Runnable task) {
        this.broker = broker;
        this.task = task;
    }

    public Broker getBroker() {
        return broker;
    }

    @Override
    public void run() {
        task.run();
    }
}
