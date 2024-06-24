package atm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
			System.out.println("계좌를 개설했습니다.");
			break;
		case 2:
			System.out.println("계좌 비밀번호를 변경했습니다.");
			System.out.println("계좌를 해지했습니다.");
			break;
		case 3:
			System.out.println("입금/출금하였습니다.");
			break;
		case 4:
			System.out.println("예금을 조회했습니다.");
			break;
		case 5:
			System.out.println("계좌를 이체했습니다.");
			break;
		case 6:
			System.out.println("업무를 종료합니다.");
			break;
		default:
			System.out.println("잘못된 선택입니다.");
		}
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