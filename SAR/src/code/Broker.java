package code;

public abstract class Broker {
	public String Name;
	 protected Broker(String name) {
		this.Name = name;
	}
	public abstract Channel accept(int port);
	public abstract Channel connect(String name, int port);
	public String getName() {
return this.Name;}}