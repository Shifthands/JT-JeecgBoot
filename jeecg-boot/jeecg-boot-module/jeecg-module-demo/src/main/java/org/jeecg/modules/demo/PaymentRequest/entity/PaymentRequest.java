package org.jeecg.modules.demo.PaymentRequest.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 付款申请
 * @Author: jeecg-boot
 * @Date:   2025-05-20
 * @Version: V1.0
 */
@Data
@TableName("payment_request")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="付款申请")
public class PaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
	/**金额*/
	@Excel(name = "金额", width = 15)
    @Schema(description = "金额")
    private BigDecimal amount;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String note;
	/**付款事由*/
	@Excel(name = "付款事由", width = 15)
    @Schema(description = "付款事由")
    private String toptic;
	/**审批编号*/
	@Excel(name = "审批编号", width = 15)
    @Schema(description = "审批编号")
    private String oanumber;
    private String ticketstatus;
    private String creator;
    private String dept;
    private String instid;
    @Schema(description = "发票关联实例")
    private String ticketinstid;
}
