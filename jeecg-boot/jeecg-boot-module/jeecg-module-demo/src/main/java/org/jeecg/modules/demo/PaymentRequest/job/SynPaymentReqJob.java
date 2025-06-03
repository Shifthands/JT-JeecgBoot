package org.jeecg.modules.demo.PaymentRequest.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.demo.DPT.entity.Ticket;
import org.jeecg.modules.demo.DPT.service.ITicketService;
import org.jeecg.modules.demo.PaymentRequest.entity.PaymentRequest;
import org.jeecg.modules.demo.PaymentRequest.service.IPaymentRequestService;
import org.jeecg.modules.demo.util.DingTalkUtil;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysThirdAccountService;
import org.jeecg.modules.system.service.ISysUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SynPaymentReqJob implements Job {

	@Autowired
	ITicketService ticketService;
	@Autowired
	DingTalkUtil util;
	@Autowired
	IPaymentRequestService paymentRequestService;
	@Autowired
	ISysThirdAccountService sysThirdAccountService;
	@Autowired
	ISysUserService userService;
	@SneakyThrows
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		// 获取当前日期
		Calendar calendar = Calendar.getInstance();
		long startTime = DateUtils.parseDate(jobExecutionContext.getMergedJobDataMap().getString("parameter"), "yyyy-MM-dd HH:mm:ss").getTime();
		List<String> instIds = util.getDingtalkWorkflowInstanceIds("PROC-B9978930-7EDF-4420-B483-72D560757086", startTime, calendar.getTime().getTime(), "", "COMPLETED");
		for (String instId : instIds) {
			Long instid = paymentRequestService.query().eq("instid", instId).count();
			if (instid>0l)continue;

			GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult oaInstDetail = util.getOAInstDetail(instId);
			String businessId = oaInstDetail.getBusinessId();
			Long oanumber = paymentRequestService.query().eq("oanumber", businessId).count();
			if (oanumber>0l)continue;
			String originatorUserId = oaInstDetail.getOriginatorUserId();
			SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneByThirdUserId(originatorUserId, "dingtalk");

			String originatorDeptName = oaInstDetail.getOriginatorDeptName();
			List<GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultFormComponentValues> formComponentValues = oaInstDetail.getFormComponentValues();
			PaymentRequest paymentRequest = new PaymentRequest();
			paymentRequest.setOanumber(businessId);
			paymentRequest.setDept(originatorDeptName);
			paymentRequest.setInstid(instId);
			paymentRequest.setTicketstatus("未上传");
			if(sysThirdAccount!=null){
				String realname = sysThirdAccount.getRealname();
				String sysUserId = sysThirdAccount.getSysUserId();SysUser byId = userService.getById(sysUserId);
				String username = byId.getUsername();
				paymentRequest.setCreateBy(username);
				paymentRequest.setCreator(realname);
			}
			for (GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultFormComponentValues formComponentValue : formComponentValues) {
				String name = formComponentValue.getName();
				String value = formComponentValue.getValue();
				if(name.contains("支付金额"))paymentRequest.setAmount(new BigDecimal(value));
				if(name.contains("付款事由"))paymentRequest.setToptic(value.length()>499?value.substring(0,499):value);
				if(name.contains("经营支出类型"))paymentRequest.setNote(value);
			}
			paymentRequestService.save(paymentRequest);
		}
	}
}
