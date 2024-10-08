package code;


public abstract class Task extends Thread{
	
	private QueueBroker br;
	private Runnable r;
	
	public Task(QueueBroker broker, Runnable runnable) {
		this.br = broker;
		this.r = runnable;
	}
		 	
	protected QueueBroker getBroker() {
		return this.br;
	}
	
	protected static Task getTask() {
		return (Task) Thread.currentThread();
	}

	@Override
	public void run() {
		this.r.run();
	}
	

}

