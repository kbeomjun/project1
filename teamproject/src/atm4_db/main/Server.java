package atm4_db.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import atm4_db.dao.AccountDAO;
import atm4_db.dao.DetailDAO;
import atm4_db.vo.AccountVO;
import atm4_db.vo.DetailVO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Server extends Thread{
	private Socket socket = new Socket();
	private static AccountDAO accountDao;
	private static DetailDAO detailDao;
	
	@Override
	public synchronized void run() {
		try {
	        init();
	        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	        String type = ois.readUTF();
	        switch(type) {
	            case "insert":
	                ATMInsert(ois, oos);
	                break;
	            case "remove":
	                ATMRemove(ois, oos);
	                break;
	            case "update":
	                ATMUpdate(ois, oos);
	                break;
	            case "deposit":
	                ATMDeposit(ois, oos);
	                break;
	            case "withdraw":
	                ATMWithdraw(ois, oos);
	                break;
	            case "transfer":
	                ATMTransfer(ois, oos);
	                break;
	            case "check":
	                ATMCheck(ois, oos);
	                break;
	            case "end":
	                ATMEnd(oos);
	                break;
	            default:
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void ATMEnd(ObjectOutputStream oos) throws IOException {
		System.out.println("접속해제");
		oos.writeUTF("업무를 종료합니다.");
		oos.flush();
	}

	private void ATMCheck(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		String ac_num = ois.readUTF();
		AccountVO tmp = accountDao.selectAccount(ac_num);
		if(tmp == null) {
			oos.writeUTF("없는 계좌입니다.");
			oos.flush();
			return;
		}
		oos.writeUTF("");
		oos.flush();
		do {
			String ac_pw = ois.readUTF();
			if(ac_pw.equals("0")) {
				oos.writeUTF("메뉴로 돌아갑니다.");
				oos.flush();
				return;
			}
			if(!tmp.getAc_pw().equals(ac_pw)) {
				oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
				oos.flush();
				continue;
			}
			oos.writeUTF("");
			oos.flush();
			break;
		}while(true);
		oos.writeUTF(tmp.toString());
		oos.flush();
		System.out.println(tmp.getAc_name()+"님이 통장조회중...");
		String dt_ac_num = ac_num;
		List<DetailVO> bankBook = detailDao.getDetail(dt_ac_num);
		oos.writeInt(bankBook.size());
		oos.flush();
		for(int i = 0; i < bankBook.size(); i++) {
			oos.writeUTF((i+1)+". "+bankBook.get(i).toString());
			oos.flush();
		}
		System.out.println(tmp.getAc_name()+"님이 통장을 조회했습니다.");
	}
	private void ATMInsert(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    System.out.println("계좌개설중...");
	    String ac_name = ois.readUTF();
	    String ac_pw = ois.readUTF();
	    int result;
	    String ac_num;
	    do {
	        result = 0;
	        ac_num = createAccountNum();
	        try {
	            if(!accountDao.insertAccount(ac_num, ac_pw, ac_name, 0)) {
	                result++;
	            }
	        } catch (Exception e) {
	            result++;
	        }
	    } while(result != 0);
	    oos.writeUTF(ac_num);
	    oos.flush();
	    System.out.println(ac_name + "님이 계좌를 개설했습니다.");
	    detailDao.insertDetail("계좌개설", 0, 0, ac_num);
	}

	private void ATMRemove(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    String ac_num = ois.readUTF();
	    AccountVO tmp = accountDao.selectAccount(ac_num);
	    if(tmp == null) {
	        oos.writeUTF("없는 계좌입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    while (true) {
	        String ac_pw = ois.readUTF();
	        if(ac_pw.equals("0")) {
	            oos.writeUTF("메뉴로 돌아갑니다.");
	            oos.flush();
	            return;
	        }
	        if(!tmp.getAc_pw().equals(ac_pw)) {
	            oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
	            oos.flush();
	        } else {
	            oos.writeUTF("");
	            oos.flush();
	            break;
	        }
	    }
	    System.out.println(tmp.getAc_name() + "님이 계좌해지중...");
	    detailDao.deleteDetail(ac_num);
	    accountDao.deleteAccount(ac_num);
	    System.out.println(tmp.getAc_name() + "님이 계좌를 해지했습니다.");
	}

	private void ATMUpdate(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    String ac_num = ois.readUTF();
	    AccountVO tmp = accountDao.selectAccount(ac_num);
	    if(tmp == null) {
	        oos.writeUTF("없는 계좌입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    while (true) {
	        String ac_pw = ois.readUTF();
	        if(ac_pw.equals("0")) {
	            oos.writeUTF("메뉴로 돌아갑니다.");
	            oos.flush();
	            return;
	        }
	        if(!tmp.getAc_pw().equals(ac_pw)) {
	            oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
	            oos.flush();
	        } else {
	            oos.writeUTF("");
	            oos.flush();
	            break;
	        }
	    }
	    String new_ac_pw = ois.readUTF();
	    if(tmp.getAc_pw().equals(new_ac_pw)) {
	        oos.writeUTF("동일한 비밀번호입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    System.out.println(tmp.getAc_name() + "님이 비밀번호 변경중...");
	    accountDao.updateAccountPw(ac_num, new_ac_pw);
	    System.out.println(tmp.getAc_name() + "님이 비밀번호를 변경했습니다.");
	}

	private void ATMDeposit(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    int deposit = ois.readInt();
	    if(deposit <= 0) {
	        oos.writeUTF(deposit + "원은 입금할 수 없습니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    String ac_num = ois.readUTF();
	    AccountVO tmp = accountDao.selectAccount(ac_num);
	    if(tmp == null) {
	        oos.writeUTF("없는 계좌입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    System.out.println(tmp.getAc_name() + "님이 입금중...");
	    int ac_balance = tmp.getAc_balance() + deposit;
	    accountDao.updateAccountBalance(ac_num, ac_balance);
	    oos.writeUTF(tmp.getAc_name() + "님의 남은 잔고 : " + ac_balance + "원");
	    oos.flush();
	    detailDao.insertDetail("입금", deposit, ac_balance, ac_num);
	    System.out.println(tmp.getAc_name() + "님이 입금하였습니다.");
	}

	private void ATMWithdraw(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    String ac_num = ois.readUTF();
	    AccountVO tmp = accountDao.selectAccount(ac_num);
	    if(tmp == null) {
	        oos.writeUTF("없는 계좌입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    while (true) {
	        String ac_pw = ois.readUTF();
	        if(ac_pw.equals("0")) {
	            oos.writeUTF("메뉴로 돌아갑니다.");
	            oos.flush();
	            return;
	        }
	        if(!tmp.getAc_pw().equals(ac_pw)) {
	            oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
	            oos.flush();
	        } else {
	            oos.writeUTF("");
	            oos.flush();
	            break;
	        }
	    }
	    if(tmp.getAc_balance() == 0) {
	        oos.writeUTF("잔액이 0원이므로 출금할 수 없습니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    oos.writeUTF("출금할 금액(잔액 : " + tmp.getAc_balance() + "원) : ");
	    oos.flush();
	    int withdraw = ois.readInt();
	    if(withdraw <= 0 || tmp.getAc_balance() < withdraw) {
	        oos.writeUTF(withdraw + "원은 출금할 수 없습니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    System.out.println(tmp.getAc_name() + "님이 출금중...");
	    int ac_balance = tmp.getAc_balance() - withdraw;
	    accountDao.updateAccountBalance(ac_num, ac_balance);
	    oos.writeUTF(tmp.getAc_name() + "님의 남은 잔고 : " + ac_balance + "원");
	    oos.flush();
	    detailDao.insertDetail("출금", -withdraw, ac_balance, ac_num);
	    System.out.println(tmp.getAc_name() + "님이 출금하였습니다.");
	}

	private void ATMTransfer(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
	    String ac_num = ois.readUTF();
	    AccountVO tmp = accountDao.selectAccount(ac_num);
	    if(tmp == null) {
	        oos.writeUTF("없는 계좌입니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    while (true) {
	        String ac_pw = ois.readUTF();
	        if(ac_pw.equals("0")) {
	            oos.writeUTF("메뉴로 돌아갑니다.");
	            oos.flush();
	            return;
	        }
	        if(!tmp.getAc_pw().equals(ac_pw)) {
	            oos.writeUTF("잘못된 비밀번호입니다. 다시 입력하세요.");
	            oos.flush();
	        } else {
	            oos.writeUTF("");
	            oos.flush();
	            break;
	        }
	    }
	    if(tmp.getAc_balance() == 0) {
	        oos.writeUTF("잔액이 0원이므로 송금할 수 없습니다.");
	        oos.flush();
	        return;
	    }
	    oos.writeUTF("");
	    oos.flush();
	    
	    String search = ois.readUTF();
	    List<AccountVO> list = accountDao.selectAccountList();
	    List<AccountVO> list2 = new ArrayList<>();
	    for(AccountVO account : list) {
	        if(account.getAc_num().equals(ac_num)) {
	            continue;
	        }
	        if(account.getAc_num().contains(search) || account.getAc_name().contains(search)) {
	            list2.add(account);
	        }
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
	public static void init() {
		String resource = "atm4_db/config/mybatis-config.xml";
		InputStream inputStream;
		SqlSession session;
		try {
			inputStream = Resources.getResourceAsStream(resource);
			SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			session = sessionFactory.openSession(true);
			accountDao = session.getMapper(AccountDAO.class);
			detailDao = session.getMapper(DetailDAO.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}