package teamproject.v2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ATM {
	private Scanner scan = new Scanner(System.in);
	private String ip = "192.168.30.5";
	private int port = 5001;
	private void printMenu() {
		System.out.print(
				"================ATM================\n"
				+ "1. 계좌개설   2. 계좌해지   3. 비밀번호 변경\n"
				+ "4. 입금      5. 출금     6. 송금\n"
				+ "7. 통장조회   8. 업무종료   선택 : ");
	}
	private void runMenu(int menu) {
		System.out.println("------------------------------");
		switch(menu) {
		case 1:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("insert");
				oos.flush();
				String accountNum = ois.readUTF();
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
				oos.writeObject(tmp);
				oos.flush();
				String result = ois.readUTF();
				if(result.equals("이미 등록된 이름과 비밀번호입니다.")) {
					System.out.println(result);
					break;
				}
				else {
					System.out.println("계좌를 개설했습니다.");
					System.out.println("------------------------------");
				}
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 2:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("remove");
				oos.flush();
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				String check = "";
				do {
					System.out.print("비밀번호 : ");
					String password = scan.next();
					oos.writeUTF(password);
					oos.flush();
					check = ois.readUTF();
					if(check.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
						System.out.println(check);
						continue;
					}
					break;
				}while(true);
				System.out.println("계좌를 해지했습니다.");
				System.out.println("------------------------------");
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 3:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("update");
				oos.flush();
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				String check = "";
				do {
					System.out.print("비밀번호 : ");
					String password = scan.next();
					oos.writeUTF(password);
					oos.flush();
					check = ois.readUTF();
					if(check.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
						System.out.println(check);
						continue;
					}
					break;
				}while(true);
				String password = "";
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
				oos.writeUTF(password);
				oos.flush();
				String result = ois.readUTF();
				if(result.equals("동일한 비밀번호입니다.")) {
					System.out.println(result);
					break;
				}
				else {
					System.out.println("비밀번호를 변경했습니다.");
					System.out.println("------------------------------");
				}
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 4:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("deposit");
				oos.flush();
				System.out.print("입금할 금액 : ");
				int deposit = scan.nextInt();
				oos.writeInt(deposit);
				oos.flush();
				String result = ois.readUTF();
				if(result.equals(deposit+"원은 입금할 수 없습니다.")) {
					System.out.println(result);
					break;
				}
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				System.out.println(deposit+"원을 입금하였습니다.");
				result = ois.readUTF();
				System.out.println(result);
				System.out.println("------------------------------");
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 5:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("withdraw");
				oos.flush();
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				String check = "";
				do {
					System.out.print("비밀번호 : ");
					String password = scan.next();
					oos.writeUTF(password);
					oos.flush();
					check = ois.readUTF();
					if(check.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
						System.out.println(check);
						continue;
					}
					break;
				}while(true);
				String result = ois.readUTF();
				if(result.equals("잔액이 0원이므로 출금할 수 없습니다.")) {
					System.out.println(result);
				}
				else {
					int withdraw = 0;
					result = ois.readUTF();
					System.out.print(result);
					withdraw = scan.nextInt();
					oos.writeInt(withdraw);
					oos.flush();
					result = ois.readUTF();
					if(result.equals(withdraw+"원은 출금할 수 없습니다.")){
						System.out.println(result);
						break;
					}
					System.out.println(withdraw+"원을 출금하였습니다.");
					result = ois.readUTF();
					System.out.println(result);
					System.out.println("------------------------------");
				}
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 6:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("transfer");
				oos.flush();
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				String check = "";
				do {
					System.out.print("비밀번호 : ");
					String password = scan.next();
					oos.writeUTF(password);
					oos.flush();
					check = ois.readUTF();
					if(check.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
						System.out.println(check);
						continue;
					}
					break;
				}while(true);
				String result = ois.readUTF();
				if(result.equals("잔액이 0원이므로 출금할 수 없습니다.")) {
					System.out.println(result);
					break;
				}
				System.out.print("송금할 계좌번호 혹은 예금주명 : ");
				search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				result = ois.readUTF();
				if(result.equals("송금할 계좌가 없습니다.")) {
					System.out.println(result);
					break;
				}
				int size = ois.readInt();
				for(int i = 0; i < size; i++) {
					result = ois.readUTF();
					System.out.println(result);
				}
				System.out.print("송금할 계좌 선택 : ");
				int index2 = scan.nextInt() - 1;
				oos.write(index2);
				oos.flush();
				
				int transfer = 0;
				result = ois.readUTF();
				if(result.equals("번호를 잘못선택했습니다.")) {
					System.out.println(result);
					break;
				}
				System.out.print(result);
				transfer = scan.nextInt();
				oos.writeInt(transfer);
				oos.flush();
				result = ois.readUTF();
				if(result.equals(transfer+"원은 송금할 수 없습니다.")) {
					System.out.println(result);
					break;
				}
				System.out.println(transfer+"원을 송금하였습니다.");
				result = ois.readUTF();
				System.out.println(result);
				System.out.println("------------------------------");
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 7:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("check");
				oos.flush();
				System.out.print("계좌번호 혹은 예금주명 : ");
				String search = scan.next();
				oos.writeUTF(search);
				oos.flush();
				int index = ois.readInt();
				if(index == -1) {
					System.out.println("없는 계좌입니다.");
					break;
				}
				String check = "";
				do {
					System.out.print("비밀번호 : ");
					String password = scan.next();
					oos.writeUTF(password);
					oos.flush();
					check = ois.readUTF();
					if(check.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
						System.out.println(check);
						continue;
					}
					break;
				}while(true);
				String result = ois.readUTF();
				System.out.println(result);
				System.out.println("============통장내역============");
				int size = ois.readInt();
				for(int i = 0; i < size; i++) {
					result = ois.readUTF();
					System.out.println(result);
				}
				System.out.println("==============================");
				System.out.print("돌아가려면 엔터를 입력하세요.");
				scan.nextLine();
				String enter = scan.nextLine();
				if(enter.equals("\n")) {
					break;
				}
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		case 8:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("end");
				oos.flush();
				String result = ois.readUTF();
				System.out.println(result);
				System.out.println("------------------------------");
				break;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		default:
			try {
				Socket socket = new Socket(ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				oos.writeUTF("");
				oos.flush();
				System.out.println("잘못된 메뉴입니다.");
				System.out.println("------------------------------");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void run() {
		int menu = 0;
		do {
			printMenu();
			menu = scan.nextInt();
			runMenu(menu);
		}while(menu != 8);
	}
}