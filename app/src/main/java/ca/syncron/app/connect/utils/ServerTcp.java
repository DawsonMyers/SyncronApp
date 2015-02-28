package ca.syncron.app.connect.utils;

// public class ChatServer {
//
// public static void main(String[] args) {
// // TODO Auto-generated method stub
//
// }
//
// }

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.eventmachine.EventMachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a very simple chat server.
 * <p>
 * Run using {@code java naga.examples.ChatServer [port]}
 *
 * @author Christoffer Lerno
 */
public class ServerTcp extends Thread implements ServerSocketObserver , ComConstants {

	public static MessageBuffer<MessageTcp>		incomingBuffer			= null;
	public static MessageBuffer<MessageTcp>		outgoingBuffer			= null;
	//public static ServerHandlerTcp			mHandler				= ServerHandlerTcp.getInstance();

	public static volatile Map<String, User>	connectedClients		= new HashMap<String, User>();	// new

	public static volatile Map<String, User>	connectedNodeClients	= new HashMap<>();
	public static volatile Map<String, User>	connectedAndroidClients	= new HashMap<>();
	//public static volatile LinkedList<Msg>		MessageQue			= new LinkedList<>();
	private static ServerTcp					mServer				= null;
	public Map<String, Object>				implementedMap			= null;

	public EventMachine						m_eventMachine;
	// private final LinkedBlockingQueue<User> m_users = new
	// LinkedBlockingQueue<>();
	public List<User>						m_users;

	//
	// ///////////////////////////////////////////////////////////////////////////////////
	public ServerTcp() {}

	ServerTcp(EventMachine machine) {
		mServer = this;
		m_eventMachine = machine;
		m_users = new ArrayList<User>();
		// m_users = new ArrayList<User>();
//		mHandler = ServerHandlerTcp.getInstance();
//		mHandler.start();
//		incomingBuffer = mHandler.getIncomingBuffer();
//		outgoingBuffer = mHandler.getOutgoingBuffer();
	}

	@Override
	public void run() {
		/**
		 * @param args
		 */
		// public static void main(String... args) {
		int port = PORT_SERVER_TCP;// Integer.parseInt(args[0]);
		try {
			EventMachine machine = new EventMachine();
			NIOServerSocket socket = machine.getNIOService().openServerSocket(port);
			socket.listen(new ServerTcp(machine));
			socket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
			machine.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// ///////////////////////////////////////////////////////////////////////////////////

	public static ServerTcp getInstance() {
		if (mServer == null) {
			// mServer = new ServerTcp();
		}
		return mServer;

	}

	public void acceptFailed(IOException exception) {
		System.out.println("Failed to accept connection: " + exception);
	}

	public void serverSocketDied(Exception exception) {
		// If the server socket dies, we could possibly try to open a new
		// socket.
		System.out.println("Server socket died.");
		System.exit(-1);
	}

	public void newConnection(NIOSocket nioSocket) {
		// Create a new user to hande the new connection.
		System.out.println("New user connected from " + nioSocket.getIp() + ".");
		m_users.add(new User(this, nioSocket));
	}

	public void removeUser(User user) {
		System.out.println("Removing user " + user + ".");
		m_users.remove(user);
	}

	public void broadcast(User sender, String string) {
		// We convert the packet, then send it to all users except the sender.

		// byte[] bytesToSend = string.getBytes();
		// for (User user : m_users) {
		// if (user != sender) user.sendBroadcast(bytesToSend);
		// }
	}

	public EventMachine getEventMachine() {
		return m_eventMachine;
	}

	public void sendToTarget(String targetId, String msgString) {

		if (connectedClients.containsKey(targetId)) {
			User target = connectedClients.get(targetId);
			target.getSocket().write(msgString.getBytes());
		}
	}

	public static User userLookup(String keyFragment) {
		for (String key : connectedClients.keySet()) {
			if (key.contains(keyFragment)) return connectedClients.get(key);
		}
		return null;
	}

}
