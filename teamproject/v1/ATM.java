package teamproject.v1;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public class ATM implements Serializable{

	private static final long serialVersionUID = 566239752834975356L;
	
	private String name;		// 예금주
	private String accountNum;	// 계좌번호
	private String pw;			// 비밀번호
	private int money = 0;		// 잔액
	
	public ATM(String name, String pw) throws Exception {
		this.name = name;
		setPw(pw);
	}
	
	public ATM(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, pw);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ATM other = (ATM) obj;
		return Objects.equals(name, other.name) && Objects.equals(pw, other.pw);
	}

	@Override
	public String toString() {
		return name + " : " + accountNum;
	}
	
	// 입금
	public void deposit(int money) {
		this.money += money;
	}
	
	// 출금
	public boolean withdraw(int money) {
		if(this.money - money < 0) {
			return false;
		}
		this.money -= money;
		return true;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) throws Exception {
		String regex = "^\\d{4}$";
		if(!Pattern.matches(regex, pw)) {
			throw new Exception("올바른 비밀번호 형식이 아닙니다.");
		}
		
		this.pw = pw;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money += money;
	}

	
	
}
