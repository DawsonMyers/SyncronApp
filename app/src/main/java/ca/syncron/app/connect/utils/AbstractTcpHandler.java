/**
 * 
 */
package ca.syncron.app.connect.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author Dawson
 *
 */
public abstract class AbstractTcpHandler extends Thread implements ComConstants {

	public final static Logger				log					= LoggerFactory.getLogger(AbstractTcpHandler.class.getName());

	public static int						counter				= 0;
	public static MsgTimer					timer				= new MsgTimer();
	public static volatile Map<String, User>	connectedClients		= new HashMap<>();										// new
																												// HashMap<>();
	public static volatile Map<String, User>	connectedNodeClients	= new HashMap<>();
	public static volatile Map<String, User>	connectedAndroidClients	= new HashMap<>();

	public Map<String, Object>				implementedMap			= null;
	// public static volatile MessageBuffer<Msg> incomingMsgBufferPACKET = new
	// MessageBuffer<Msg>();
	// public static volatile MessageBuffer<MessageTcp> incomingMsgBufferPACKET
	// = new MessageBuffer<MessageTcp>();
	// public static volatile MessageBuffer<MessageTcp> outgoingMsgBuffer = new
	// MessageBuffer<MessageTcp>();

	// public static UdpMessenger udpMessenger;
	public static Thread					udpMessengerThrd;
	public static AbstractTcpHandler			handler;

	// Started in ArdulinkSerial

	// public static void main(String[] args) {
	// handler = new UdpHandler();
	//
	// }

	public abstract MessageBuffer<MessageTcp> getIncomingBuffer();

	public abstract MessageBuffer<MessageTcp> getOutgoingBuffer();

	public abstract void startMsgHandlers();

	public abstract void handleIncomingMessage(MessageTcp msg);

	public abstract void handleOutgoingMessage(MessageTcp msg);

	public abstract void sendMessage(MessageTcp msg);

	//
	// ///////////////////////////////////////////////////////////////////////////////////

	public AbstractTcpHandler() {
		log.info("Logger started");

	}

	public void run() {
		// udpMessenger = new UdpMessenger(this);
		//
		// udpMessengerThrd = new Thread(udpMessenger);
		// udpMessengerThrd.start();

		log.info("Starting Handlers");
		// startMsgHandlers();
	}

	public void processMessage(MessageTcp msg) {
		log.debug("Processing received message");

		new Thread(() -> {
			String type = msg.getType();
			switch (type) {
				case tDIGITAL:
					handleDigitalMessage(msg);
					break;
				case tANALOG:
					handleAnalogMessage(msg);
					break;
				case tADMIN:
					handleAdminMessage(msg);
					break;
				case tUPDATE:
					handleUpdateMessage(msg);
					break;
				case tREGISTER:
					handleRegisterMessage(msg);
					break;
				case tSTATUS:
					handleStatusMessage(msg);
					break;
				case tLOGIN:
					handleLoginMessage(msg);
					break;
				case tUSER:
					handleUserMessage(msg);
					break;

				default:
					log.error("message could not be identified");
					break;
			}
		}).start();
	}

	/**
	 * Abstract callback methods that are triggered whenever the corresponding
	 * message type is received. The methods must be implemented differently
	 * according to whether it is being run on the node, server, or android
	 * 
	 * @param msg
	 */
	public abstract void handleDigitalMessage(MessageTcp msg);

	public abstract void handleAnalogMessage(MessageTcp msg);

	public abstract void handleAdminMessage(MessageTcp msg);

	public abstract void handleUpdateMessage(MessageTcp msg);

	public abstract void handleRegisterMessage(MessageTcp msg);

	public abstract void handleStatusMessage(MessageTcp msg);

	public abstract void handleLoginMessage(MessageTcp msg);

	public abstract void handleUserMessage(MessageTcp msg);

	public abstract void implementedMapConfig();

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
