package atm4_db.vo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountVO {
	private String ac_num;
	private String ac_pw;
	private String ac_name;
	private int ac_balance;
	
	private String password;
	private List<String> bankBook;
	
	@Override
	public String toString() {
		return "KH은행 "+ac_num+"(예금주:"+ac_name+") 잔고 : "+format(ac_balance)+"원";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountVO other = (AccountVO) obj;
		return Objects.equals(ac_name, other.ac_name) && Objects.equals(ac_pw, other.ac_pw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(ac_name, ac_pw);
	}
	
	private String format(int balance) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(balance);
	}
	
}