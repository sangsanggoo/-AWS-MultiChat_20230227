package main;

import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dto.request.requestDto;
import dto.response.responseDto;
import lombok.Getter;

@Getter
public class ConnectedSocket extends Thread {
	private static List<ConnectedSocket> connectedsocketlist = new ArrayList<>();
	private Socket socket;
	private Gson gson;
	private String username;
	
	public ConnectedSocket(Socket socket) {
		this.socket = socket;
		gson = new Gson();
	}
	
	@Override
	public void run() {
		while(true) {
			BufferedReader bufferedReader;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestJson = bufferedReader.readLine();
				
				System.out.println("요청" + requestJson);
				requestMapping(requestJson);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

}
	private void requestMapping(String requestJson) {
		requestDto<?> requestdto = gson.fromJson(requestJson,requestDto.class);
		switch(requestdto.getResource()) {
		case "usernameCheck" :
			checkUsername((String)requestdto.getBody());
			break;
				}
	}
	private void checkUsername(String username) {
		if(username.isBlank()) {
			sendToMe(new responseDto<String>("usernameCheckIsBlank", "사용자 이름은 공백일 수 없습니다." ));
			return;
		}
		
		for(ConnectedSocket connectedSocket : connectedsocketlist) {
			if(connectedSocket.getUsername().equals(username)) {
				sendToMe(new responseDto<String>("usernameCheckIsDuplicate","이미 사용중인 이름입니다."));
				return;
			}
		}
		this.username = username;
		connectedsocketlist.add(this);
		sendToMe(new responseDto<String>("usernameCheckSuccessfully",null));
		
		
	}
	private void sendToMe(responseDto<?> responseDto) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream,true);
			String responseJson = gson.toJson(responseDto);
			printWriter.println(responseJson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendToAll() {
		
	}
}
