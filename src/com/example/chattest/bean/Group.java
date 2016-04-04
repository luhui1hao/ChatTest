package com.example.chattest.bean;

import java.util.List;

/**
 *
 * @author ly
 *
 */
public class Group {
	private String groupNum;
	private int groupId;
	private List<Member> members;

	public String getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(String groupNum) {
		this.groupNum = groupNum;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
}
