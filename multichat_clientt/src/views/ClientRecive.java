package views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import dto.response.ResponseDto;

public class ClientRecive extends Thread {

	private Socket socket;
	private Gson gson;
	
	public ClientRecive(Socket socket) {
		this.socket = socket;
		gson = new Gson();
	}
	
	@Override
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while(true) {
				String responseJson = bufferedReader.readLine();
				responseMapping(responseJson);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void responseMapping(String responseJson) {
		ResponseDto<?> responseDto = gson.fromJson(responseJson, ResponseDto.class); // ❤
		switch (responseDto.getResource()) {
			case "usernameCheckIsBlank":
			case "usernameCheckIsDuplicate":
				JOptionPane.showMessageDialog(null, (String) responseDto.getBody(), "접속오류", JOptionPane.WARNING_MESSAGE);
				break;
				
			case "usernameCkeckSuccessfully" :
				ClientApplication.getInstance().
				getMainCard()
				.show(ClientApplication.getInstance().getMainPanel(), "roomListPanel");
				break;
				
			case "refreshRoomList" :
				refreshRoomList((List<Map<String, String>>) responseDto.getBody()); // 형변환을 해줘야함 ❤에서 <?>로 데리러 왔기 때문에 
				break;
				
			case "createRoomSuccessfully" :
				ClientApplication.getInstance().
				getMainCard()
				.show(ClientApplication.getInstance().getMainPanel(), "roomPanel");
				break;
			case "refreshUsernameList" :
				refreshUserNameList((List<String>) responseDto.getBody());
				break;
			default:
				break;
		}
	}
	
	private void refreshRoomList(List<Map<String,String>> roomList) { //방 리스트 갱신
		ClientApplication.getInstance().getRoomNameListModel().clear(); // 갱신 시킬때마다 초기화를 시켜야 안쌓임
		ClientApplication.getInstance().setRoomInfoList(roomList);
		for(Map<String, String> roomInfo : roomList) {
			ClientApplication.getInstance().getRoomNameListModel().addElement(roomInfo.get("roomName")); //Map에 있는 roomName키값을 이용해서 방목록 리스트에 넣어줌
		}

	}
	
	private void refreshUserNameList(List<String> usernameList) {
		ClientApplication.getInstance().getUsernameListModel().clear();
		ClientApplication.getInstance().getUsernameListModel().addAll(usernameList);
	}
}











