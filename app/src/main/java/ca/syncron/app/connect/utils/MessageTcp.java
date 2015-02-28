/**
 *
 */
package ca.syncron.app.connect.utils;

import naga.NIOSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Dawson
 */
public class MessageTcp extends MsgMetaData implements ComConstants {
	public final static Logger	log			= LoggerFactory.getLogger(MessageTcp.class.getName());
	//public AbstractTcpHandler	tcpHandler	= ServerHandlerTcp.getInstance();
	public User				mUser		= null;
	public User				mTargetUser	= null;
	public ServerTcp			mServer		= null;
	public String				targetMsg		= "";

	// jsonMsg = {message_type: "digital", sender_type:"node",value:"0"}

	// Constructors
	// ///////////////////////////////////////////////////////////////////////////////////
	public MessageTcp() {}

	// sending
	public MessageTcp(User user, String jsonMsg) {
		//tcpHandler = ServerHandlerTcp.getInstance();
		mServer = user.m_server;
		setUser(user);
		setJsonMsg(jsonMsg);
		parseJsonToMap();
	}

	public MessageTcp(User user, Map<String, Object> jMap) {
		setUser(user);
		prepareJsonFromMap(jMap);
	}

	// Receiving
	public MessageTcp(String jsonMsg) {
		setJsonMsg(jsonMsg);
		// Also parses
		parseJsonToMap();
	}

	// Processing
	// ///////////////////////////////////////////////////////////////////////////////////
	public boolean isValid() {
		if (getUser() != null && getTargetUser() != null) {
			setTargetMsg(getJsonMsg());
			if (getTargetUser().getSocket().isOpen() && getUser().targetMsg != null) { return true; }
		}
		return false;
	}

	// Member setter/getter
	// ///////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param user
	 */
	public void setUser(User user) {
		mUser = user;
	}

	/**
	 * @return object client of type Client
	 */
	public User getUser() {
		return this.mUser;
	}


	public void setHandler(AbstractTcpHandler tcpHandler) {
		this.tcpHandler = tcpHandler;
	}

	/**
	 * @return object targetUser of type User
	 */
	public User getTargetUser() {
		return this.mTargetUser;
	}

	/**
	 * @param targetUser
	 *             the targetUser to set
	 */
	public void setTargetUser(User targetUser) {
		this.mTargetUser = targetUser;
	}

	public NIOSocket getTargetSock() {
		return mTargetUser != null ? mTargetUser.getSocket() : null;

	}

	/**
	 * @return
	 */
	public String getTargetMsg() {
		return targetMsg;
	}

	public byte[] getTargetMsgBytes() {
		return targetMsg.getBytes();
	}

	/**
	 * @param jsonMsg
	 */
	public void setTargetMsg(String jsonMsg) {
		targetMsg = jsonMsg;
	}

}
