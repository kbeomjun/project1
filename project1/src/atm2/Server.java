package atm2;

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
		int port = 5001;
		String fileName = "src/atm2/server.txt";
		//load(fileName);
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("클라이언트가 접속했습니다.");
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				String type = ois.readUTF();
				switch(type) {
				case "insert":
					int result = 0;
					String accountNum = "";
					do {
						result = 0;
						accountNum = createAccountNum();
						for(Account tmp : list) {
							if(tmp.getAccountNum().equals(accountNum)) {
								result++;
							}
						}
						System.out.println(result);
					}while(result != 0);
					oos.writeUTF(accountNum);
					oos.flush();
					try {
						Account tmp = (Account) ois.readObject();
						tmp.getBankBook().add("계좌개설 | 잔고 : 0원");
						list.add(tmp);
						System.out.println(list);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
		save(fileName);
	}
	private static String createAccountNum() {
		int min = 1, max = 9999;
		int random = (int)(Math.random() * (max - min + 1) + min);
		String randomNum = String.valueOf(random);
		if(randomNum.length() < 4) {
			while(randomNum.length() < 4) {
				randomNum = "0" + randomNum;
			}
		}
		return "1010-"+randomNum;
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