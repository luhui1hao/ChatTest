package com.example.chattest.bean;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ly
 *
 */
public class MessageGroup implements Serializable{
	private int sequence;
	private String commandType; // 请求或响应命令
	private String whichCommand; // 命令类型
	private String status;
	private String description;
	private List<Group> groups; // 组信息

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getWhichCommand() {
		return whichCommand;
	}

	public void setWhichCommand(String whichCommand) {
		this.whichCommand = whichCommand;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
