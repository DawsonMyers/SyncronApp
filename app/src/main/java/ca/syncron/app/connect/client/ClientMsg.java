/**
 * 
 */
package ca.syncron.app.connect.client;


import ca.syncron.app.connect.utils.ComConstants;
import ca.syncron.app.connect.utils.MsgMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dawson
 *
 */
public class ClientMsg extends MsgMetaData implements ComConstants {
	public final static Logger log = LoggerFactory.getLogger(ClientMsg.class.getName());

	public ClientMsg() {}

	public ClientMsg(AndroidClientTcp client, String msg) {
		mClient = client;
		clientHandler = client.handler;
		setJsonMsg(msg);
		parseJsonToMap();
	}

}
