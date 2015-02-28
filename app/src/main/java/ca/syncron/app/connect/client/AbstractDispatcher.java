/**
 * 
 */
package ca.syncron.app.connect.client;


import ca.syncron.app.connect.utils.MessageBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.syncron.app.connect.utils.SyncUtils;
/**
 * @author Dawson
 *
 */
public abstract class AbstractDispatcher extends Thread implements Runnable {


	public final static Logger                   log        = LoggerFactory.getLogger(AbstractDispatcher.class.getName());
	public volatile     MessageBuffer<ClientMsg> msgBuffer  = new MessageBuffer<>();
	//public volatile MessageBuffer<ClientMsg>	msgBuffer		= new MessageBuffer<ClientMsg>();
	public              AbstractHandler          tcpHandler = null;
	public              ClientHandlerTcp         cHandler   = null;

	/**
	 *
	 */
	public AbstractDispatcher() {}

	// using super so that the node, server, and android implementations will
	// all have access
	public AbstractDispatcher(AbstractHandler handler) {
		if (handler instanceof ClientHandlerTcp) cHandler = (ClientHandlerTcp) handler;
		tcpHandler = handler;
	}
//	public AbstractDispatcher(ClientHandlerTcp handler) {
//		cHandler = handler;
//	}

	public AbstractDispatcher(MessageBuffer<ClientMsg> messageBuffer) {
		msgBuffer = messageBuffer;
	}

	@Override
	public void run() {
		log.info("started");
		while (true) {
			if (msgBuffer.queEmpty()) {
				synchronized (msgBuffer) {
					try {
						msgBuffer.wait();
						log.info("NOTIFIED");
					} catch (InterruptedException e) {
						e.printStackTrace();
						SyncUtils.getDateBox();
					}
				}
			} else {
				log.info("processing a msg");

				handleMessage() ;
				//new Thread(() -> handleMessage()).start();
			}
		}
	}

	public abstract void handleMessage();

	public abstract void sendMessage(ClientMsg msg);

}
