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
				A:switch(type) {
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
				case "remove":
					String search = ois.readUTF();
					int index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					do {
						String password = ois.readUTF();
						if(!list.get(index).getPassword().equals(password)) {
							oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
							oos.flush();
							continue;
						}
						oos.writeUTF("");
						oos.flush();
						break;
					}while(true);
					list.remove(index);
					break;
				case "update":
					search = ois.readUTF();
					index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					do {
						String password = ois.readUTF();
						if(!list.get(index).getPassword().equals(password)) {
							oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
							oos.flush();
							continue;
						}
						oos.writeUTF("");
						oos.flush();
						break;
					}while(true);
					String password = ois.readUTF();
					list.get(index).setPassword(password);
					break;
				case "deposit":
					int deposit = ois.readInt();
					if(deposit <= 0) {
						oos.writeUTF(deposit+"원은 입금할 수 없습니다.");
						oos.flush();
						break;
					}
					oos.writeUTF("");
					oos.flush();
					search = ois.readUTF();
					index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					if(index == -1) {
						break;
					}
					int money = 0;
					money = list.get(index).getBalance() + deposit;
					list.get(index).setBalance(money);
					oos.writeUTF(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
					oos.flush();
					list.get(index).getBankBook().add("입금 "+deposit+"원 | 잔고 : "+list.get(index).getBalance()+"원");
					break;
				case "withdraw":
					search = ois.readUTF();
					index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					if(index == -1) {
						break;
					}
					do {
						password = ois.readUTF();
						if(!list.get(index).getPassword().equals(password)) {
							oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
							oos.flush();
							continue;
						}
						oos.writeUTF("");
						oos.flush();
						break;
					}while(true);
					if(list.get(index).getBalance() == 0) {
						oos.writeUTF("잔액이 0원이므로 출금할 수 없습니다.");
						oos.flush();
					}
					else {
						oos.writeUTF("");
						oos.flush();
						int withdraw = 0;
						oos.writeUTF("출금할 금액(잔액 : "+list.get(index).getBalance()+"원) : ");
						oos.flush();
						withdraw = ois.readInt();
						if(withdraw <= 0 || list.get(index).getBalance() < withdraw) {
							oos.writeUTF(withdraw+"원은 출금할 수 없습니다.");
							oos.flush();
							break;
						}
						oos.writeUTF("");
						oos.flush();
						money = list.get(index).getBalance() - withdraw;
						list.get(index).setBalance(money);
						oos.writeUTF(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
						oos.flush();
						list.get(index).getBankBook().add("출금 "+withdraw+"원 | 잔고 : "+list.get(index).getBalance()+"원");
					}
					break;
				case "transfer":
					search = ois.readUTF();
					index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					if(index == -1) {
						break;
					}
					do {
						password = ois.readUTF();
						if(!list.get(index).getPassword().equals(password)) {
							oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
							oos.flush();
							continue;
						}
						oos.writeUTF("");
						oos.flush();
						break;
					}while(true);
					if(list.get(index).getBalance() == 0) {
						oos.writeUTF("잔액이 0원이므로 출금할 수 없습니다.");
						oos.flush();
					}
					else {
						oos.writeUTF("");
						oos.flush();
						search = ois.readUTF();
						List<Account> list2 = new ArrayList<Account>();
						for(int i = 0; i < list.size(); i++) {
							if(index == i) {
								continue;
							}
							if(list.get(i).getAccountNum().contains(search) || list.get(i).getName().contains(search)) {
								list2.add(list.get(i));
							}
						}
						if(list2.size() == 0) {
							oos.writeUTF("송금할 계좌가 없습니다.");
							oos.flush();
							break;
						}
						oos.writeUTF("");
						oos.writeInt(list2.size());
						oos.flush();
						for(int i = 0; i < list2.size(); i++) {
							oos.writeUTF((i+1)+". "+list2.get(i).getBank()+" "+list2.get(i).getAccountNum()+"(예금주:"+list2.get(i).getName()+")");
							oos.flush();
						}
						int index2 = ois.read();
						if(index2 > list2.size() - 1 || index2 < 0) {
							oos.writeUTF("번호를 잘못선택했습니다.");
							oos.flush();
							break;
						}
						int transfer = 0;
						oos.writeUTF("송금할 금액(잔액 : "+list.get(index).getBalance()+"원) : ");
						oos.flush();
						transfer = ois.readInt();
						if(transfer <= 0 || list.get(index).getBalance() < transfer) {
							oos.writeUTF(transfer+"원은 송금할 수 없습니다.");
							oos.flush();
							break;
						}
						oos.writeUTF("");
						oos.flush();
						money = list.get(index).getBalance() - transfer;
						list.get(index).setBalance(money);
						oos.writeUTF(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
						oos.flush();
						list.get(index).getBankBook().add("송금 "+transfer+"원 | 잔고 : "+list.get(index).getBalance()+"원");
						for(int i = 0; i < list.size(); i++) {
							if(list.get(i).getAccountNum().equals(list2.get(index2).getAccountNum())) {
								index2 = i;
							}
						}
						money = list.get(index2).getBalance() + transfer;
						list.get(index2).setBalance(money);
						list.get(index2).getBankBook().add(list.get(index).getName()+" "+transfer+"원 | 잔고 : "+list.get(index2).getBalance()+"원");
					}
					break;
				case "check":	
					search = ois.readUTF();
					index = findAccount(search);
					oos.writeInt(index);
					oos.flush();
					if(index == -1) {
						break;
					}
					do {
						password = ois.readUTF();
						if(!list.get(index).getPassword().equals(password)) {
							oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
							oos.flush();
							continue;
						}
						oos.writeUTF("");
						oos.flush();
						break;
					}while(true);
					oos.writeUTF(list.get(index).toString());
					oos.flush();
					List<String> bankBook = list.get(index).getBankBook();
					oos.writeInt(bankBook.size());
					oos.flush();
					for(int i = 0; i < bankBook.size(); i++) {
						oos.writeUTF((i+1)+". "+bankBook.get(i));
						oos.flush();
					}
					break;
				case "end":
					oos.writeUTF("업무를 종료합니다.");
					oos.flush();
					break;
				default:
				}
			}
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
		save(fileName);
	}
	private static int findAccount(String search) {
		int index = -1;
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getAccountNum().equals(search) || list.get(i).getName().equals(search)) {
				index = i;
			}
		}
		return index;
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