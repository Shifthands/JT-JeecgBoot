package org.jeecg.modules.demo.PaymentTicket.mapper;

import java.util.List;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 发票子表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface PaymentTicketDetailMapper extends BaseMapper<PaymentTicketDetail> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<PaymentTicketDetail>
   */
	public List<PaymentTicketDetail> selectByMainId(@Param("mainId") String mainId);
}
