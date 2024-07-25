package atm4_db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Detail implements Serializable {
	private static final long serialVersionUID = 8292997162766257306L;
	private int detailNum;
	@NonNull
	private String detail;
	@NonNull
	private int money;
	@NonNull
	private Date date;
	@NonNull
	private int balance;
	@NonNull
	private String accountNum;
	@Override
	public String toString() {
		return detailNum+". "+date+" | "+detail+" | "+money+"원  |  "+"잔고 : "+balance+"원";
	}
}