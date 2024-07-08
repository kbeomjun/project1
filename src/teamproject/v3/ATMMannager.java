package teamproject.v3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import program.Program;

public class ATMMannager implements Program{

	private Scanner sc = new Scanner(System.in);
	private List<ATM> list = new ArrayList<ATM>();
	private String fileName = "src/teamproject/v3/ATM.txt";
	private Date date = new Date();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void printMenu() {
		System.out.print(
				"================ATM================\n"
				+ "1. 계좌개설   2. 계좌해지   3. 비밀번호 변경\n"
				+ "4. 입금      5. 출금     6. 송금\n"
				+ "7. 통장조회   8. 업무종료   선택 : ");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void load(String fileName) {
		try(FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis)) {
			list = (List<ATM>)ois.readObject();
		} catch (Exception e) {
			System.out.println("예의 발생");
		} 
	}
	
	@Override
	public void save(String fileName) {
		try(FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(list);
		} catch (Exception e) {
			System.out.println("예외 발생");
		}
	}
	
	@Override
	public void run() {
		load(fileName);
		int menu = 0;
		
		do {
			printMenu();
			try {
				menu = sc.nextInt();
				printBar();
				runMenu(menu);
			} catch (InputMismatchException e) {
				System.out.println("잘못된 메뉴입니다.");
				sc.nextLine();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while(menu != 8);
		save(fileName);
	}
	
	@Override
	public void runMenu(int menu) throws Exception {
		switch(menu) {
		case 1: // 계좌개설
			AccountOpening();
			break;
		case 2: // 계좌해지
			AccountCancellation();
			break;
		case 3: // 비밀번호 변경
			ChangePassword();
			break;
		case 4: // 입금
			deposit();
			break;
		case 5: // 출금
			withdraw();
			break;
		case 6: // 송금
			remittance();
			break;
		case 7: // 통장조회
			check();
			break;
		case 8: // 업무종료
			exit();
			break;
		default:
			defaultPrint();
		}
	}

	private void AccountOpening() throws Exception {
		// 이름과 비밀번호를 입력한후 객체에 저장
		System.out.print("예금주명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		System.out.print("비밀번호(4자리) : ");
		String password = sc.next();
		ATM atm = new ATM(name, password);
		printBar();
		
		// 리스트에 객체가 있는지 확인
		if(list.contains(atm)) {
			// 있다면 안내문구 출력후 종료
			System.out.println("동일한 이름과 비밀번호가 존재합니다.");
			printBar();
			return;
		}
		
		// 없다면 리스트에 추가후 안내문구 출력후 종료
		list.add(atm);
		System.out.println("계좌번호 : " + atm.getAccountNum());
		System.out.println("계좌를 개설하였습니다.");
		String dateStr = format.format(date);
		int index = list.indexOf(atm);
		list.get(index).setList(dateStr + " | 계좌개설 | 잔고 : 0원");
		System.out.println(list);
		printBar();
	}

	private void AccountCancellation() throws Exception {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("해지하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 번호가 맞다면 비밀번호 입력 후 객체를 생성
		System.out.print("비밀번호 : ");
		String password = sc.next();
		printBar();
		ATM tmp = new ATM(name, password);
		
		// 리스트에 객체가 있는지 확인
		if(!list.remove(tmp)) {
			
			// 없다면 안내문구 출력 후 종료
			System.out.println("비밀번호가 맞지 않습니다.");
			printBar();
			return;
		}
		
		// 있다면 삭제 후 안내문구 출력 후 종료
		System.out.println("계좌를 해지하였습니다.");
		printBar();
		
	}
	
	/**
	 * 번지에 리스트가 있는지 없는지 확인
	 * @param index
	 * @param name
	 * @return 이름과 번지가 일치하다면 true 그 외에는 false
	 */
	private boolean checkAccount(int index, String name) {
		if (list == null) {
			return false;
		}
		
		if (index < 0 || index >= list.size()) {
			return false;
		}
		
		return list.get(index).getName().equals(name);
	}

	private void ChangePassword() throws Exception {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("변경하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 번호가 맞다면 비밀번호 입력
		System.out.print("비밀번호 : ");
		String password = sc.next();
		printBar();
		
		// 비밀번호가 맞지 않다면 안내문구 출력 후 종료
		if(!list.get(index).getPassword().equals(password)) {
			System.out.println("비밀번호가 맞지 않습니다.");
			return;
		}
		
		// 맞다면 새 비밀번호 입력
		String newPassword = "";
		do {
			System.out.print("변경할 비밀번호(4자리) : ");
			newPassword = sc.next();
			
			// 비밀번호 형식이 맞지 않다면 안내문구 출력 후 다시 입력
			String regex = "^\\d{4}$";
			if(!Pattern.matches(regex, newPassword)) {
				System.out.println("잘못된 비밀번호 형식입니다. 다시 입력하세요.");
				printBar();
				continue;
			}
			break;
		}while(true);
		
		// 맞다면 비밀번호를 바꿔주고 안내문구 출력 후 종료
		list.get(index).setPassword(newPassword);
		System.out.println("비밀번호를 변경하였습니다.");
		printBar();
	}

	private void deposit() {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("입금하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 입금할 금액 입력받고
		System.out.print("입금할 금액 : ");
		int balance = sc.nextInt();
		printBar();
		
		// 입금이 되면 안내문구 출력 후 종료
		list.get(index).deposit(balance);
		System.out.println(balance + "원을 입금하였습니다.");
		System.out.println(list);
		String dateStr = format.format(date);
		list.get(index).setList(dateStr + " | 입금 "+ balance +"원 | 잔고 : " + list.get(index).getBalance()+"원");
		printBar();
	}

	private void withdraw() {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("출금하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 번호가 맞다면 비밀번호 입력
		System.out.print("비밀번호 : ");
		String password = sc.next();
		printBar();
		
		// 비밀번호가 맞지 않다면 안내문구 출력 후 종료
		if(!list.get(index).getPassword().equals(password)) {
			System.out.println("비밀번호가 맞지 않습니다.");
			return;
		}
		
		// 출금할 금액 입력받고
		System.out.print("출금할 금액 : ");
		int balance = sc.nextInt();
		printBar();
		
		// 출금을 한 후 안내문구 출력 후 종료
		if(!list.get(index).withdraw(balance)) {
			System.out.println("잔액이 부족합니다.");
			return;
		}
		System.out.println(balance + "원을 출금하였습니다.");
		String dateStr = format.format(date);
		list.get(index).setList(dateStr + " | 출금 " + balance + "원 | 잔고 : "+list.get(index).getBalance()+"원");
		printBar();
	}

	private void remittance() {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("송금하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 번호가 맞다면 비밀번호 입력
		System.out.print("비밀번호 : ");
		String password = sc.next();
		printBar();
		
		// 비밀번호가 맞지 않다면 안내문구 출력 후 종료
		if(!list.get(index).getPassword().equals(password)) {
			System.out.println("비밀번호가 맞지 않습니다.");
			return;
		}
		// 이름을 입력 받고
		System.out.print("송금할 예금주 명 : ");
		sc.nextLine();
		String newName = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		count = 0;
		i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(newName)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int newIndex = 0;
		do{
			System.out.print("송금하실 계좌를 선택하세요 : ");
			newIndex = sc.nextInt() - 1;
			if(!checkAccount(newIndex, newName)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(newIndex, newName));
		
		System.out.print("송금하실 금액 : ");
		int balance = sc.nextInt();
		if(!list.get(index).withdraw(balance)) {
			System.out.println("잔액이 부족합니다.");
			printBar();
			return;
		}
		list.get(newIndex).deposit(balance);
		System.out.println(balance + "원을 송금하였습니다.");
		String dateStr = format.format(date);
		list.get(index).setList(dateStr + " | 송금 " + balance + "원 | 잔고 : "+list.get(index).getBalance()+"원");
		dateStr = format.format(date);
		list.get(newIndex).setList(dateStr + " | "+list.get(index).getName() + " " + balance + "원 | 잔고 : " + list.get(newIndex).getBalance() + "원");
		printBar();
	}

	private void check() {
		// 이름을 입력 받고
		System.out.print("예금주 명 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		
		// 리스트에 같은 이름을 가진 객체가 있는지 확인
		int count = 0;
		int i = 1;
		for (ATM atm : list) {
			
			// 있으면 번호와 함께 출력
			if(atm.getName().equals(name)) {
				System.out.println(i + ". 예금주 명 : " + atm.getName() 
				+ " / 계좌번호 : " + atm.getAccountNum());
				count++;
			}
			i++;
		}
		
		// 없으면 안내문구 출력후 종료
		if(count == 0) {
			System.out.println("없는 계좌입니다.");
			printBar();
			return;
		}
		
		// 번호를 입력받고
		// 잘못된 번호이면 안내문구 출력후 다시 입력받는다.
		printBar();
		int index = 0;
		do{
			System.out.print("통장내역 확인하실 계좌를 선택하세요 : ");
			index = sc.nextInt() - 1;
			if(!checkAccount(index, name)) {
				sc.nextLine();
				System.out.println("번호를 잘못 입력하셨습니다.");
			}
			printBar();
		} while(!checkAccount(index, name));
		
		// 번호가 맞다면 비밀번호 입력
		System.out.print("비밀번호 : ");
		String password = sc.next();
		printBar();
		
		// 비밀번호가 맞지 않다면 안내문구 출력 후 종료
		if(!list.get(index).getPassword().equals(password)) {
			System.out.println("비밀번호가 맞지 않습니다.");
			return;
		}
		
		System.out.println("============통장내역============");
		list.get(index).printList();
		System.out.println("==============================");
		System.out.print("돌아가려면 엔터를 입력하세요.");
		sc.nextLine();
		sc.nextLine();
	}

	private void exit() {
		System.out.println("프로그램을 종료합니다.");
		printBar();
	}

	private void defaultPrint() {
		System.out.println("잘못된 번호입니다.");
		printBar();
	}
	
	public void printBar() {
		System.out.println("-----------------------------------");
	}

}
