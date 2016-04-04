package com.example.chattest.bean;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ly
 *
 */
public class MessageForward implements Serializable{
	private int sequence;
	private String commandType;
	private String whichCommand;
	private String status;
	private String description;
	private List<Info> infos;

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

	public List<Info> getInfos() {
		return infos;
	}

	public void setInfos(List<Info> infos) {
		this.infos = infos;
	}
}
