/**
 * Tracks the length of time it takes to get data back from the server
 */
package ca.syncron.app.connect.utils;

/**
 * @author Dawson
 *
 */
public class MsgTimer {
	public long startTime = 0;
	public long msgTime = 0;
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	public long finish() {
		msgTime = System.currentTimeMillis() - startTime;
		return msgTime;
	}
	public long finish(long msgTime) {
		msgTime = System.currentTimeMillis() - startTime;
		this.msgTime = msgTime;
		return msgTime;
	}
	public void print() {
		System.out.println("Message time: "+ msgTime + "ms");
	}
	public void print(long msgTime) {
		System.out.println("Message time: "+ msgTime + "ms");
	}
	

}
