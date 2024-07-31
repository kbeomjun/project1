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
		return format(dt_date)+" | "+dt_detail+" | "+balanceFormat(dt_money)+"원  |  "+"잔고 : "+balanceFormat(dt_balance)+"원";
	}
	
	public String format(Date dt_date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(dt_date);
	}
	
	public String balanceFormat(int balance) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(balance);
	}
}