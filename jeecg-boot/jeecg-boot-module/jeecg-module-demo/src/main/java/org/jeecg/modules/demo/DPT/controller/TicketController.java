package org.jeecg.modules.demo.DPT.controller;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkstorage_1_0.Client;
import com.aliyun.dingtalkstorage_1_0.models.*;
import com.aliyun.ocr_api20210707.models.RecognizeInvoiceResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.DPT.entity.Ticket;
import org.jeecg.modules.demo.DPT.service.ITicketService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.util.DingTalkUtil;
import org.jeecg.modules.demo.util.TicketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 进项发票
 * @Author: jeecg-boot
 * @Date:   2025-05-14
 * @Version: V1.0
 */
@Tag(name="进项发票")
@RestController
@RequestMapping("/JTSJ/ticket")
@Slf4j
public class TicketController extends JeecgController<Ticket, ITicketService> {
	@Autowired
	private ITicketService ticketService;
	private CloseableHttpClient httpClient;
	private String  token;
	/**
	 * 分页列表查询
	 *
	 * @param ticket
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "进项发票-分页列表查询")
	@Operation(summary="进项发票-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Ticket>> queryPageList(Ticket ticket,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<Ticket> queryWrapper = QueryGenerator.initQueryWrapper(ticket, req.getParameterMap());
		Page<Ticket> page = new Page<Ticket>(pageNo, pageSize);
		IPage<Ticket> pageList = ticketService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 根据审批结果更新发票状态
	  * @param parm
	  * @return
	  */
	 @PostMapping(value = "/updateTicketStatus")
	 public Result<String> updateTicketStatus(@RequestBody JSONObject parm){
		 String auditResult = parm.getString("AuditResult");
		 String checkResult = parm.getString("checkResult");
		 String updateStatusString;
		 if(auditResult.equals("同意")){
			 updateStatusString="2";
		 }else if(auditResult.equals("发起")){
			 updateStatusString="1";
		 }else {
			 updateStatusString="0";
		 }
		 String[] tickets = checkResult.split(";");
		 String regex = "发票号码：(\\d+)";
		 Pattern pattern = Pattern.compile(regex);
		 for (String ticket : tickets) {
			 String invoiceNumber = extractInvoiceNumber(ticket, pattern);
			 if(!invoiceNumber.isEmpty()){
				 Ticket fphm = ticketService.query().eq("fphm", invoiceNumber).one();
				 if(fphm!=null){
				 	fphm.setStatus(updateStatusString);
					 ticketService.updateById(fphm);
				 }
			 }
		 }
		 log.info("审批流"+auditResult+":更新成功,发票内容："+checkResult  );
		 return Result.OK("审批流"+auditResult+":更新成功,发票内容："+checkResult   );
	 }

