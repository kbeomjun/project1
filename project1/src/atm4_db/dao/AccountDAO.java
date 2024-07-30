package atm4_db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import atm4_db.vo.AccountVO;

public interface AccountDAO {
	public boolean insertAccount(
			@Param("ac_num")String ac_num, 
			@Param("ac_pw")String ac_pw, 
			@Param("ac_name")String ac_name, 
			@Param("ac_balance")int ac_balance);

	public AccountVO selectAccount(@Param("ac_num")String ac_num);

	public void deleteAccount(@Param("ac_num")String ac_num);

	public void updateAccountPw(@Param("ac_num")String ac_num, @Param("ac_pw")String ac_pw);

	public void updateAccountBalance(@Param("ac_num")String ac_num, @Param("ac_balance")int ac_balance);

	public List<AccountVO> selectAccountList();
}