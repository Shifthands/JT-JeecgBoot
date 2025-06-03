package org.jeecg.modules.demo.PaymentTicket.service;

import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicket;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 付款发票
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface IPaymentTicketService extends IService<PaymentTicket> {

	/**
	 * 添加一对多
	 *
	 * @param paymentTicket
	 * @param paymentTicketDetailList
	 */
	public void saveMain(PaymentTicket paymentTicket, List<PaymentTicketDetail> paymentTicketDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param paymentTicket
   * @param paymentTicketDetailList
	 */
	public void updateMain(PaymentTicket paymentTicket, List<PaymentTicketDetail> paymentTicketDetailList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain(String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain(Collection<? extends Serializable> idList);
	
}
