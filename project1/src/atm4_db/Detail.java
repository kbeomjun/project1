package atm4_db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class Detail implements Serializable {
	private static final long serialVersionUID = 8292997162766257306L;
	private int detailNum = 0;
	private String detail;
	private int money;
	private Date date = new Date();
	private List<String> bankBook = new ArrayList<String>();
	@Override
	public String toString() {
		return detailNum+". "+date+" "+detail+" "+money+"원  |  "+"잔고 : ";
	}
	public Detail(String detail, int money, Date date) {
		this.detailNum++;
		this.detail = detail;
		this.money = money;
		this.date = date;
		this.bankBook.add(detailNum+". "+date+" "+detail+" "+money+"원  |  "+"잔고 : ");
	}	
}