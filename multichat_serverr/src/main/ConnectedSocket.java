package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dto.request.RequestDto;
import dto.response.ResponseDto;
import entity.Room;
import lombok.Getter;

@Getter
public class ConnectedSocket extends Thread {

	private static List<ConnectedSocket> connectedSocketList = new ArrayList<>(); //연결된 소켓들을 담을 소켓 리스트
	private static List<Room> roomList = new ArrayList<>(); 					  // 방을 담을 방리스트
	private static int index = 0;
	private Socket socket;
	private String username;
	
	private Gson gson;
	
	public ConnectedSocket(Socket socket) {
		this.socket = socket;
		gson = new Gson();
		Room room = new Room("testRoom" + index,"testUser" + index);
		index++;
		roomList.add(room);
	}
	
	@Override
	public void run() {
		while(true) {
			BufferedReader bufferedReader;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestJson = bufferedReader.readLine();
				
				System.out.println("요청: " + requestJson);
				requestMapping(requestJson);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void requestMapping(String requestJson) {
		RequestDto<?> requestDto = gson.fromJson(requestJson, RequestDto.class);
		
		switch(requestDto.getResource()) {
			case "usernameCheck":
				checkUsername((String) requestDto.getBody());
				break;
			case "createRoom" :
				Room room = new Room((String) requestDto.getBody(),username);
				room.getUsers().add(this);
				roomList.add(room);
				ResponseDto<String> responseDto = new ResponseDto<String>("createRoomSuccessfully", null); 
				sendToMe(responseDto);  						  // 자기한테 추가된 방 리스트를 보내줌
				refreshUsernameList(username);
				sendToAll(refreshRoomList(),connectedSocketList); // 접속한 모든 사용자들에게 추가된 방리스트들을 보내줌
				
		}
	}
	
	
	
	
	private void checkUsername(String username) {
		if(username.isBlank()) {
			sendToMe(new ResponseDto<String>("usernameCheckIsBlank", "사용자 이름은 공백일 수 없습니다"));
			return;
		}
		
		for(ConnectedSocket connectedSocket : connectedSocketList) {
			if(connectedSocket.getUsername().equals(username)) {
				sendToMe(new ResponseDto<String>("usernameCheckIsDuplicate", "이미 사용중인 이름입니다."));
				return;
			}
		}
		
		this.username = username;
		connectedSocketList.add(this);
		sendToMe(new ResponseDto<String>("usernameCkeckSuccessfully", null));
		sendToMe(refreshRoomList());
	}
	
	private Room findConnectedRoom(String username) {
		for (Room r : roomList) {
			for(ConnectedSocket cs : r.getUsers()) {
				if(cs.getUsername().equals(username)) {
					return r;
				}
			}
		}
		return null;
	}
	
	private Room findRoom(Map<String, String> roomInfo) { //이게 먼뜻일까
		for(Room room : roomList) {
			if(room.getRoomName().equals(roomInfo.get("roomName")) && room.getOwner().equals(roomInfo.get("owner")));
			return room;
		}
		return null;
	}
	
	private void refreshUsernameList(String username) {
		Room room = findConnectedRoom(username);
		List<String> usernameList = new ArrayList<>();
		usernameList.add(room.getRoomName());
		for (ConnectedSocket connectedSocket : room.getUsers()) {
			if(connectedSocket.getUsername().equals(room.getOwner())) {
				usernameList.add(connectedSocket.getUsername() + "(방장)");
				continue;
			}
			usernameList.add(connectedSocket.getUsername());
		}
		ResponseDto<List<String>> responseDto = new ResponseDto<List<String>>("refreshUsernameList", usernameList);
		sendToAll(responseDto, room.getUsers());
		
	}
	
	private void sendToMe(ResponseDto<?> responseDto) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream, true);
			
			String responseJson = gson.toJson(responseDto);
			printWriter.println(responseJson);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private ResponseDto<List<Map<String, String>>> refreshRoomList() { 
		List<Map<String, String>> roomNameList = new ArrayList<>();
		for (Room room : roomList) {
			Map<String, String> roomInfo = new HashMap<>();
			roomInfo.put("roomName",room.getRoomName());
			roomInfo.put("owner",room.getOwner());
			roomNameList.add(roomInfo);
		}
		ResponseDto<List<Map<String, String>>> responseDto = new ResponseDto<List<Map<String, String>>>("refreshRoomList", roomNameList);
		return responseDto;
 	}
	
	private void sendToAll(ResponseDto<?> responseDto, List<ConnectedSocket> connectedSockets) {
		for (ConnectedSocket connectedSocket : connectedSockets) {
		try {
			OutputStream outputStream = connectedSocket.socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream, true);
			
			String responseJson = gson.toJson(responseDto);
			printWriter.println(responseJson);
			
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}
}
}










