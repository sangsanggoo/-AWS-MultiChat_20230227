package views;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.gson.Gson;

import dto.request.requestDto;


public class ClientApplication extends JFrame {

	private static final long serialVersionUID = -4289131769792608665L;
	
	
	private Gson gson;
	private Socket socket;
	
	
	private JPanel mainPanel;
	private CardLayout mainCard;
	
	private JTextField usernameField;
	private JTextField textField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApplication frame = new ClientApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public ClientApplication() {
		/*=================<< init >>=========================*/
		gson = new Gson();
		try {
			socket = new Socket("127.0.0.1",9090);
			ClientReceive clientReceive = new ClientReceive(socket);
			clientReceive.start();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (ConnectException e1) {
			JOptionPane.showMessageDialog(this, "서버에 접속 할수 없습니다.","접속오류",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		/*=================<< frame set >>=========================*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 150, 480, 800);
		
		/*=================<< panels >>=========================*/
		mainPanel = new JPanel();
		JPanel loginPanel = new JPanel();
		JPanel roomListPanel = new JPanel();
		JPanel roomPanel = new JPanel();
		
		
		/*=================<< layout >>=========================*/
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);
		loginPanel.setLayout(null);
		roomListPanel.setLayout(null);
		roomPanel.setLayout(null);
		
		
		/*=================<< panel set>>=========================*/
		setContentPane(mainPanel);
		mainPanel.add(loginPanel, "loginPanel");
		mainPanel.add(roomListPanel, "roomListPanel");
		mainPanel.add(roomPanel, "roomPanel");
		
		/*=================<< login panel>>=========================*/
		
		JButton enterButton = new JButton("접속하기");
		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					enterButton.doClick();
				}

			}
		});
		
		usernameField.setBounds(27, 353, 399, 59);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);
		
		enterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
					requestDto<String> usernameCheckDto = new requestDto<String>("usernameCheck", usernameField.getText());
					sendRequest(usernameCheckDto);
				
			}
		});
		enterButton.setBounds(26, 422, 400, 163);
		loginPanel.add(enterButton);
		
		
		
		/*=================<< roomlist panel>>=========================*/		
		
		JScrollPane roomListScroll = new JScrollPane();
		roomListScroll.setBounds(144, 10, 298, 731);
		roomListPanel.add(roomListScroll);
		
		JButton createRoomButton = new JButton("방생성");
		createRoomButton.setBounds(12, 10, 96, 73);
		roomListPanel.add(createRoomButton);
		
		/*=================<< room panel>>=========================*/
		
		
		
		JScrollPane joinUserListScroll = new JScrollPane();
		joinUserListScroll.setBounds(0, 0, 356, 91);
		roomPanel.add(joinUserListScroll);
		
		JButton roomExitButton = new JButton("나가기");
		roomExitButton.setBounds(357, 0, 97, 91);
		roomPanel.add(roomExitButton);
		
		JScrollPane chattingContentScroll = new JScrollPane();
		chattingContentScroll.setBounds(0, 89, 454, 589);
		roomPanel.add(chattingContentScroll);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(0, 678, 388, 73);
		roomPanel.add(textField);
		
		JButton sendButton = new JButton("전송");
		sendButton.setBounds(387, 678, 67, 73);
		roomPanel.add(sendButton);
		
		

	}
	
	private void sendRequest(requestDto<?> requestDto) {
		String reqJson = gson.toJson(requestDto);
		OutputStream outputStream = null;
		PrintWriter printWriter = null;
		try {
			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(outputStream,true);
			printWriter.println(reqJson);
			System.out.println("클라이언트 -> 서버" + reqJson);
		} catch ( IOException e) {
			e.printStackTrace();
		} 

	}
}
