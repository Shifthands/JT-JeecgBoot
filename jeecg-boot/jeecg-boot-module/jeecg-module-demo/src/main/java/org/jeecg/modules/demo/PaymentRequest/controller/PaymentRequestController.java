package org.jeecg.modules.demo.PaymentRequest.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.dto.message.BusTemplateMessageDTO;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.jeecg.common.api.dto.message.TemplateMessageDTO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.MessageTypeEnum;
import org.jeecg.common.constant.enums.SysAnnmentTypeEnum;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.PaymentRequest.entity.PaymentRequest;
import org.jeecg.modules.demo.PaymentRequest.service.IPaymentRequestService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 付款申请
 * @Author: jeecg-boot
 * @Date:   2025-05-20
 * @Version: V1.0
 */
@Tag(name="付款申请")
@RestController
@RequestMapping("/PaymentRequest/paymentRequest")
@Slf4j
public class PaymentRequestController extends JeecgController<PaymentRequest, IPaymentRequestService> {
	@Autowired
	private IPaymentRequestService paymentRequestService;
	
	/**
	 * 分页列表查询
	 *
	 * @param paymentRequest
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "付款申请-分页列表查询")
	@Operation(summary="付款申请-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent="PaymentRequest/PaymentRequestList")
	public Result<IPage<PaymentRequest>> queryPageList(PaymentRequest paymentRequest,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<PaymentRequest> queryWrapper = QueryGenerator.initQueryWrapper(paymentRequest, req.getParameterMap());
		Page<PaymentRequest> page = new Page<PaymentRequest>(pageNo, pageSize);
		IPage<PaymentRequest> pageList = paymentRequestService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param paymentRequest
	 * @return
	 */
	@AutoLog(value = "付款申请-添加")
	@Operation(summary="付款申请-添加")
	@RequiresPermissions("PaymentRequest:payment_request:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PaymentRequest paymentRequest) {
		paymentRequestService.save(paymentRequest);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param paymentRequest
	 * @return
	 */
	@AutoLog(value = "付款申请-编辑")
	@Operation(summary="付款申请-编辑")
	@RequiresPermissions("PaymentRequest:payment_request:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PaymentRequest paymentRequest) {
		paymentRequestService.updateById(paymentRequest);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "付款申请-通过id删除")
	@Operation(summary="付款申请-通过id删除")
	@RequiresPermissions("PaymentRequest:payment_request:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		paymentRequestService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "付款申请-批量删除")
	@Operation(summary="付款申请-批量删除")
	@RequiresPermissions("PaymentRequest:payment_request:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.paymentRequestService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "付款申请-通过id查询")
	@Operation(summary="付款申请-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PaymentRequest> queryById(@RequestParam(name="id",required=true) String id) {
		PaymentRequest paymentRequest = paymentRequestService.getById(id);
		if(paymentRequest==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(paymentRequest);
	}

	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	 @GetMapping(value = "/sendMsg")
	 public Result<String> sendMsg(@RequestParam(name="ids",required=true) String ids) {
		 String[] split = ids.split(",");
		 StringBuilder result=new StringBuilder();
		 for (String id : split) {
			 PaymentRequest byId = paymentRequestService.getById(id);
			 result.append(byId.getOanumber()).append(",").append("\n\n");
		 }
		 MessageDTO md = new MessageDTO();
		 md.setToAll(false);
		 md.setTitle("消息发送测试");
		 md.setTemplateCode("ticket");
		 md.setToUser("17336131948");
		 md.setType(MessageTypeEnum.DD.getType());

		 Map<String, Object> data = new HashMap<>();
		 data.put("projectname", "付款申请");
		 data.put("oanumber", result.toString().substring(0,result.length()-2));
		 md.setData(data);

		 sysBaseAPI.sendTemplateMessage(md);
		 return Result.OK("发送成功");
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param paymentRequest
    */
    @RequiresPermissions("PaymentRequest:payment_request:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PaymentRequest paymentRequest) {
        return super.exportXls(request, paymentRequest, PaymentRequest.class, "付款申请");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("PaymentRequest:payment_request:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PaymentRequest.class);
    }

}
