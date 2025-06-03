package org.jeecg.modules.demo.PaymentTicket.service;

import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 发票子表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface IPaymentTicketDetailService extends IService<PaymentTicketDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<PaymentTicketDetail>
	 */
	public List<PaymentTicketDetail> selectByMainId(String mainId);
}
