package atm4_db.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ATM {
	private Scanner scan = new Scanner(System.in);
	private String ip = "192.168.30.211";
	private int port = 5001;

	private void printMenu() {
		System.out.print(
				"================ATM================\n" 
				+ "1. 계좌개설   2. 계좌해지   3. 비밀번호 변경\n"
				+ "4. 입금      5. 출금     6. 송금\n" 
				+ "7. 통장조회   8. 업무종료   선택 : "
				);
	}

	private void runMenu(int menu) throws Exception {
		printBar();
		A: switch (menu) {
		case 1:
			scan.nextLine();
			Socket socket = new Socket(ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("insert");
			oos.flush();
			// 아이디가 정규표현식에 적합해야 변수에 저장
			String ac_name = regexName();
			oos.writeUTF(ac_name);
			oos.flush();
			String message = "";
			// 비밀번호가 정규표현식에 적합해야 변수에 저장
			String ac_pw = regexPw(message);
			oos.writeUTF(ac_pw);
			oos.flush();
			String ac_num = ois.readUTF();
			System.out.println("계좌를 개설중입니다...");
			Thread.sleep(1000);
			System.out.println("계좌를 개설했습니다.");
			System.out.println("KH은행 " + ac_num + "(예금주:" + ac_name + ") 잔고 : 0원");
			printBar();
			break;
		case 2:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("remove");
			oos.flush();
			// 계좌번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			boolean isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			// 계좌에 비밀번호가 맞는지 확인하기 위해 서버로 비밀번호를 보내는 메소드
			boolean escape = sendPw(oos, ois, ac_num);
			if (escape) {break A;}
			printBar();
			System.out.println("계좌를 해지중입니다...");
			Thread.sleep(1000);
			System.out.println("계좌를 해지했습니다.");
			printBar();
			break;
		case 3:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("update");
			oos.flush();
			// 계좌번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			// 계좌에 비밀번호가 맞는지 확인하기 위해 서버로 비밀번호를 보내는 메소드
			escape = sendPw(oos, ois, ac_num);
			if(escape) {break A;}
			printBar();
			message = "새로운 ";
			// 새 비밀번호가 정규표현식에 정합해야 변수에 저장
			ac_pw = regexPw(message);
			oos.writeUTF(ac_pw);
			oos.flush();
			String result = ois.readUTF();
			if (result.equals("동일한 비밀번호입니다.")) {
				System.out.println(result);
				break;
			}
			System.out.println("비밀번호를 변경중입니다...");
			Thread.sleep(1000);
			System.out.println("비밀번호를 변경했습니다.");
			printBar();
			break;

		case 4:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("deposit");
			oos.flush();
			message = "입금할 금액 : ";
			// 금액이 정규 표현식에 적합해야 변수에 저장
			String str = regexAmount(message);
			int deposit = Integer.parseInt(str);
			oos.writeInt(deposit);
			oos.flush();
			result = ois.readUTF();
			if (result.equals(deposit + "원은 입금할 수 없습니다.")) {
				System.out.println(result);
				break;
			}
			// 계좌 번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			printBar();
			System.out.println("입금중입니다...");
			Thread.sleep(1000);
			System.out.println(balanceFormat(deposit) + "원을 입금하였습니다.");
			result = ois.readUTF();
			System.out.println(result);
			printBar();
			System.out.print("돌아가려면 엔터를 입력하세요.");

			String enter = scan.nextLine();
			if (enter.equals("\n")) {
				break;
			}
			break;

		case 5:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("withdraw");
			oos.flush();
			// 계좌 번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			// 계좌에 비밀번호가 맞는지 확인하기 위해 서버로 비밀번호를 보내는 메소드
			escape = sendPw(oos, ois, ac_num);
			if (escape) {break A;}
			result = ois.readUTF();
			if (result.equals("잔액이 0원이므로 출금할 수 없습니다.")) {
				System.out.println(result);
				break;
			}
			printBar();
			int withdraw = 0;
			result = ois.readUTF();
			// 금액이 정규표현식에 적합해야 변수에 저장
			str = regexAmount(result);
			withdraw = Integer.parseInt(str);
			oos.writeInt(withdraw);
			oos.flush();
			result = ois.readUTF();
			if (result.equals(balanceFormat(withdraw) + "원은 출금할 수 없습니다.")) {
				System.out.println(result);
				break;
			}
			System.out.println("출금중입니다...");
			Thread.sleep(1000);
			System.out.println(balanceFormat(withdraw) + "원을 출금하였습니다.");
			result = ois.readUTF();
			System.out.println(result);
			printBar();
			System.out.print("돌아가려면 엔터를 입력하세요.");
		
			enter = scan.nextLine();
			if (enter.equals("\n")) {
				break;
			}
			break;

		case 6:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("transfer");
			oos.flush();
			// 계좌 번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			// 계좌에 비밀번호가 맞는지 확인하기 위해 서버로 비밀번호를 보내는 메소드
			escape = sendPw(oos, ois, ac_num);
			if (escape) {break A;}
			result = ois.readUTF();
			if (result.equals("잔액이 0원이므로 송금할 수 없습니다.")) {
				System.out.println(result);
				break;
			}
			printBar();
			// 송금할 상대의 계좌번호 혹은 이름이 정규표현식에 적합해야 변수에 저장
			String search = regexSearch();
			oos.writeUTF(search);
			oos.flush();
			result = ois.readUTF();
			if (result.equals("송금할 계좌가 없습니다.")) {
				System.out.println(result);
				break;
			}
			int size = ois.readInt();
			for (int i = 0; i < size; i++) {
				result = ois.readUTF();
				System.out.println(result);
			}
			System.out.print("송금할 계좌 선택 : ");
			int index = scan.nextInt() - 1;
			oos.writeInt(index);
			oos.flush();
			int transfer = 0;
			result = ois.readUTF();
			if (result.equals("번호를 잘못선택했습니다.")) {
				System.out.println(result);
				break;
			}
			scan.nextLine();
			// 금액이 정규표현식에 적합해야 변수에 저장
			str = regexAmount(result);
			transfer = Integer.parseInt(str);
			oos.writeInt(transfer);
			oos.flush();
			result = ois.readUTF();
			if (result.equals(balanceFormat(transfer) + "원은 송금할 수 없습니다.")) {
				System.out.println(result);
				break;
			}
			System.out.println("송금중입니다...");
			Thread.sleep(1000);
			System.out.println(balanceFormat(transfer) + "원을 송금하였습니다.");
			result = ois.readUTF();
			System.out.println(result);
			printBar();
			System.out.print("돌아가려면 엔터를 입력하세요.");
			
			enter = scan.nextLine();
			if (enter.equals("\n")) {
				break;
			}
			break;

		case 7:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("check");
			oos.flush();
			// 계좌번호가 정규표현식에 적합해야 변수에 저장
			ac_num = regexAccount();
			// 계좌번호가 있는지 확인하기 위해 서버로 계좌번호를 보내는 메소드
			isTrue = sendAccount(oos, ois, ac_num);
			if(!isTrue) {break;} 
			// 계좌에 비밀번호가 맞는지 확인하기 위해 서버로 비밀번호를 보내는 메소드
			escape = sendPw(oos, ois, ac_num);
			if (escape) {break A;}
			result = ois.readUTF();
			System.out.println(result);
			System.out.println("통장을 조회중입니다...");
			Thread.sleep(1000);
			System.out.println("=================================통장내역=================================");
			System.out.println(String.format("%-22s", "날짜") + String.format("%18s", "변동금액")
								+ String.format("%18s", "잔고") + String.format("%11s", "내역"));
			System.out.println("------------------------------------------------------------------------");
			size = ois.readInt();
			for (int i = 0; i < size; i++) {
				result = ois.readUTF();
				System.out.println(result);
			}
			System.out.println("========================================================================");
			System.out.print("돌아가려면 엔터를 입력하세요.");
			
			enter = scan.nextLine();
			if (enter.equals("\n")) {break;}
			break;

		case 8:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("end");
			oos.flush();
			result = ois.readUTF();
			System.out.println(result);
			printBar();
			break;

		default:
			scan.nextLine();
			socket = new Socket(ip, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeUTF("");
			oos.flush();
			System.out.println("잘못된 메뉴입니다.");
			printBar();
		}
	}
	
	private String balanceFormat(int balance) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(balance);
	}

	private boolean sendAccount(ObjectOutputStream oos, ObjectInputStream ois, String ac_num) throws IOException {
		oos.writeUTF(ac_num);
		oos.flush();
		String result = ois.readUTF();
		if (result.equals("없는 계좌입니다.")) {
			System.out.println(result);
			return false;
		}
		return true;
	}

	private boolean sendPw(ObjectOutputStream oos, ObjectInputStream ois, String ac_num) throws IOException {
		do {
			System.out.print("비밀번호(메뉴로 돌아가기 : 0) : ");
			String ac_pw = scan.nextLine();
			oos.writeUTF(ac_pw);
			oos.flush();
			String result = ois.readUTF();
			if (result.equals("메뉴로 돌아갑니다.")) {
				System.out.println(result);
				printBar();
				return true;
			}
			if (result.equals("잘못된 비밀번호입니다. 다시 입력하세요.")) {
				System.out.println(result);
				continue;
			}
			break;
		} while (true);

		return false;
	}

	private String regexAmount(String message) {
		String str;
		
		do {
			System.out.print(message);
			str = scan.nextLine();
			String regex = "^\\d{0,10}$";
			if (!Pattern.matches(regex, str)) {
				System.out.println("올바른 금액을 입력하세요.");
				continue;
			}
			break;
		} while (true);
		return str;
	}

	private String regexPw(String message) {
		String ac_pw;
		do {
			System.out.print(message + "비밀번호(4자리) : ");
			ac_pw = scan.nextLine();
			String regex = "^\\d{4}$";
			if (!Pattern.matches(regex, ac_pw)) {
				System.out.println("잘못된 비밀번호 형식입니다. 다시 입력하세요.");
				continue;
			}
			break;
		} while (true);
		return ac_pw;
	}

	private String regexAccount() {
		String ac_num;
		do {
			System.out.print("계좌번호 : ");
			ac_num = scan.nextLine();
			String regex = "^1010-\\d{4}$";
			if (!Pattern.matches(regex, ac_num)) {
				System.out.println("잘못된 계좌번호입니다. 다시 입력하세요.");
				continue;
			}
			break;
		} while (true);
		return ac_num;
	}
	
	private String regexName() {
		String ac_name;
		do {
			System.out.print("예금주명 : ");
			ac_name = scan.nextLine();
			String regex = "^[가-힣]{2,4}$";
			if (!Pattern.matches(regex, ac_name)) {
				System.out.println("잘못된 이름입니다. 다시 입력하세요.");
				continue;
			}
			break;
		} while (true);
		return ac_name;
	}
	
	private String regexSearch() {
		String search;
		do {
			System.out.print("송금할 계좌번호 혹은 예금주명 : ");
			search = scan.nextLine();
			String regex = "^[가-힣]{1,4}$";
			String regex2 = "^1010-\\d{4}$";
			if (!Pattern.matches(regex, search) && !Pattern.matches(regex2, search)) {
				System.out.println("잘못된 계좌번호 혹은 이름입니다. 다시 입력하세요.");
				continue;
			}
			break;
		} while (true);
		return search;
	}

	private void printBar() {
		System.out.println("-----------------------------------");
	}

	public void run() {
		int menu = 0;
		do {
			printMenu();
			try {
				menu = scan.nextInt();
				runMenu(menu);
			} catch (InputMismatchException e) {
				printBar();
				System.out.println("잘못된 입력입니다.");
				scan.nextLine();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (menu != 8);
	}
}