package org.jeecg.modules.demo.PaymentTicket.vo;

import java.util.List;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicket;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 付款发票
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Data
@Schema(description="付款发票")
public class PaymentTicketPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**创建人*/
	@Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
	@Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "更新日期")
    private Date updateTime;
	/**所属部门*/
	@Schema(description = "所属部门")
    private String sysOrgCode;
	/**发票*/
	@Excel(name = "发票", width = 15)
	@Schema(description = "发票")
    private String ticket;
	/**关联付款单*/
	@Excel(name = "关联付款单", width = 15)
	@Schema(description = "关联付款单")
    private String pamentlist;

	@ExcelCollection(name="发票子表")
	@Schema(description = "发票子表")
	private List<PaymentTicketDetail> paymentTicketDetailList;
	/**发票校验结果*/
	@Excel(name = "发票校验结果", width = 15, dicCode = "ticket_check_code")
	@Dict(dicCode = "ticket_check_code")
	@Schema(description = "发票校验结果")
	private String ticketcheck;


}
