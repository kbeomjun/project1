package teamproject.v1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import program.Program;

public class ATMManager implements Program {

	Scanner sc = new Scanner(System.in);
	List<ATM> list = new ArrayList<ATM>();
	Set<String> set = new HashSet<String>(); // 계좌번호 중복 확인을 위한 Set
	String fileName = "src/teamproject/atm.txt";
	
	@Override
	public void printMenu() {
		System.out.println("메뉴");
		System.out.println("1. 계좌등록");
		System.out.println("2. 입금");
		System.out.println("3. 출금");
		System.out.println("4. 계좌목록");
		System.out.println("5. 종료");
		System.out.print("메뉴 입력 : ");
	}

	@SuppressWarnings("unchecked")
	@Override 
	public void load(String fileName) {
		try(FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis)) {
			list = (List<ATM>)ois.readObject();
		} catch (Exception e) {
			System.out.println("파일 불러오기에 실패하였습니다.");
		} 
	}
	
	@Override
	public void save(String fileName) {
		try(FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(list);
		} catch (Exception e) {
			System.out.println("파일 저장에 실패하였습니다.");
		}
	}
	
	
	@Override
	public void run() {
		
		load(fileName);
		int menu = 0;
		
		do {
			printMenu();
			menu = sc.nextInt();
			printBar();
			try {
				runMenu(menu);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while(menu != 5);
		save(fileName);
	}
	
	@Override
	public void runMenu(int menu) throws Exception {
		
		switch(menu) {
		case 1:
			registration();	// 계좌 등록
			break;
		case 2:
			deposit();		// 입금
			break;
		case 3:
			withdraw();		// 출금
			break;
		case 4:				// 계좌목록
			accountList(); 
			break;
		case 5:
			exit();
			break;
		default:
		}
	}
	
	private void exit() {
		System.out.println("프로그램을 종료합니다.");
	}


	private void accountList() {
		System.out.print("이름 입력 :");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		for (ATM atm : list) {
			if(atm.getName().contains(name)) {
				System.out.println(list);
			}
		}
		printBar();
		System.out.print("메뉴로 돌아가고 싶으면 엔터를 누르세요 : ");
		sc.nextLine();
		printBar();
	}


	private void withdraw() throws Exception {
		// 이름과 비밀번호를 입력받고
		System.out.print("이름 : ");
		sc.nextLine();
		String name = sc.nextLine();
		System.out.print("비밀번호(4자리) : ");
		String pw = sc.next();
		
		// 객체에 이름과 비밀번호를 저장하고
		ATM atm = new ATM(name, pw);
		
		int index = list.indexOf(atm);
		if(index == -1) {
			System.out.println("일치하는 계좌가 없습니다.");
		}
		
		printBar();
		System.out.print("출금할 금액 : ");
		int money = nextInt();
		printBar();
		if(!list.get(index).withdraw(money)) {
			System.out.println("잔액이 부족합니다.");
			printBar();
			return;
		}
		System.out.println("출금이 완료되었습니다.");
		System.out.println("현재 잔액 : " + list.get(index).getMoney());
		printBar();
		
	}


	private void deposit() {
		// 이름과 계좌번호를 입력받고
		System.out.print("이름 : ");
		sc.nextLine();
		String name = sc.nextLine();
		ATM tmp = new ATM(name);
		printBar();
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getName().equals(tmp.getName())) {
				System.out.println(i + 1 + ". " + list.get(i));
			}
		}
		printBar();
		System.out.print("입금할 번호 : ");
		int index = sc.nextInt() - 1;
		printBar();
		System.out.print("입금할 금액 : ");
		int money = sc.nextInt();
		list.get(index).deposit(money);
		printBar();
		System.out.println("입금이 완료되었습니다.");
		System.out.println("현재 잔액 : " + list.get(index).getMoney());
		printBar();
	}


	private void registration() throws Exception {
		// 이름과 비밀번호를 입력받고
		System.out.print("이름 : ");
		sc.nextLine();
		String name = sc.nextLine();
		printBar();
		System.out.print("비밀번호(4자리) : ");
		String pw = sc.next();
		printBar();
		
		// 객체에 이름과 비밀번호를 저장하고
		ATM atm = new ATM(name, pw);
		
		// 이름과 비밀번호가 똑같은 사람이 있는지 확인
		// 있으면 안내문구 출력후 종료
		if(list.contains(atm)) {
			System.out.println("동일한 이름과 비밀번호가 있습니다.");
			printBar();
			return;
		}
		
		// 없으면 계좌번호 생성
		String accountNum = createAccountNum();
		atm.setAccountNum(accountNum);
		System.out.println("계좌번호 : " + accountNum);
		
		// 리스트에 저장
		list.add(atm);
		System.out.println(atm.getName() + "님 계좌가 등록되었습니다.");
		printBar();
	}

	private String createAccountNum() {
		
		int min = 100000, max = 999999;
		String accountNum;
		do {
			int random = (int)(Math.random() * (max - min + 1) + min);
			accountNum = String.valueOf(random);
		}while(!set.add(accountNum));
		
		return String.format("%s-%s", accountNum.substring(0, 3), accountNum.substring(3));
	}


	public void printBar() {
		
		System.out.println("--------------------");
	}

	public int nextInt() {

		try {
			return sc.nextInt();
		} catch(InputMismatchException e) {
			sc.nextLine();
			return Integer.MIN_VALUE;
		}
	}

}
