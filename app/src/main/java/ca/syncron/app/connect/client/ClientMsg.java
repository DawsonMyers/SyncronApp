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

	public String getChat() {
		return extract(value, VALUEt);
	}

	public String extract(String msg, String token) {
		String value = "";
//		if (msg != null & token != null) {
//			int i1 = msg.indexOf(token);
//			int i2 = msg.indexOf(QUOTEt, i1 + token.length());
//			if(i1>i2) return "EXTRACT_ERROR";
//			value = msg.substring(i1 + token.length(), i2);
//			return value;
//		}
		return "EXTRACT_ERROR";
	}


}
