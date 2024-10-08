package impl;


public class Message {
	
	private byte[] bytes;
    private int offset;
    private int length;
	
	public Message(byte[] bytes, int offset, int length) {
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}
	
	public Message(byte[] bytes) {
		this.bytes = bytes;
		this.offset = 0;
		this.length = bytes.length;
	}
	
	public Message(int length) {
		this.bytes = new byte[length];
		this.offset = 0;
		this.length = length;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public int getLength() {
		return this.length;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}


	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
	@Override
	public String toString() {
		return new String(bytes);
	}
	
	public byte getByte(int i) {
		return bytes[i];
	}
	
	public void setByte(byte b, int i) {
		bytes[i] = b;
	}

}