	 public static String extractInvoiceNumber(String input, Pattern pattern) {
		 Matcher matcher = pattern.matcher(input);
		 if (matcher.find()) {
			 return matcher.group(1);
		 } else {
			 return "";
		 }
	 }
	 @Autowired
	 TicketUtil aliyunOCR;
	 private String processAtte(Object atte) throws Exception {
		 String result = null;
		 try {

			 String spaceId = ((JSONObject) atte).getString("spaceId");
			 String fileId = ((JSONObject) atte).getString("fileId");
			 String fileName = ((JSONObject) atte).getString("fileName");
//			 授权
			 AddPermissionResponse addPermissionResponse = storageClient.addPermissionWithOptions(spaceId, fileId, addPermissionRequest, addPermissionHeaders, new RuntimeOptions());
			 if (!addPermissionResponse.getBody().success){
				 log.error("附件:"+fileName+" 添加权限失败");
			 }
//			 获取下载链接
			 GetFileDownloadInfoResponse attchmentDownloadInfo = getAttchmentDownloadInfo(spaceId, fileId, token, storageClient, getFileDownloadInfoHeaders, getFileDownloadInfoRequest);
			 if(attchmentDownloadInfo!=null){
				 GetFileDownloadInfoResponseBody.GetFileDownloadInfoResponseBodyHeaderSignatureInfo headerSignatureInfo = attchmentDownloadInfo.getBody().getHeaderSignatureInfo();
				 String url = headerSignatureInfo.getResourceUrls().get(0);
				 Map<String, String> headers = headerSignatureInfo.getHeaders();
				 HttpURLConnection connection = getFileInputStreamWithoutClosing(url, headers);
				 try {
					 InputStream inputStream = connection.getInputStream();
					 String data = aliyunOCR.getInvoiceByOCR(inputStream);
					 String invoiceNumber = JSONObject.parseObject(data).getJSONObject("data").getString("invoiceNumber");
//					 String invoiceCode = JSONObject.parseObject(data).getJSONObject("data").getString("invoiceCode");
//					 if(!invoiceCode.isEmpty()){
//						 invoiceNumber=invoiceCode+invoiceNumber;
//					 }
					 Ticket fphm = ticketService.query().eq("fphm", invoiceNumber).one();
					 if(fphm!=null){
						 String status = fphm.getStatus();
						 String statusTip=status.equals("0")?"校验通过":status.equals("1")?"正在使用，请检查！":"已报销";
//						 if(status.equals("0")){
//							 fphm.setStatus("1");
//							 ticketService.updateById(fphm);
//						 }
						 result= (fileName+"  (发票号码："+invoiceNumber+") "+statusTip+"");
					 }else {
						 result= (fileName+"  (发票号码："+invoiceNumber+") 校验失败，请检查！");
					 }
				 } catch (Exception _error) {
					 TeaException error = new TeaException(_error.getMessage(), _error);
					 // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
					 // 错误 message
					 log.error(error.getMessage());
					 com.aliyun.teautil.Common.assertAsString(error.message);
					 result=(fileName+" 识别失败！");
				 } finally {
					 connection.disconnect();
				 }
			 }
		 } catch (InterruptedException e) {
			 Thread.currentThread().interrupt();
		 }
		 return result;
	 }
	 private Client storageClient;
	 private com.aliyun.dingtalkstorage_1_0.models.AddPermissionHeaders addPermissionHeaders;
	 private com.aliyun.dingtalkstorage_1_0.models.AddPermissionRequest addPermissionRequest;
	 private com.aliyun.dingtalkstorage_1_0.models.GetFileDownloadInfoHeaders getFileDownloadInfoHeaders;
	 private com.aliyun.dingtalkstorage_1_0.models.GetFileDownloadInfoRequest getFileDownloadInfoRequest;
	 @Autowired
	 DingTalkUtil util;
	 /**
	  * 获取发票校验信息并上锁
	  * @param ticket
	  * @return
	  * @throws Exception
	  */
	 @PostMapping(value = "/fromOA")
	 public Result<String> fromOA(@RequestBody JSONObject ticket) throws Exception {
//		获取附件列表
		 String atteString = ticket.getString("atte");
		 JSONArray attes = JSONArray.parseArray(atteString);
		 if(attes==null){
		 	return Result.ok("");
		 }
		 String token = util.getAccessToken("dingy4ty1ij8uookoz5b", "6IdNwkz1u7wxf80uJJr6N73G5Yi0U_Wl9JHRlfHBKiMgTDIbJ1H_0mSSi_bBA0XQ");
		 String unionId="zq3RFJ5p3yURjajCC1OplwiEiE";
		 this.storageClient = createStorageClient();

		 this.addPermissionHeaders = new com.aliyun.dingtalkstorage_1_0.models.AddPermissionHeaders();
		 addPermissionHeaders.xAcsDingtalkAccessToken=token;
		 com.aliyun.dingtalkstorage_1_0.models.AddPermissionRequest.AddPermissionRequestMembers members0 = new com.aliyun.dingtalkstorage_1_0.models.AddPermissionRequest.AddPermissionRequestMembers()
				 .setType("USER")
				 .setId(unionId);
		 this.addPermissionRequest = new com.aliyun.dingtalkstorage_1_0.models.AddPermissionRequest()
				 .setUnionId(unionId)
				 .setRoleId("MANAGER")
				 .setMembers(Arrays.asList(
						 members0
				 ));
		 this.getFileDownloadInfoHeaders = new com.aliyun.dingtalkstorage_1_0.models.GetFileDownloadInfoHeaders();
		 this.getFileDownloadInfoHeaders.xAcsDingtalkAccessToken =token;
		 this.getFileDownloadInfoRequest = new com.aliyun.dingtalkstorage_1_0.models.GetFileDownloadInfoRequest()
				 .setUnionId(unionId);
		 ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
		 ExecutorService executor = Executors.newFixedThreadPool(4);

		 for (Object atte : attes) {
			 executor.submit(() -> {
				 String processed = null;
				 try {
					 processed = processAtte(atte);
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
				 queue.add(processed); // 线程安全
			 });
		 }

		 executor.shutdown();
		 executor.awaitTermination(1, TimeUnit.MINUTES);

		 StringBuilder result = new StringBuilder();
		 queue.forEach(str -> result.append(str).append(";").append("\n"));
		 log.info("Final Result:\n" + result);
		 return Result.OK(result.toString());
	 }
	 public HttpURLConnection  getFileInputStreamWithoutClosing(String url, Map<String, String> headers) throws IOException {
		 HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

		 // 禁用自动重定向（可选）
		 connection.setInstanceFollowRedirects(false);

		 // 设置请求方法
		 connection.setRequestMethod("GET");

		 // 添加头部信息
		 if (headers != null) {
			 for (Map.Entry<String, String> entry : headers.entrySet()) {
				 connection.setRequestProperty(entry.getKey(), entry.getValue());
			 }
		 }

		 // 获取响应码
		 int responseCode = connection.getResponseCode();

		 if (responseCode == HttpURLConnection.HTTP_OK) {
			 return connection; // 注意：调用者必须手动关闭 InputStream
		 } else {
			 connection.disconnect(); // 失败时关闭连接
			 throw new IOException("Failed to download file. HTTP error code: " + responseCode);
		 }
	 }
	 /**
	  * 获取附件下载信息
	  * @param AccessToken
	  * @return
	  */
	 public GetFileDownloadInfoResponse getAttchmentDownloadInfo(String spaceId, String fileId, String AccessToken, Client storageClient, GetFileDownloadInfoHeaders getFileDownloadInfoHeaders,GetFileDownloadInfoRequest getFileDownloadInfoRequest) throws Exception {
		 GetFileDownloadInfoResponse response = null;
		 try {
			  response = storageClient.getFileDownloadInfoWithOptions(spaceId, fileId, getFileDownloadInfoRequest, getFileDownloadInfoHeaders, new RuntimeOptions());
		 } catch (TeaException err) {
			 if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
				 // err 中含有 code 和 message 属性，可帮助开发定位问题
			 }

		 } catch (Exception _err) {
			 TeaException err = new TeaException(_err.getMessage(), _err);
			 if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
				 // err 中含有 code 和 message 属性，可帮助开发定位问题
			 }

		 }
		 return response;
	 }

	 public static com.aliyun.dingtalkstorage_1_0.Client createStorageClient() throws Exception {
		 com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
		 config.protocol = "https";
		 config.regionId = "central";
		 return new com.aliyun.dingtalkstorage_1_0.Client(config);
	 }
	/**
	 *   添加
	 *
	 * @param ticket
	 * @return
	 */
	@AutoLog(value = "进项发票-添加")
	@Operation(summary="进项发票-添加")
	@RequiresPermissions("JTSJ:ticket:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Ticket ticket) {
		ticketService.save(ticket);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ticket
	 * @return
	 */
	@AutoLog(value = "进项发票-编辑")
	@Operation(summary="进项发票-编辑")
	@RequiresPermissions("JTSJ:ticket:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Ticket ticket) {
		ticketService.updateById(ticket);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "进项发票-通过id删除")
	@Operation(summary="进项发票-通过id删除")
	@RequiresPermissions("JTSJ:ticket:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ticketService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "进项发票-批量删除")
	@Operation(summary="进项发票-批量删除")
	@RequiresPermissions("JTSJ:ticket:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ticketService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "进项发票-通过id查询")
	@Operation(summary="进项发票-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Ticket> queryById(@RequestParam(name="id",required=true) String id) {
		Ticket ticket = ticketService.getById(id);
		if(ticket==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(ticket);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ticket
    */
    @RequiresPermissions("JTSJ:ticket:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Ticket ticket) {
        return super.exportXls(request, ticket, Ticket.class, "进项发票");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("JTSJ:ticket:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Ticket.class);
    }

}
