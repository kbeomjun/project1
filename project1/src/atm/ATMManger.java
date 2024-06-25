package atm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ATMManger {
	private Scanner scan = new Scanner(System.in);
	private List<Account> list = new ArrayList<Account>();
	
	private void printMenu() {
		System.out.print("1. 계좌개설\n"
				+ "2. 계좌관리\n"
				+ "3. 입금/출금\n"
				+ "4. 예금조회\n"
				+ "5. 계좌이체\n"
				+ "6. 업무종료\n");
		System.out.print("선택 : ");
	}
	
	private void runMenu(int menu) {
		switch(menu) {
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
			System.out.print("예금주명 : ");
			String name = scan.next();
			list.add(new Account(accountNum, password, name, 0));
			System.out.println("계좌를 개설했습니다.");
			Collections.sort(list);
			System.out.println(list);
			break;
		case 2:
			int select = 0;
			System.out.println("계좌 비밀번호를 변경했습니다.");
			System.out.println("계좌를 해지했습니다.");
			break;
		case 3:
			System.out.print("1. 입금\n"
					+"2. 출금\n");
			System.out.print("선택 : ");
			select = scan.nextInt();
			switch(select) {
			case 1:
				System.out.print("입금할 금액 : ");
				int deposit = scan.nextInt();
				if(deposit <= 0) {
					System.out.print(deposit+"원은 입금할 수 없습니다.");
					break;
				}
				int index = findAccount();
				int money = list.get(index).getBalance() + deposit;
				list.get(index).setBalance(money);
				System.out.println(deposit+"원을 입금하였습니다.");
				System.out.println(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
				break;
			case 2:
				index = findAccount();
				checkPassWord(index);
				System.out.print("출금할 금액 : ");
				int withdraw = scan.nextInt();
				if(withdraw <= 0 || list.get(index).getBalance() < withdraw) {
					System.out.println(withdraw+"원은 출금할 수 없습니다.");
					break;
				}
				money = list.get(index).getBalance() - withdraw;
				list.get(index).setBalance(money);
				System.out.println(withdraw+"원을 출금하였습니다.");
				System.out.println(list.get(index).getName()+"님의 남은 잔고 : "+list.get(index).getBalance()+"원");
				break;
			default:
				System.out.println("잘못된 메뉴입니다.");
			}
			break;
		case 4:
			int index = findAccount();
			checkPassWord(index);
			System.out.println(list.get(index).toString());
			System.out.println("예금을 조회했습니다.");
			break;
		case 5:
			System.out.println("계좌를 이체했습니다.");
			break;
		case 6:
			System.out.println("업무를 종료합니다.");
			break;
		default:
			System.out.println("잘못된 메뉴입니다.");
		}
	}
	
	private String createAccountNum() {
		int min = 1, max = 999;
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
			if(list.get(i).getAccountNum().contains(search) || list.get(i).getName().contains(search)) {
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
		//String filename = "";
		//load(filename);
		int menu = 0;
		do {
			printMenu();
			menu = scan.nextInt();
			runMenu(menu);
		}while(menu != 6);
		//save(filename);
	}
}