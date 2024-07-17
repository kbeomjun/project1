package teamproject.v3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import day21.socket3.Contact;


public class ServerMain {

	public static void main(String[] args) {
		
		int port = 4788;
		String fileName = "src/teamproject/v3/server.txt";
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true) {
				Socket socket = serverSocket.accept();
				
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				
				String type = ois.readUTF();
				
				switch(type) {
				case "save":
					save(fileName, ois);
					break;
				case "load":
					load(fileName, oos);
					break;
				}
				
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void load(String fileName, ObjectOutputStream oos) {
		// 파일을 열어서 연락처 리스트를 가져옴
		List<ATM> list = new ArrayList<ATM>();
		try(ObjectInputStream fois = new ObjectInputStream(new FileInputStream(fileName))) {
			list = (List<ATM>) fois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// oos를 이용해서 연락처 리스트를 전송
		try {
			oos.writeObject(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	private static void save(String fileName, ObjectInputStream ois) {
		List<ATM> list;
		// ois를 통해 연락처 리스트를 가져옴
		try {
			list = (List<ATM>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		// 파일을 열어서 위에서 가져온 연락처 리스트를 저장
		try(ObjectOutputStream foos = new ObjectOutputStream (new FileOutputStream(fileName))){
			foos.writeObject(list);
			foos.flush();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
