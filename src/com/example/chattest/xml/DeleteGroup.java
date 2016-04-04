package com.example.chattest.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.example.chattest.bean.Group;
import com.example.chattest.bean.MessageGroup;
import com.example.chattest.bean.MessageResponse;

/**
 *
 * @author ly
 *
 */
public class DeleteGroup {
	private static final String TAG = "DeleteGroup";
	private static final String DEFAULT_ENCODE = "UTF-8";

	public static MessageGroup parser(InputStream is) {
		return parser(is, DEFAULT_ENCODE);
	}

	public static MessageGroup parser(InputStream is, String encode) {
		MessageGroup msg = null;
		List<Group> groups = null;
		Group group = null;

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
						msg = new MessageGroup();
						groups = new ArrayList<Group>();
					} else if ("Sequence".equalsIgnoreCase(tag)) {
						msg.setSequence(Integer.valueOf(parser.nextText()));
					} else if ("CommandType".equalsIgnoreCase(tag)) {
						msg.setCommandType(parser.nextText());
					} else if ("WhichCommand".equalsIgnoreCase(tag)) {
						msg.setWhichCommand(parser.nextText());
					} else if ("Params".equalsIgnoreCase(tag)) {
						group = new Group();
					} else if ("GroupId".equalsIgnoreCase(tag)) {
						group.setGroupId(Integer.valueOf(parser.nextText()));
					}
					break;
				case XmlPullParser.END_TAG:
					String endTag = parser.getName();
					if ("Params".equalsIgnoreCase(endTag)) {
						groups.add(group);
					} else if ("Message".equalsIgnoreCase(endTag)) {
						msg.setGroups(groups);
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

	public static String pack(MessageGroup msg){
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			buffer.append("<Message>");
			buffer.append("<Sequence>" + msg.getSequence() + "</Sequence>");
			buffer.append("<CommandType>REQUEST</CommandType>");
			buffer.append("<WhichCommand>GROUPDEL</WhichCommand>");

			for (Group group : msg.getGroups()) {
				buffer.append("<Params>");
				buffer.append("<GroupId>" + group.getGroupId() + "</GroupId>");
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

	public static MessageResponse response(InputStream is, String encode) {
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
	
	public static String packResponse(MessageGroup msg) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			buffer.append("<Message>");
			buffer.append("<Sequence>" + msg.getSequence() + "</Sequence>");
			buffer.append("<CommandType>RESPONSE</CommandType>");
			buffer.append("<WhichCommand>GROUPDEL</WhichCommand>");
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
