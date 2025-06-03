package org.jeecg.modules.demo.DPT.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.demo.DPT.entity.Ticket;
import org.jeecg.modules.demo.DPT.service.ITicketService;
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

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SynTicketJob implements Job {

	@Autowired
	ITicketService ticketService;
	@SneakyThrows
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		// 获取当前日期
		Calendar calendar = Calendar.getInstance();

		// 格式化今天日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		String todayStr = sdf.format(calendar.getTime());
		String todayStr1 = "2026-1-1";

		// 获取昨天日期
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String yesterdayStr = sdf.format(calendar.getTime());
		String yesterdayStr1 = "2020-1-1";

		String parameter = jobExecutionContext.getMergedJobDataMap().getString("parameter");
		if (parameter!=null){
			yesterdayStr=parameter;
		}
//		获取进项发票池
		// 1. 创建 HttpClient
		HttpClient client = HttpClient.newHttpClient();
		String params = "token=cddf62af6a834658bef9c32d497df883y3mz7qdhry" +
				"&begintime=" +yesterdayStr+
				"&endtime=" +todayStr+
				"&pageindex=1" +
				"&pagesize=1000" +
				"&spid=ee79fbeca7f9f68ae5996c3ae5b016bd";
		// 3. 构建 HttpRequest
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://fp.xinpukeji.cn/wechatapi/NEWKP/JXFPAPI/JXMX"))
				.header("Content-Type", "application/x-www-form-urlencoded") // 设置请求头
				.POST(HttpRequest.BodyPublishers.ofString(params))
				.build();

		// 4. 发送请求并获取响应
		HttpResponse<String> response = null;
		try {
			response = client.send(
					request,
					HttpResponse.BodyHandlers.ofString()
			);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JSONObject rsp = JSONObject.parseObject(response.body());
		JSONArray rows = rsp.getJSONArray("rows");
		for (Object row : rows) {
			String fphm = ((JSONObject) row).getString("FPHM");
//			同步到本地
			Long count = ticketService.query().eq("FPHM", fphm).count();
			if(count>0){
				continue;
			}
			Ticket ticket = new Ticket();
//			购方名称
			ticket.setKhmc(((JSONObject) row).getString("KHMC"));
//			开票人
			ticket.setKpr(((JSONObject)row).getString("KPR"));
//			销方名称
			ticket.setXfmc(((JSONObject)row).getString("XFMC"));
//			票种说明
			ticket.setPzsm(((JSONObject)row).getString("PZSM"));
//			整张发票金额
			ticket.setKpje(new BigDecimal(((JSONObject)row).getString("KPJE")));
//			购方税号
			ticket.setKhsh(((JSONObject)row).getString("KHSH"));
//			整张发票税额
			ticket.setKpse(new BigDecimal(((JSONObject)row).getString("KPSE")));
//			发票种类
			ticket.setFpzl(((JSONObject)row).getString("FPZL"));
//			销方税号
			ticket.setXfsh(((JSONObject)row).getString("XFSH"));
			ticket.setStatus("0");
//			发票号码
			ticket.setFphm(fphm);
			ticketService.save(ticket);
			log.info("发票号码同步："+fphm);
		}

	}
}
