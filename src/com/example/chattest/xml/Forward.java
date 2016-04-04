package com.example.chattest.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.example.chattest.bean.MessageForward;
import com.example.chattest.bean.Info;
import com.example.chattest.bean.MessageGroup;
import com.example.chattest.bean.MessageResponse;

/**
 *
 * @author ly
 *
 */
public class Forward {
	private static final String TAG = "Forward";
	private static final String DEFAULT_ENCODE = "UTF-8";

	public static MessageForward parser(InputStream is) {
		return parser(is, DEFAULT_ENCODE);
	}

	public static MessageForward parser(InputStream is, String encode) {
		MessageForward msg = null;
		List<Info> infos = null;
		Info info = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, encode);
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String tag = parser.getName();
					if ("Message".equalsIgnoreCase(tag)) {
						msg = new MessageForward();
						infos = new ArrayList<Info>();
					} else if ("Sequence".equalsIgnoreCase(tag)) {
						msg.setSequence(Integer.valueOf(parser.nextText()));
					} else if ("CommandType".equalsIgnoreCase(tag)) {
						msg.setCommandType(parser.nextText());
					} else if ("WhichCommand".equalsIgnoreCase(tag)) {
						msg.setWhichCommand(parser.nextText());
					} else if ("Params".equalsIgnoreCase(tag)) {
						info = new Info();
					} else if ("FileType".equalsIgnoreCase(tag)) {
						info.setFileType(parser.nextText());
					} else if ("SendType".equalsIgnoreCase(tag)) {
						info.setSendType(parser.nextText());
					} else if ("GroupId".equalsIgnoreCase(tag)) {
						info.setGroupId(Integer.valueOf(parser.nextText()));
					} else if ("Identify".equalsIgnoreCase(tag)) {
						info.setIdentify(parser.nextText());
					} else if ("Detail".equalsIgnoreCase(tag)) {
						info.setDetail(parser.nextText());
					} else if ("Time".equalsIgnoreCase(tag)) {
						info.setTime(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					String endTag = parser.getName();
					if ("Params".equalsIgnoreCase(endTag)) {
						infos.add(info);
					} else if ("Message".equalsIgnoreCase(endTag)) {
						msg.setInfos(infos);
					}
					break;
				default:
					break;
				}

				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception : " + e.getMessage());
			msg = null;
		}

		return msg;
	}

	public static String pack(MessageForward forward, int sequence) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			buffer.append("<Message>");
			buffer.append("<Sequence>" + sequence + "</Sequence>");
			buffer.append("<CommandType>REQUEST</CommandType>");
			buffer.append("<WhichCommand>FORWARD</WhichCommand>");

			for (Info info : forward.getInfos()) {
				buffer.append("<Params>");
				buffer.append("<FileType>" + info.getFileType() + "</FileType>");
				buffer.append("<SendType>" + info.getSendType() + "</SendType>");
				buffer.append("<Identify>" + info.getIdentify() + "</Identify>");
				buffer.append("<Detail>" + info.getDetail() + "</Detail>");
				buffer.append("</Params>");
			}
			buffer.append("</Message>");

			return buffer.toString();
		} catch (Exception e) {
			Log.e(TAG, "Exception : " + e.getMessage());
		}
		
		return null;
	}

	public static MessageResponse response(InputStream is) {
		return response(is, DEFAULT_ENCODE);
	}
	
	public static MessageResponse response(InputStream is,String encode){
		MessageResponse response = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, encode);
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String tag = parser.getName();
					if ("Message".equalsIgnoreCase(tag)) {
						response = new MessageResponse();
					} else if ("Sequence".equalsIgnoreCase(tag)) {
						response.setSequence(Integer.valueOf(parser.nextText()));
					} else if ("CommandType".equalsIgnoreCase(tag)) {
						response.setCommandType(parser.nextText());
					} else if ("WhichCommand".equalsIgnoreCase(tag)) {
						response.setWhichCommand(parser.nextText());
					} else if("Status".equalsIgnoreCase(tag)){
						response.setStatus(Integer.valueOf(parser.nextText()));
					} else if ("Description".equalsIgnoreCase(tag)) {
						response.setDescription(parser.nextText());
					}
					break;
				default:
					break;
				}
				
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception : " + e.getMessage());
			response = null;
		}

		return response;
	}
	
	public static String packResponse(MessageForward msg) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			buffer.append("<Message>");
			buffer.append("<Sequence>" + msg.getSequence() + "</Sequence>");
			buffer.append("<CommandType>RESPONSE</CommandType>");
			buffer.append("<WhichCommand>FORWARD</WhichCommand>");
			buffer.append("<Status>" + msg.getStatus() + "</Status>");
			buffer.append("<Description>" + msg.getDescription() + "</Description>");
			buffer.append("</Message>");

			return buffer.toString();
		} catch (Exception e) {
			Log.e(TAG, "Exception : " + e.getMessage());
		}

		return null;
	}
}
