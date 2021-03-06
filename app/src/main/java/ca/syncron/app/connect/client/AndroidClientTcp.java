/**
 *
 */
package ca.syncron.app.connect.client;

import android.util.Log;
import ca.syncron.app.connect.utils.ComConstants;
import ca.syncron.app.service.SyncronService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.eventmachine.EventMachine;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Dawson
 */
public class AndroidClientTcp extends Thread implements SocketObserver, ComConstants {
	public final static Logger log = LoggerFactory.getLogger(AndroidClientTcp.class.getName());
	public static final String  msgDIGITAL_TEMP = "{message_type: \"digital\",sender_type:\"android\",value:\"<value>\",pin:\"<pin>\",target_id:\"node\"}";
	public static final String  PIN             = "<pin>";
	public static final String  VALUE           = "<value>";
	public static final String  ipLocal         = "192.168.1.109";
	public static final int     port            = 6500;
	public static       boolean isConnected     = false;
	public static NIOSocket socket;
	public static AndroidClientTcp mClient  = null;
	public static SyncronService   mService = null;
	public EventMachine mEventMachine;
	public ClientHandlerTcp handler = null;
	public String           host    = IP_SERVER;//IP_LOCAL;
	String id = this.getClass().getSimpleName();

	public static boolean isScheduled = false;

	public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();// = Executors.newFixedThreadPool(5);


	//Log.d(id,"sleeping");
	//  {message_type: "digital",sender_type:"node",value:"0",pin:"3",target_id:"android"}
	//  "{message_type: \"digital\",sender_type:\"android\",value:\"<value>\",pin:\"<pin>\",target_id:\"node\"}"
	// public NodeClientTcp() {
	// }
	public AndroidClientTcp() {}

	public AndroidClientTcp(SyncronService syncronService) {
		mService = syncronService;
		Log.d(id, "constructor");
		//private ScheduledExecutorService scheduler;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		//scheduler.scheduleAtFixedRate(new SomeTask(), 0, 10, TimeUnit.MINUTES);


	}

	public AndroidClientTcp(EventMachine machine) {
		mEventMachine = machine;
		mClient = this;
		handler = new ClientHandlerTcp();
	}

	public static AndroidClientTcp getInstance() {return mClient;}

	public void init(EventMachine machine) {
		mEventMachine = machine;
		mClient = this;
		handler = new ClientHandlerTcp();
	}

	@Override
	public void run() {

		connect();
		//public static void main(String[] args) {
//		int port = 6500;// Integer.parseInt(args[0]);
//		InetSocketAddress address = new InetSocketAddress(host, port);
//		try {
//			Log.d(id, "Run()");
//			EventMachine machine = new EventMachine();
//			// InetAddress ip = InetAddress.getByName(HTTP_SERVER);
//			InetAddress ip = InetAddress.getByName(ipLocal);
//			socket = machine.getNIOService().openSocket(ip, port);
//			socket.listen(new AndroidClientTcp(machine));
//			socket.setPacketReader(new AsciiLinePacketReader());
//			socket.setPacketWriter(new AsciiLinePacketWriter());
//			machine.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


	@Override
	public void connectionOpened(NIOSocket nioSocket) {
		log.info("Connected to server");
		setConnected(true);
		Log.d(id, "Connected");
		mService.toast("Connected to server");
	}

	@Override
	public void connectionBroken(NIOSocket nioSocket, Exception exception) {
		log.info("Disconnected from server");
		setConnected(false);
		mService.toast("Disconnected from server");
		Log.d(id, "Disconnected");

		mService.disconnected();

		//scheduler.scheduleAtFixedRate(() -> connect(), 0, 5, TimeUnit.SECONDS);
		//isScheduled = true;

	}

	@Override
	public void packetReceived(NIOSocket socket, byte[] packet) {
		log.info("packet received");
		String message = new String(packet).trim();
		mService.toast("Message received from server");
		if (message.length() == 0) return;
		// register name with server
		if (message.contains(sysREGISTER_REQUEST)) {
			sendMessage(sysID_ANDROID);
			log.info("Sending registration ID to server");
		}
		System.out.println("Received message: \n->" + message);
		//System.out.println(testMsg);
		//sendMessage("{message_type:\"digital\",sender_type:\"node\",value:\"TEST FROM NODE\",target_id:\"android\"}");

		if (isJsonMsg(message) && message.contains(CHAT_token)) {
			//mService.
			String chatMsg = extract(message, VALUEt);
			System.out.println("Chat message received:" + "\n" + chatMsg);
			mService.chatReceived(chatMsg);
			return;
		}

		handler.addToQue(message);
	}

	@Override
	public void packetSent(NIOSocket socket, Object tag) {
		mService.toast("Message set to server");
		log.info("Packet sent");
	}

	public void sendMessage(String msg) {
		socket.write(msg.getBytes());
	}

	public void sendDigitalMessage(String pin, String value) {
		String msg = msgDIGITAL_TEMP.replace(PIN, pin).replace(VALUE, value);
		//mService.toast("Sending message: \n" + msg);
		Log.e(id, msg);
		if (socket != null && socket.isOpen()) {
			socket.write(msg.getBytes());
		} else {
			connect();

		}
		//socket.write(msg.getBytes());
	}

	private void connect() {
		InetSocketAddress address = new InetSocketAddress(host, port);
		try {
			Log.d(id, "Run()");
			EventMachine machine = new EventMachine();
			// InetAddress ip = InetAddress.getByName(HTTP_SERVER);
			InetAddress ip = InetAddress.getByName(ipLocal);
			socket = machine.getNIOService().openSocket(ip, port);
			socket.listen(new AndroidClientTcp(machine));
			socket.setPacketReader(new AsciiLinePacketReader());
			socket.setPacketWriter(new AsciiLinePacketWriter());
			machine.start();
			if (socket.isOpen()) mService.mConnected = true;
			//if(isScheduled && socket.isOpen()) scheduler.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String extract(String msg, String token) {
		String value = "";
		if (msg != null & token != null) {
			int i1 = msg.indexOf(token);
			int i2 = msg.indexOf(QUOTEt, i1 + token.length());
			value = msg.substring(i1 + token.length(), i2);
		}
		return value;

	}

	// ///////////////////////////////////////////////////////////////////////////////////

	public boolean isJsonMsg(String message) {
		if (message.startsWith("{message_type:") & message.endsWith("}")) {
			return true;
		}
		return false;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean b) {
		isConnected = b;
		mService.isConnected(b);
	}

	public void sendChatMessage(String msg) {
		if (msg != null) {
			msg = msgCHAT.replace(VALUE, msg);
			sendMessage(msg);
		}
	}
}