package views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import dto.response.responseDto;

public class ClientReceive extends Thread{
	
	private Socket socket;
	private Gson gson;
	
	public ClientReceive(Socket socket) {
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
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void responseMapping(String responseJson) {
		responseDto<?> responseDto = gson.fromJson(responseJson, responseDto.class);
		switch(responseDto.getResource()) {
		case "usernameCheckIsBlank" :
		case "usernameCheckIsDuplicate" :
			JOptionPane.showMessageDialog(null, (String) responseDto.getBody(),"접속오류",JOptionPane.WARNING_MESSAGE);
			break;
		default :
			break;
			
		}
	}
}
