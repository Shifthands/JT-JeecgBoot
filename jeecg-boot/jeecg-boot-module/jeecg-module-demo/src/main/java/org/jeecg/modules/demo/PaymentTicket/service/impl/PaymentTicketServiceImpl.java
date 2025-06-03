package org.jeecg.modules.demo.PaymentTicket.service.impl;

import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicket;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import org.jeecg.modules.demo.PaymentTicket.mapper.PaymentTicketDetailMapper;
import org.jeecg.modules.demo.PaymentTicket.mapper.PaymentTicketMapper;
import org.jeecg.modules.demo.PaymentTicket.service.IPaymentTicketService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 付款发票
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class PaymentTicketServiceImpl extends ServiceImpl<PaymentTicketMapper, PaymentTicket> implements IPaymentTicketService {

	@Autowired
	private PaymentTicketMapper paymentTicketMapper;
	@Autowired
	private PaymentTicketDetailMapper paymentTicketDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(PaymentTicket paymentTicket, List<PaymentTicketDetail> paymentTicketDetailList) {
		paymentTicketMapper.insert(paymentTicket);
		if(paymentTicketDetailList!=null && paymentTicketDetailList.size()>0) {
			for(PaymentTicketDetail entity:paymentTicketDetailList) {
				//外键设置
				entity.setMainid(paymentTicket.getId());
				paymentTicketDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(PaymentTicket paymentTicket,List<PaymentTicketDetail> paymentTicketDetailList) {
		paymentTicketMapper.updateById(paymentTicket);
		
		//1.先删除子表数据
		paymentTicketDetailMapper.deleteByMainId(paymentTicket.getId());
		
		//2.子表数据重新插入
		if(paymentTicketDetailList!=null && paymentTicketDetailList.size()>0) {
			for(PaymentTicketDetail entity:paymentTicketDetailList) {
				//外键设置
				entity.setMainid(paymentTicket.getId());
				paymentTicketDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		paymentTicketDetailMapper.deleteByMainId(id);
		paymentTicketMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			paymentTicketDetailMapper.deleteByMainId(id.toString());
			paymentTicketMapper.deleteById(id);
		}
	}
	
}
