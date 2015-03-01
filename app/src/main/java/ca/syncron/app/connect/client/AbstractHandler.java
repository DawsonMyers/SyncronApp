/**
 * 
 */
package ca.syncron.app.connect.client;


import ca.syncron.app.connect.utils.ComConstants;
import ca.syncron.app.connect.utils.MessageBuffer;
import ca.syncron.app.connect.utils.MsgTimer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dawson
 *
 */
public abstract class AbstractHandler   extends Thread implements ComConstants {

	public final static Logger log = LoggerFactory.getLogger(AbstractHandler.class.getName());

	public static          int                 counter          = 0;
	public static          MsgTimer            timer            = new MsgTimer();
	public static volatile Map<String, Object> connectedClients = new HashMap<>();                                        // new
	                                                                          // HashMap<>();


	public Map<String, Object> implementedMap = null;

	public static Thread          udpMessengerThrd;
	public static AbstractHandler handler;



	public abstract MessageBuffer<ClientMsg> getIncomingBuffer();

	public abstract MessageBuffer<ClientMsg> getOutgoingBuffer();

	public abstract void startMsgHandlers();

	public abstract void handleIncomingMessage(ClientMsg msg);

	public abstract void handleOutgoingMessage(ClientMsg msg);

	public abstract void sendMessage(ClientMsg msg);

	//
	// ///////////////////////////////////////////////////////////////////////////////////

	public AbstractHandler() {
		log.info("Logger started");

	}

	public void run() {

		log.info("Starting Handlers");
		// startMsgHandlers();
	}

	public void processMessage(ClientMsg msg) {
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
				case tCHAT:
					handleChatMessage(msg);
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
	public abstract void handleDigitalMessage(ClientMsg msg);

	public abstract void handleAnalogMessage(ClientMsg msg);

	public abstract void handleAdminMessage(ClientMsg msg);

	public abstract void handleUpdateMessage(ClientMsg msg);

	public abstract void handleRegisterMessage(ClientMsg msg);

	public abstract void handleStatusMessage(ClientMsg msg);

	public abstract void handleLoginMessage(ClientMsg msg);

	public abstract void handleUserMessage(ClientMsg msg);

	public abstract void handleChatMessage(ClientMsg msg);

	public abstract void implementedMapConfig();

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}