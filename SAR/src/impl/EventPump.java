package impl;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import impl.events.Event;

public class EventPump extends Thread {
    
    private static final EventPump INSTANCE = new EventPump();

    private boolean isActive;
    private Queue<Runnable> taskQueue;
    private static Runnable currentTask;
    private ExecutorService executorService;
    
    private EventPump() {
        taskQueue = new LinkedList<>();
        executorService = Executors.newSingleThreadExecutor();
        isActive = true;
    }
    @Override
    public void run() {
        while (isActive) {
            synchronized (this) {
                while (this.isTaskQueueEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

            this.fetchNext();
        
            try {
                currentTask.run();
            } catch (Exception e) {
            	
            }
            
            if (!Event.getTask().killed()) {
                this.post(currentTask);
            }
        }
    }
    public static EventPump getInstance() {
        return INSTANCE;
    }
    synchronized private Runnable fetchNext() {
        Runnable task = taskQueue.poll();
        currentTask = task;
        return task;
    }
    
    public static Runnable getCurrentTask() {
        return currentTask;
    }
    

    synchronized private boolean isTaskQueueEmpty() {
        return taskQueue.isEmpty();
    }
    

    synchronized public void post(Runnable task) {
        taskQueue.add(task);
        this.notify();
    }

    synchronized public void unpost(Runnable task) {
        taskQueue.remove(task);
    }

    
    public void terminateDispatcher() {
        this.isActive = false;
    }
    
   
}
