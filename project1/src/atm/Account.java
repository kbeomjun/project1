package atm;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account implements Serializable, Comparable<Account> {
	private static final long serialVersionUID = -2655162611679713315L;
	private String accountNum;
	private String password;
	private String name;
	private int balance;
	private static String bank = "KH은행";
	@Override
	public String toString() {
		return bank+" "+accountNum+"(예금주:"+name+") 잔고 : "+balance+"원";
	}
	@Override
	public int compareTo(Account o) {
		return accountNum.compareTo(o.accountNum);
	}
}