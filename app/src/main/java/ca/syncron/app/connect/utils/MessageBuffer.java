/**
 * 
 */
package ca.syncron.app.connect.utils;

import java.util.LinkedList;

/**
 * @author Dawson
 *
 */
public class MessageBuffer<Message> {

	// public MessageBuffer msgBuffer;//= new MessageBuffer();

	public LinkedList<Message>	mLinkedList;	// = new LinkedList<>();

	/**
	 * 
	 */
	public MessageBuffer() {
		mLinkedList = new LinkedList<>();
	}

	public synchronized void addToQue(Message msg) {
		mLinkedList.addLast(msg);
		System.out.println("  Msg added to List");
		if (queSize() >= 1) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	public synchronized Message nextFromQue() {
		return mLinkedList.pollFirst();

	}

	public synchronized int queSize() {
		int size = mLinkedList.size();
		
		if(size > 1) 
			System.out.println("\t\t\t\t\t\t\t\t buffer size = " + size);
		return size;
	}

	public boolean queEmpty() {
		return queSize() == 0;
	}
}
