package org.jeecg.modules.demo.PaymentTicket.controller;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryRuleEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.DPT.entity.Ticket;
import org.jeecg.modules.demo.DPT.service.ITicketService;
import org.jeecg.modules.demo.PaymentRequest.entity.PaymentRequest;
import org.jeecg.modules.demo.PaymentRequest.service.IPaymentRequestService;
import org.jeecg.modules.demo.util.TicketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicketDetail;
import org.jeecg.modules.demo.PaymentTicket.entity.PaymentTicket;
import org.jeecg.modules.demo.PaymentTicket.service.IPaymentTicketService;
import org.jeecg.modules.demo.PaymentTicket.service.IPaymentTicketDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
/**
 * @Description: 付款发票
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Tag(name="付款发票")
@RestController
@RequestMapping("/PaymentTicket/paymentTicket")
@Slf4j
public class PaymentTicketController extends JeecgController<PaymentTicket, IPaymentTicketService> {

	@Autowired
	private IPaymentTicketService paymentTicketService;

	@Autowired
	private IPaymentTicketDetailService paymentTicketDetailService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param paymentTicket
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "付款发票-分页列表查询")
	@Operation(summary="付款发票-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent="PaymentTicket/paymentTicketList")
	public Result<IPage<PaymentTicket>> queryPageList(PaymentTicket paymentTicket,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		QueryWrapper<PaymentTicket> queryWrapper = QueryGenerator.initQueryWrapper(paymentTicket, req.getParameterMap());
		Page<PaymentTicket> page = new Page<PaymentTicket>(pageNo, pageSize);
		IPage<PaymentTicket> pageList = paymentTicketService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	@Value(value = "${jeecg.path.upload}")
	private String uploadpath;
	@Autowired
	TicketUtil aliyunOCR;
	@Autowired
	IPaymentRequestService requestService;
	@Autowired
	private ITicketService ticketService;
	/**
	 * 更新发票实例id到项目申请列表中，同时识别发票内容，增加到子表中
	 * @param paymentTicket
	 */
	public void updateTicketInstIdForReq(PaymentTicket paymentTicket) throws Exception {
		String ticketid = paymentTicket.getId();
		String reqIds = paymentTicket.getPamentlist();
		String[] split = reqIds.split(",");
		for (String reqid : split) {
			PaymentRequest requestServiceById = requestService.getById(reqid);
			requestServiceById.setTicketinstid(ticketid);
			requestServiceById.setTicketstatus("已上传");
			requestService.updateById(requestServiceById);
		}

		QueryWrapper<PaymentTicketDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("mainid",paymentTicket.getId());
		paymentTicketDetailService.remove(wrapper);

		String ticket = paymentTicket.getTicket();
		if(ticket==null||ticket.isEmpty())return;
		String[] ticketPaths = ticket.split(",");
		String checkstatus="失败";

		Integer successCount=0;
		for (String ticketPath : ticketPaths) {
			String filePath = uploadpath + File.separator + ticketPath;
			File file = new File(filePath);
			InputStream inputStream = new FileInputStream(file);
			String invoiceByOCR = aliyunOCR.getInvoiceByOCR(inputStream);
			inputStream.close();
			String invoiceNumber = JSONObject.parseObject(invoiceByOCR).getJSONObject("data").getString("invoiceNumber");
			log.info("识别发票成功("+invoiceNumber+")");
			Ticket fphm = ticketService.query().eq("fphm", invoiceNumber).one();

			PaymentTicketDetail paymentTicketDetail=new PaymentTicketDetail();
			paymentTicketDetail.setInvoicenumber(invoiceNumber);
			paymentTicketDetail.setMainid(paymentTicket.getId());
			if(fphm!=null){
				String status = fphm.getStatus();
				String statusTip=status.equals("2")?"重复上传，请检查":"已报销";
				paymentTicketDetail.setCheckstatus(statusTip);
				if(status.equals("0")){
					successCount++;
					fphm.setStatus("2");
					ticketService.updateById(fphm);
				}
			}else {
				paymentTicketDetail.setCheckstatus("不存在发票池");
			}
			paymentTicketDetailService.save(paymentTicketDetail);
		}
		if(successCount>0){
			if(successCount== ticketPaths.length){
				checkstatus="成功";
			}else {
				checkstatus="部分成功";
			}
		}
		paymentTicket.setTicketcheck(checkstatus);
		paymentTicketService.updateById(paymentTicket);
	}
	/**
	 *   添加
	 * @param paymentTicket
	 * @return
	 */
	@AutoLog(value = "付款发票-添加")
	@Operation(summary="付款发票-添加")
	@RequiresPermissions("PaymentTicket:payment_ticket:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PaymentTicket paymentTicket) throws Exception {
		if(paymentTicket.getPamentlist().isEmpty()||paymentTicket.getTicket().isEmpty()){
			return Result.error("请上传发票并关联付款申请");
		}
		paymentTicketService.save(paymentTicket);
		updateTicketInstIdForReq(paymentTicket);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 * @param paymentTicket
	 * @return
	 */
	@AutoLog(value = "付款发票-编辑")
	@Operation(summary="付款发票-编辑")
	@RequiresPermissions("PaymentTicket:payment_ticket:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PaymentTicket paymentTicket) throws Exception {
		if(paymentTicket.getPamentlist().isEmpty()||paymentTicket.getTicket().isEmpty()){
			return Result.error("请上传发票并关联付款申请");
		}
		paymentTicketService.updateById(paymentTicket);
		updateTicketInstIdForReq(paymentTicket);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "付款发票-通过id删除")
	@Operation(summary="付款发票-通过id删除")
	@RequiresPermissions("PaymentTicket:payment_ticket:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		paymentTicketService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "付款发票-批量删除")
	@Operation(summary="付款发票-批量删除")
	@RequiresPermissions("PaymentTicket:payment_ticket:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.paymentTicketService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 导出
	 * @return
	 */
	@RequiresPermissions("PaymentTicket:payment_ticket:exportXls")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, PaymentTicket paymentTicket) {
		return super.exportXls(request, paymentTicket, PaymentTicket.class, "付款发票");
	}

	/**
	 * 导入
	 * @return
	 */
	@RequiresPermissions("PaymentTicket:payment_ticket:importExcel")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, PaymentTicket.class);
	}
	/*---------------------------------主表处理-end-------------------------------------*/


	/*--------------------------------子表处理-发票子表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "发票子表-通过主表ID查询")
	@Operation(summary="发票子表-通过主表ID查询")
	@GetMapping(value = "/listPaymentTicketDetailByMainId")
	public Result<IPage<PaymentTicketDetail>> listPaymentTicketDetailByMainId(PaymentTicketDetail paymentTicketDetail,
																			  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																			  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
																			  HttpServletRequest req) {
		QueryWrapper<PaymentTicketDetail> queryWrapper = QueryGenerator.initQueryWrapper(paymentTicketDetail, req.getParameterMap());
		Page<PaymentTicketDetail> page = new Page<PaymentTicketDetail>(pageNo, pageSize);
		IPage<PaymentTicketDetail> pageList = paymentTicketDetailService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 * @param paymentTicketDetail
	 * @return
	 */
	@AutoLog(value = "发票子表-添加")
	@Operation(summary="发票子表-添加")
	@PostMapping(value = "/addPaymentTicketDetail")
	public Result<String> addPaymentTicketDetail(@RequestBody PaymentTicketDetail paymentTicketDetail) {
		paymentTicketDetailService.save(paymentTicketDetail);
		return Result.OK("添加成功！");
	}

	/**
	 * 编辑
	 * @param paymentTicketDetail
	 * @return
	 */
	@AutoLog(value = "发票子表-编辑")
	@Operation(summary="发票子表-编辑")
	@RequestMapping(value = "/editPaymentTicketDetail", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editPaymentTicketDetail(@RequestBody PaymentTicketDetail paymentTicketDetail) {
		paymentTicketDetailService.updateById(paymentTicketDetail);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发票子表-通过id删除")
	@Operation(summary="发票子表-通过id删除")
	@DeleteMapping(value = "/deletePaymentTicketDetail")
	public Result<String> deletePaymentTicketDetail(@RequestParam(name="id",required=true) String id) {
		paymentTicketDetailService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "发票子表-批量删除")
	@Operation(summary="发票子表-批量删除")
	@DeleteMapping(value = "/deleteBatchPaymentTicketDetail")
	public Result<String> deleteBatchPaymentTicketDetail(@RequestParam(name="ids",required=true) String ids) {
		this.paymentTicketDetailService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 导出
	 * @return
	 */
	@RequestMapping(value = "/exportPaymentTicketDetail")
	public ModelAndView exportPaymentTicketDetail(HttpServletRequest request, PaymentTicketDetail paymentTicketDetail) {
		// Step.1 组装查询条件
		QueryWrapper<PaymentTicketDetail> queryWrapper = QueryGenerator.initQueryWrapper(paymentTicketDetail, request.getParameterMap());
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		// Step.2 获取导出数据
		List<PaymentTicketDetail> pageList = paymentTicketDetailService.list(queryWrapper);
		List<PaymentTicketDetail> exportList = null;

		// 过滤选中数据
		String selections = request.getParameter("selections");
		if (oConvertUtils.isNotEmpty(selections)) {
			List<String> selectionList = Arrays.asList(selections.split(","));
			exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		} else {
			exportList = pageList;
		}

		// Step.3 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		//此处设置的filename无效,前端会重更新设置一下
		mv.addObject(NormalExcelConstants.FILE_NAME, "发票子表");
		mv.addObject(NormalExcelConstants.CLASS, PaymentTicketDetail.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发票子表报表", "导出人:" + sysUser.getRealname(), "发票子表"));
		mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		return mv;
	}

	/**
	 * 导入
	 * @return
	 */
	@RequestMapping(value = "/importPaymentTicketDetail/{mainId}")
	public Result<?> importPaymentTicketDetail(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<PaymentTicketDetail> list = ExcelImportUtil.importExcel(file.getInputStream(), PaymentTicketDetail.class, params);
				for (PaymentTicketDetail temp : list) {
					temp.setMainid(mainId);
				}
				long start = System.currentTimeMillis();
				paymentTicketDetailService.saveBatch(list);
				log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				return Result.OK("文件导入成功！数据行数：" + list.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件导入失败！");
	}

	/*--------------------------------子表处理-发票子表-end----------------------------------------------*/




}
