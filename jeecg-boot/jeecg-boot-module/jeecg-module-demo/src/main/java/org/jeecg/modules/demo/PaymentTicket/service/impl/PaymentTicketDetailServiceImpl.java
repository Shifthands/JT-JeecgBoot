package org.jeecg.modules.demo.PaymentTicket.service.impl;

import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import org.jeecg.modules.demo.PaymentTicket.mapper.PaymentTicketDetailMapper;
import org.jeecg.modules.demo.PaymentTicket.service.IPaymentTicketDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 发票子表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class PaymentTicketDetailServiceImpl extends ServiceImpl<PaymentTicketDetailMapper, PaymentTicketDetail> implements IPaymentTicketDetailService {
	
	@Autowired
	private PaymentTicketDetailMapper paymentTicketDetailMapper;
	
	@Override
	public List<PaymentTicketDetail> selectByMainId(String mainId) {
		return paymentTicketDetailMapper.selectByMainId(mainId);
	}
}
