package atm2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Account implements Serializable, Comparable<Account> {
	private static final long serialVersionUID = -2655162611679713315L;
	@NonNull
	private String accountNum;
	@NonNull
	private String password;
	@NonNull
	private String name;
	@NonNull
	private int balance;
	private List<String> bankBook = new ArrayList<String>();
	private String bank = "KH은행";
	@Override
	public String toString() {
		return bank+" "+accountNum+"(예금주:"+name+") 잔고 : "+balance+"원";
	}
	@Override
	public int compareTo(Account o) {
		return accountNum.compareTo(o.accountNum);
	}
}