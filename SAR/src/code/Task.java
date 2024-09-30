package code;

import code.Broker;
import impl.QueueBrokerImpl;

public class Task extends Thread {
    private final Broker broker;
    private final QueueBrokerImpl qb;
    private final Runnable task;
   

    public Task(Broker broker, Runnable task) {
        this.broker = broker;
		this.qb = null;
        this.task = task;
    }
    public Task(QueueBrokerImpl broker, Runnable task) {
        this.broker = broker.br;
		this.qb = broker;
        this.task = task;
    }

    public Broker getBroker() {
        return broker;
    }
    public QueueBrokerImpl getQueueBroker() {
        return qb;
    }
    static Task getTask() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof Task) {
            return (Task) currentThread;
        }
        return null;
    }
    @Override
    public void run() {
        task.run();
    }
}
