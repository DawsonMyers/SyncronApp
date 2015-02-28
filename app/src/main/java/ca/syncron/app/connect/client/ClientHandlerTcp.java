/**
 * 
 */
package ca.syncron.app.connect.client;


import ca.syncron.app.connect.utils.MessageBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Dawson
 *
 */
public class ClientHandlerTcp extends  AbstractHandler {
	public final static Logger                                 log      = LoggerFactory.getLogger(ClientHandlerTcp.class.getName());
	public static       Receiver                               receiver = null;
	public static       Sender                                 sender   = null;
	public static       AndroidClientTcp client   = null;

	public ClientHandlerTcp() {
		client = AndroidClientTcp.getInstance();
		startMsgHandlers();
	}

	@Override
	public MessageBuffer<ClientMsg> getIncomingBuffer() {
		return null;
	}

	@Override
	public MessageBuffer<ClientMsg> getOutgoingBuffer() {
		return null;
	}

	@Override
	public void startMsgHandlers() {
		(receiver = new Receiver(this)).start();

		(sender = new Sender(this)).start();
	}

	@Override
	public void handleIncomingMessage(ClientMsg msg) {}

	@Override
	public void handleOutgoingMessage(ClientMsg msg) {}

	@Override
	public void sendMessage(ClientMsg msg) {}

	//	Callbacks
	// ///////////////////////////////////////////////////////////////////////////////////
	@Override
	public void handleDigitalMessage(ClientMsg msg) {

		System.out.println("Digital message processed");
	}

	@Override
	public void handleAnalogMessage(ClientMsg msg) {}

	@Override
	public void handleAdminMessage(ClientMsg msg) {}

	@Override
	public void handleUpdateMessage(ClientMsg msg) {}

	@Override
	public void handleRegisterMessage(ClientMsg msg) {}

	@Override
	public void handleStatusMessage(ClientMsg msg) {}

	@Override
	public void handleLoginMessage(ClientMsg msg) {}

	@Override
	public void handleUserMessage(ClientMsg msg) {}

	@Override
	public void implementedMapConfig() {}
	public void addToQue(ClientMsg msg) {
		receiver.msgBuffer.addToQue(msg);
	}
	public void addToQue(String strMsg) {
		ClientMsg msg = new ClientMsg(client,strMsg);
		receiver.msgBuffer.addToQue(msg);
	}
}

class Receiver extends AbstractDispatcher {
	public Receiver(AbstractHandler handler) {
		super(handler);
	}

	@Override
	public void handleMessage() {
		ClientMsg msg = msgBuffer.nextFromQue();
		tcpHandler.processMessage(msg);
	}

	@Override
	public void sendMessage(ClientMsg msg) {}

}

class Sender extends AbstractDispatcher {
	public Sender(AbstractHandler handler) {
		super(handler);
	}

	@Override
	public void handleMessage() {
		if (msgBuffer.queSize() > 0) {
			ClientMsg msg = (ClientMsg) msgBuffer.nextFromQue();
			new Thread(() -> sendMessage(msg), "ClentSender").start();

		}
	}

	@Override
	public void sendMessage(ClientMsg msg) {
		cHandler.client.sendMessage(msg.getJsonMsg());
	}



}