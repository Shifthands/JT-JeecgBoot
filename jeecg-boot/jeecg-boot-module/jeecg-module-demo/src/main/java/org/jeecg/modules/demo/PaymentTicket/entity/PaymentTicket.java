package org.jeecg.modules.demo.PaymentTicket.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 付款发票
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Schema(description="付款发票")
@Data
@TableName("payment_ticket")
public class PaymentTicket implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**创建人*/
    @Schema(description = "创建人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
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
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    private String sysOrgCode;
	/**发票*/
	@Excel(name = "发票", width = 15)
    @Schema(description = "发票")
    private String ticket;
	/**关联付款单*/
	@Excel(name = "关联付款单", width = 15)
    @Schema(description = "关联付款单")
    private String pamentlist;
	/**发票校验结果*/
    @Excel(name = "发票校验结果", width = 15, dicCode = "ticket_check_code")
    @Dict(dicCode = "ticket_check_code")
    @Schema(description = "发票校验结果")
    private String ticketcheck;

}
