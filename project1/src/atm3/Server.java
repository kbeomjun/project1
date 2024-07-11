package atm3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Server extends Thread{
	private Socket socket;
	private static List<Account> list = new ArrayList<Account>();
	private static final String fileName = "src/atm3/server.txt";;
	
	@Override
	public synchronized void run() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			String type = ois.readUTF();
			switch(type) {
			case "insert":
				System.out.println("계좌개설중...");
				load(fileName);
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
					if(list.contains(tmp)) {
						oos.writeUTF("이미 등록된 이름과 비밀번호입니다.");
						oos.flush();
						break;
					}
					oos.writeUTF("");
					oos.flush();
					String dateStr = format.format(date);
					tmp.getBankBook().add(dateStr+" | 계좌개설 | 잔고 : 0원");
					list.add(tmp);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				save(fileName);
				System.out.println(list);
				break;
			case "remove":
				load(fileName);
				String search = ois.readUTF();
				int index = findAccount(search);
				System.out.println(list.get(index).getName()+"님이 계좌해지중...");
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
				save(fileName);
				System.out.println(list);
				break;
			case "update":
				load(fileName);
				search = ois.readUTF();
				index = findAccount(search);
				System.out.println(list.get(index).getName()+"님이 비밀번호 변경중...");
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
				if(list.get(index).getPassword().equals(password)) {
					oos.writeUTF("동일한 비밀번호입니다.");
					oos.flush();
					break;
				}
				oos.writeUTF("");
				oos.flush();
				list.get(index).setPassword(password);
				save(fileName);
				System.out.println(list);
				break;
			case "deposit":
				load(fileName);
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
				System.out.println(list.get(index).getName()+"님이 입금중...");
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
				String dateStr = format.format(date);
				list.get(index).getBankBook().add(dateStr+" | 입금 "+deposit+"원 | 잔고 : "+list.get(index).getBalance()+"원");
				save(fileName);
				System.out.println(list);
				break;
			case "withdraw":
				load(fileName);
				search = ois.readUTF();
				index = findAccount(search);
				System.out.println(list.get(index).getName()+"님이 출금중...");
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
					dateStr = format.format(date);
					list.get(index).getBankBook().add(dateStr+" | 출금 "+withdraw+"원 | 잔고 : "+list.get(index).getBalance()+"원");
					save(fileName);
				}
				System.out.println(list);
				break;
			case "transfer":
				load(fileName);
				search = ois.readUTF();
				index = findAccount(search);
				System.out.println(list.get(index).getName()+"님이 송금중...");
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
					dateStr = format.format(date);
					list.get(index).getBankBook().add(dateStr+" | 송금 "+transfer+"원 | 잔고 : "+list.get(index).getBalance()+"원");
					for(int i = 0; i < list.size(); i++) {
						if(list.get(i).getAccountNum().equals(list2.get(index2).getAccountNum())) {
							index2 = i;
						}
					}
					money = list.get(index2).getBalance() + transfer;
					list.get(index2).setBalance(money);
					list.get(index2).getBankBook().add(dateStr+" | "+list.get(index).getName()+" "+transfer+"원 | 잔고 : "+list.get(index2).getBalance()+"원");
					save(fileName);
				}
				System.out.println(list);
				break;
			case "check":
				load(fileName);
				search = ois.readUTF();
				index = findAccount(search);
				System.out.println(list.get(index).getName()+"님이 통장조회중...");
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
				save(fileName);
				break;
			case "end":
				System.out.println("접속해제");
				oos.writeUTF("업무를 종료합니다.");
				oos.flush();
				save(fileName);
				break;
			default:
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String createAccountNum() {
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
	private int findAccount(String search) {
		int index = -1;
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getAccountNum().equals(search) || list.get(i).getName().equals(search)) {
				index = i;
			}
		}
		return index;
	}
	private void save(String fileName2) {
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
	private void load(String fileName2) {
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