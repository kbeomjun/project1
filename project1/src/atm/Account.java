package atm;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account implements Serializable {
	private static final long serialVersionUID = -2655162611679713315L;
	private String accountNum;
	private int password;
	private String name;
	private int balance;
	@Override
	public String toString() {
		return "계좌 번호 : "+accountNum+"(예금주 : "+name+") 잔고 : "+balance+"원";
	}
}