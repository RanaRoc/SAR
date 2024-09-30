package code;

public  class Broker {
	public String Name;
	 public Broker(BrokerManager bm,String name) {
		this.Name = name;
	}
	public  Channel accept(int port) {
		return null;
	}
	public Channel connect(String name, int port) {
		return null;
	}
	public String getName() {
return this.Name;}}