package code;


abstract class Task extends Thread {

abstract void post(Runnable r);
 static Task task() {
	 return null;
 }
abstract void kill();
abstract boolean killed();
}
