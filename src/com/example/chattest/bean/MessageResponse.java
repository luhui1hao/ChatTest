package com.example.chattest.bean;

/**
 *
 * @author ly
 *
 */
public class MessageResponse {
	private int sequence;
	private String commandType;
	private String whichCommand;
	private int status;
	private String groupId;
	private String description;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
