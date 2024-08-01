package atm4_db.vo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailVO {
	
	private int dt_num;
	private String dt_detail;
	private int dt_money;
	private Date dt_date;
	private int dt_balance;
	private String dt_ac_num;
	
	@Override
	public String toString() {
		String str1 = dateFormat(dt_date);
		String str2 = dt_detail;
		String str3 = balanceFormat(dt_money) + "원";
		String str4 = balanceFormat(dt_balance) + "원";
		return String.format("%19s", str1) + " | " + String.format("%15s", str3) + " | "
				+ String.format("%15s", str4) + " | " + String.format("%8s", str2); 
	}
	
	public String dateFormat(Date dt_date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(dt_date);
	}
	
	public String balanceFormat(int balance) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(balance);
	}
}