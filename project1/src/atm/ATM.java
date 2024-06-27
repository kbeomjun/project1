package atm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ATM {
	private Scanner scan = new Scanner(System.in);
	private List<Account> list = new ArrayList<Account>();
	private String ip = "192.168.30.22";
	private int port = 5001;
	
	private void printMenu() {
		System.out.print("======ATM======\n"
				+ "1. 계좌개설\n"
				+ "2. 계좌관리\n"
				+ "3. 업무종료\n"
				+ "선택 : ");
	}
	private void printMenu2() {
		System.out.print("======계좌관리======\n"
				+ "1. 비밀번호 변경\n"
				+ "2. 계좌해지\n"
				+ "3. 입금/출금/송금\n"
				+ "4. 통장조회\n"
				+ "5. 이전으로\n"
				+ "선택 : ");
	}
	
	private void runMenu(int menu) {
		System.out.println("------------------------------");
		A:switch(menu) {
		case 1:
			String accountNum = "";
			int result = 0;
			do {
				result = 0;
				accountNum = createAccountNum();
				for(Account tmp : list) {
					if(tmp.getAccountNum().equals(accountNum)) {
						result++;
					}
				}
			}while(result != 0);
			System.out.println("계좌번호 : "+accountNum);
			System.out.print("예금주명 : ");
			String name = scan.next();
			String password = "";
			do {
				System.out.print("비밀번호(4자리) : ");
				password = scan.next();
				String regex = "^\\d{4}$";
				if(!Pattern.matches(regex, password)) {
					System.out.println("잘못된 비밀번호 형식입니다. 다시 입력하세요.");
					continue;
				}
				break;
			}while(true);
			Account tmp = new Account(accountNum, password, name, 0);
			tmp.getBankBook().add("계좌개설 | 잔고 : 0원");
			list.add(tmp);
			System.out.println("계좌를 개설했습니다.");
			System.out.println(list);
			System.out.println("------------------------------");
			break;
		case 2:
			int index = findAccount();
			if(index == -1) {
				break;
			}
			checkPassWord(index);
			int select = 0;
			do {
				printMenu2();
				select = scan.nextInt();
				System.out.println("------------------------------");
				switch(select) {
				case 1:
					do {
						System.out.print("변경할 비밀번호(4자리) : ");
						password = scan.next();
						String regex = "^\\d{4}$";
						if(!Pattern.matches(regex, password)) {
							System.out.println("잘못된 비밀번호 형식입니다. 다시 입력하세요.");
							continue;
						}
						break;
					}while(true);
					list.get(index).setPassword(password);
					System.out.println("비밀번호를 변경했습니다.");
					System.out.println("------------------------------");
					break A;
				case 2:
					list.remove(index);
					System.out.println("계좌를 해지했습니다.");
					System.out.println("------------------------------");
					break A;
				case 3:
					System.out.print("1. 입금\n"
							+"2. 출금\n"
							+"3. 송금\n"
							+"선택 : ");
					select = scan.nextInt();
					System.out.println("------------------------------");
					switch(select) {
					case 1:
						System.out.print("입금할 금액 : ");
						int deposit = scan.nextInt();
						if(deposit <= 0) {
							System.out.println(deposit+"원은 입금할 수 없습니다.");
							break;
						}
						int money = list.get(index).getBalance() + deposit;
						list.get(index).setBalance(money);
						System.out.println(deposit+"원을 입금하였습니다.");
						System.out.println(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
						list.get(index).getBankBook().add("입금 "+deposit+"원 | 잔고 : "+list.get(index).getBalance()+"원");
						System.out.println("------------------------------");
						break;
					case 2:
						if(list.get(index).getBalance() == 0) {
							System.out.println("잔액이 0원이므로 출금할 수 없습니다.");
							break;
						}
						int withdraw = 0;
						do {
							System.out.print("출금할 금액(잔액 : "+list.get(index).getBalance()+"원) : ");
							withdraw = scan.nextInt();
							if(withdraw <= 0 || list.get(index).getBalance() < withdraw) {
								System.out.println(withdraw+"원은 출금할 수 없습니다.");
								continue;
							}
							break;
						}while(true);
						money = list.get(index).getBalance() - withdraw;
						list.get(index).setBalance(money);
						System.out.println(withdraw+"원을 출금하였습니다.");
						System.out.println(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
						list.get(index).getBankBook().add("출금 "+withdraw+"원 | 잔고 : "+list.get(index).getBalance()+"원");
						System.out.println("------------------------------");
						break;
					case 3:
						if(list.get(index).getBalance() == 0) {
							System.out.println("잔액이 0원이므로 송금할 수 없습니다.");
							break;
						}
						System.out.print("송금할 계좌번호 혹은 예금주명 : ");
						String search = scan.next();
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
							System.out.println("송금할 계좌가 없습니다.");
							break;
						}
						for(int i = 0; i < list2.size(); i++) {
							System.out.println((i+1)+". "+list2.get(i).getBank()+" "+list2.get(i).getAccountNum()+"(예금주:"+list2.get(i).getName()+")");
						}
						System.out.print("송금할 계좌 선택 : ");
						int index2 = scan.nextInt() - 1;
						int transfer = 0;
						do {
							System.out.print("송금할 금액(잔액 : "+list.get(index).getBalance()+"원) : ");
							transfer = scan.nextInt();
							if(transfer <= 0 || list.get(index).getBalance() < transfer) {
								System.out.println(transfer+"원은 송금할 수 없습니다.");
								continue;
							}
							break;
						}while(true);
						money = list.get(index).getBalance() - transfer;
						list.get(index).setBalance(money);
						System.out.println(transfer+"원을 송금하였습니다.");
						System.out.println(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
						list.get(index).getBankBook().add("송금 "+transfer+"원 | 잔고 : "+list.get(index).getBalance()+"원");
						for(int i = 0; i < list.size(); i++) {
							if(list.get(i).getAccountNum().equals(list2.get(index2).getAccountNum())) {
								index2 = i;
							}
						}
						money = list.get(index2).getBalance() + transfer;
						list.get(index2).setBalance(money);
						list.get(index2).getBankBook().add(list.get(index).getName()+" "+transfer+"원 | 잔고 : "+list.get(index2).getBalance()+"원");
						System.out.println("------------------------------");
					default:
						System.out.println("잘못된 메뉴입니다.");
						System.out.println("------------------------------");
					}
					break;
				case 4:
					System.out.println(list.get(index).toString());
					System.out.println("============통장내역============");
					List<String> bankBook = list.get(index).getBankBook();
					for(int i = 0; i < bankBook.size(); i++) {
						System.out.println((i+1)+". "+bankBook.get(i));
					}
					System.out.println("==============================");
					System.out.print("돌아가려면 엔터를 입력하세요.");
					scan.nextLine();
					String enter = scan.nextLine();
					if(enter.equals("\n")) {
						System.out.println("------------------------------");
						break;
					}
					break;
				case 5:
					System.out.println("이전으로 돌아갑니다.");
					System.out.println("------------------------------");
					break;
				default:
					System.out.println("잘못된 메뉴입니다.");
					System.out.println("------------------------------");
				}
			}while(select != 5);
			break;
		case 3:
			System.out.println("업무를 종료합니다.");
			System.out.println("------------------------------");
			break;
		default:
			System.out.println("잘못된 메뉴입니다.");
			System.out.println("------------------------------");
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
	
	private int findAccount() {
		System.out.print("계좌번호 혹은 예금주명 : ");
		String search = scan.next();
		int index = -1;
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getAccountNum().equals(search) || list.get(i).getName().equals(search)) {
				index = i;
			}
		}
		if(index == -1) {
			System.out.println("없는 계좌입니다.");
			return index;
		}
		return index;
	}
	
	private void checkPassWord(int index) {
		do {
			System.out.print("비밀번호 : ");
			String password = scan.next();
			if(!list.get(index).getPassword().equals(password)) {
				System.out.println("잘못된 비밀번호입니다. 다시 입력하세요.");
				continue;
			}
			break;
		}while(true);
	}
	
	public void run() {
		load();
		int menu = 0;
		do {
			printMenu();
			menu = scan.nextInt();
			runMenu(menu);
		}while(menu != 3);
		save();
	}
	public void save() {
		try {
			Socket socket = new Socket(ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeUTF("save");
			oos.writeObject(list);
			oos.flush();
			oos.close();
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void load() {
		try {
			Socket socket = new Socket(ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeUTF("load");
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			try {
				list = (List<Account>)ois.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println("Class Not Found");
				e.printStackTrace();
			}
			System.out.println(list);
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}
}