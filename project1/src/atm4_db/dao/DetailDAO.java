package atm4_db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import atm4_db.vo.DetailVO;

public interface DetailDAO {
	void insertDetail(
			@Param("dt_detail")String dt_detail, 
			@Param("dt_money")int dt_money, 
			@Param("dt_balance")int dt_balance, 
			@Param("dt_ac_num")String dt_ac_num);

	List<DetailVO> getDetail(@Param("dt_ac_num") String dt_ac_num);

	void deleteDetail(@Param("dt_ac_num")String dt_ac_num);
}