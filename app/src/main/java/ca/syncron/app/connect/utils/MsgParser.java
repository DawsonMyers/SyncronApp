/**
 * 
 */
package ca.syncron.app.connect.utils;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Dawson
 *
 */
public class MsgParser {
	public final static Logger	log			= LoggerFactory.getLogger(MsgParser.class.getName());
	public static JSONObject		json;
	public Map				msgMap;
	public static String		jsonStartToken	= "{message_type:";
	public static String		jsonEndToken	= "}";

	/**
	 * 
	 */
	public MsgParser() {}


//	public static Map<String, Object> parseMsg(MsgPacket msgPacket) {
//
//		String jsonString = msgPacket.getJsonMsg();
//		log.info("Parsing JSON string");
//		JsonParserFactory factory = JsonParserFactory.getInstance();
//		JSONParser parser = factory.newJsonParser();
//		Map<String, Object> jMap = parser.parseJson(jsonString);
//		msgPacket.setjMap(jMap);
//
//		return jMap;
//
//	}

	/**
	 * @param messageTcp
	 *             jsonMsg = {message_type: "digital", sender_type:"node",value:"0"}
	 *             
	 */
	public static Map<String, Object> parseMsg(MsgMetaData messageTcp) {
		String jsonString = messageTcp.getJsonMsg();
		if (jsonString.startsWith(jsonStartToken) && jsonString.endsWith(jsonEndToken)) {

			try {
				log.info("Parsing JSON string");
				JsonParserFactory factory = JsonParserFactory.getInstance();
				JSONParser parser = factory.newJsonParser();
				Map<String, Object> jMap = parser.parseJson(jsonString);
				messageTcp.setjMap(jMap);
				return jMap;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Error parsing JSON message");
				SyncUtils.getDateBox();
				return null;
			}
		} else {
log.warn("Not a JSON string");
			return null;
		}
	}

}
