package org.jeecg.modules.demo.PaymentRequest.service.impl;

import org.jeecg.modules.demo.PaymentRequest.entity.PaymentRequest;
import org.jeecg.modules.demo.PaymentRequest.mapper.PaymentRequestMapper;
import org.jeecg.modules.demo.PaymentRequest.service.IPaymentRequestService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 付款申请
 * @Author: jeecg-boot
 * @Date:   2025-05-20
 * @Version: V1.0
 */
@Service
public class PaymentRequestServiceImpl extends ServiceImpl<PaymentRequestMapper, PaymentRequest> implements IPaymentRequestService {

}
