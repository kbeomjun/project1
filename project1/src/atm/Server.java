package atm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private static List<Account> list = new ArrayList<Account>();

	public static void main(String[] args) {
		String fileName = "src/atm/server.txt";
		int port = 5001;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket socket = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				String type = ois.readUTF();
				switch(type) {
				case "save":
					receive(ois, fileName);
					break;
				case "load":
					send(oos, fileName);
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	private static void send(ObjectOutputStream oos, String fileName) {
		try {
			ObjectInputStream fois = new ObjectInputStream(new FileInputStream(fileName));
			list = (List<Account>)fois.readObject();
			oos.writeObject(list);
			oos.flush();
		} catch (Exception e) {
			try {
				oos.writeObject(new ArrayList<String>());
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("[클라이언트가 접속했습니다.]");
	}
	@SuppressWarnings("unchecked")
	private static void receive(ObjectInputStream ois, String fileName) {
		try {
			list = (List<Account>)ois.readObject();
		} catch (Exception e) {
			return;
		}
		try {
			ObjectOutputStream foos = new ObjectOutputStream(new FileOutputStream(fileName));
			for(int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
			}
			foos.writeObject(list);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("[서버에 저장했습니다.]");
	}
	public static void save(String fileName) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(list);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public static void load(String fileName) {
		try {
			ObjectInputStream ios = new ObjectInputStream(new FileInputStream(fileName));
			try {
				list = (List<Account>) ios.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println("Class Not Found");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
}