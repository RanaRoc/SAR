package code;

import code.Broker;

abstract class Task extends Thread {
Task(Broker b, Runnable r){
	
}
Task(QueueBroker b, Runnable r){
	
}
abstract Broker getBroker();
abstract QueueBroker getQueueBroker();
 static Task getTask() {
	 return null;
 }
}
