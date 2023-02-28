package entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import main.ConnectedSocket;

@Getter
public class Room {
	private String roomName; //방 이름
	private String owner; //방장 이름
	private List<ConnectedSocket> users; 
	
	public Room(String roomName, String owner) {
		this.roomName = roomName;
		this.owner = owner;
		users = new ArrayList<>();
	}

	
	//	방안에 있는 유저이름들을 모두 뿌려주기 위해 만든거
	public List<String> getUsernameList() { 
		List<String> usernameList = new ArrayList<>();
		for(ConnectedSocket connectedSocket : users) {
			usernameList.add(connectedSocket.getUsername());
		}
		return usernameList;
	}
}
