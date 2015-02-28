/**
 * 
 */
package ca.syncron.app.connect.utils;

import naga.NIOSocket;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Dawson
 *
 */
// jsonMsg = {message_type: "digital", sender_type:"node",value:"0"}
public class User implements SocketObserver, ComConstants {
	public final static Logger	log				= LoggerFactory.getLogger(User.class.getName());
	private final static long	LOGIN_TIMEOUT		= 30 * 100000;
	private final static long	INACTIVITY_TIMEOUT	= 500 * 60 * 1000;
	public final ServerTcp		m_server;
	private final NIOSocket		m_socket;
	private String				m_name;
	private DelayedEvent		m_disconnectEvent;
	public   String		targetMsg			= "EMPTY MESSAGE";

	public User(ServerTcp server, NIOSocket socket) {
		m_server = server;
		m_socket = socket;
		getSocket().setPacketReader(new AsciiLinePacketReader());
		getSocket().setPacketWriter(new AsciiLinePacketWriter());
		getSocket().listen(this);
		m_name = null;
	}

	public void connectionOpened(NIOSocket nioSocket) {
		// We start by scheduling a disconnect event for the login.
		m_disconnectEvent = m_server.getEventMachine().executeLater(new Runnable() {
			public void run() {
				getSocket().write("Disconnecting due to inactivity".getBytes());
				getSocket().closeAfterWrite();
			}
		}, LOGIN_TIMEOUT);

		// Send the request to log in.
		nioSocket.write(sysREGISTER_REQUEST.getBytes());
	//	nioSocket.write("Please enter your name:".getBytes());
	}

	public String toString() {
		return m_name != null ? m_name + "@" + getSocket().getIp() : "anon@" + getSocket().getIp();
	}

	public void connectionBroken(NIOSocket nioSocket, Exception exception) {
		// Inform the other users if the user was logged in.
		if (m_name != null) {
			m_server.broadcast(this, m_name + " left the chat.");
		}
		// Remove the user.
		m_server.removeUser(this);
	}

	private void scheduleInactivityEvent() {
		// Cancel the last disconnect event, schedule another.
		if (m_disconnectEvent != null) m_disconnectEvent.cancel();
		m_disconnectEvent = m_server.getEventMachine().executeLater(new Runnable() {
			public void run() {
				getSocket().write("Disconnected due to inactivity.".getBytes());
				getSocket().closeAfterWrite();
			}
		}, INACTIVITY_TIMEOUT);
	}

	// Received
	// ///////////////////////////////////////////////////////////////////////////////////

	public void packetReceived(NIOSocket socket, byte[] packet) {

		String message = new String(packet).trim();

		if (message.length() == 0) return;

		// Reset inactivity timer.
		scheduleInactivityEvent();

		// ONLY happens on first connect
		// In this protocol, the first line entered is the name.
		if (m_name == null) {
			m_name = message;
		//	m_server.connectedClients.putIfAbsent(m_name, this);
			System.out.println(this + " logged in.");
			m_server.broadcast(this, m_name + " has connected.");
			getSocket().write(("Welcome " + m_name + ". There are " + m_server.m_users.size() + " user(s) currently logged in.").getBytes());
			return;
		}

		MessageTcp msg = new MessageTcp(this, message);
		m_server.incomingBuffer.addToQue(msg);

		System.out.println("msg received from -> " + m_name + "\n -> " + message);
		// m_server.broadcast(this, m_name + ": " + message);
	}
	 
	// ///////////////////////////////////////////////////////////////////////////////////


	public void packetSent(NIOSocket socket, Object tag) {
		System.out.println("EVENT	packetSent");
	}

	public void sendBroadcast(byte[] bytesToSend) {
		// Only send broadcast to users logged in.
		if (m_name != null) {
			getSocket().write(bytesToSend);
		}

	}

	public void sendToTarget(byte[] bytesToSend, User target) {
		// send to specific client
		if (target != null) {
			target.getSocket().write(bytesToSend);
		}
	}

	public void sendToTarget(String targetId, String msgString) {

		if (m_server.connectedClients.containsKey(targetId)) {
			User target = m_server.connectedClients.get(targetId);
			target.getSocket().write(msgString.getBytes());
			System.out.println("sending relay to " + targetId);
		}
	}

	public void sendMessage(String msgString) {
		getSocket().write(msgString.getBytes());
	}

	/**
	 * @return object m_socket of type NIOSocket
	 */
	public NIOSocket getSocket() {
		return m_socket;
	}
	// public void setSocket(NIOSocket socket) {
	// m_socket = socket;
	// }

	/**
	 * @return object m_name of type String
	 */
	public String getName() {
		return this.m_name;
	}

	/**
	 * @param m_name the m_name to set
	 */
	public void setName(String m_name) {
		this.m_name = m_name;
	}

}
