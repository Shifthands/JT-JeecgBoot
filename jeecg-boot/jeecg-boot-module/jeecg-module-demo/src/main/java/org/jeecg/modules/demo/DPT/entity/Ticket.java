package org.jeecg.modules.demo.DPT.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 进项发票
 * @Author: jeecg-boot
 * @Date:   2025-05-14
 * @Version: V1.0
 */
@Data
@TableName("ticket")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="进项发票")
public class Ticket implements Serializable {
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
	/**购方名称*/
	@Excel(name = "购方名称", width = 15)
    @Schema(description = "购方名称")
    private String khmc;
	/**开票人*/
	@Excel(name = "开票人", width = 15)
    @Schema(description = "开票人")
    private String kpr;
	/**销方名称*/
	@Excel(name = "销方名称", width = 15)
    @Schema(description = "销方名称")
    private String xfmc;
	/**票种说明*/
	@Excel(name = "票种说明", width = 15)
    @Schema(description = "票种说明")
    private String pzsm;
	/**整张发票金额*/
	@Excel(name = "整张发票金额", width = 15)
    @Schema(description = "整张发票金额")
    private BigDecimal kpje;
	/**购方税号*/
	@Excel(name = "购方税号", width = 15)
    @Schema(description = "购方税号")
    private String khsh;
	/**整张发票税额*/
	@Excel(name = "整张发票税额", width = 15)
    @Schema(description = "整张发票税额")
    private BigDecimal kpse;
	/**发票种类*/
	@Excel(name = "发票种类", width = 15)
    @Schema(description = "发票种类")
    private String fpzl;
	/**销方税号*/
	@Excel(name = "销方税号", width = 15)
    @Schema(description = "销方税号")
    private String xfsh;
	/**发票号码*/
	@Excel(name = "发票号码", width = 15)
    @Schema(description = "发票号码")
    private String fphm;
//    状态：0未使用，1使用中，2已报销
    private String status;
}
