package org.jeecg.modules.demo.util;

import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponse;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;
import me.zhyd.oauth.log.Log;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DingTalkUtil {
    public String getAccessToken(String appkey,String appsecret) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey(appkey);
        req.setAppsecret(appsecret);
        req.setHttpMethod("GET");
        OapiGettokenResponse rsp = client.execute(req);
        String access_token=rsp.getAccessToken();
        return access_token;
    }

    @PostConstruct
    public void init() throws Exception {
        this.DingtalkworkflowClient = createDingtalkworkflowClient();
        this.token=getAccessToken("dingy4ty1ij8uookoz5b", "6IdNwkz1u7wxf80uJJr6N73G5Yi0U_Wl9JHRlfHBKiMgTDIbJ1H_0mSSi_bBA0XQ");
    }
    private com.aliyun.dingtalkworkflow_1_0.Client DingtalkworkflowClient;
    private String token;
    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dingtalkworkflow_1_0.Client createDingtalkworkflowClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";

        return new com.aliyun.dingtalkworkflow_1_0.Client(config);
    }

    /**
     * 获取钉钉OA审核实例列表
     * @param ProcessCode
     * @param StartTime
     * @param EndTime
     * @param userid
     * @param statusList
     */
    public List<String> getDingtalkWorkflowInstanceIds(String ProcessCode, long StartTime, long EndTime, String userid, String statusList){
        com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsHeaders listProcessInstanceIdsHeaders = new com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsHeaders();
        listProcessInstanceIdsHeaders.xAcsDingtalkAccessToken = token;
        com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsRequest listProcessInstanceIdsRequest = new com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsRequest()
                .setProcessCode(ProcessCode)
                .setStartTime(StartTime)
                .setEndTime(EndTime)
                .setNextToken(0L)
                .setMaxResults(20L)
                .setStatuses(java.util.Arrays.asList(
                        statusList
                ));
        List<String> ids = new ArrayList<>();
        try {
                ListProcessInstanceIdsResponse rsp = DingtalkworkflowClient.listProcessInstanceIdsWithOptions(listProcessInstanceIdsRequest, listProcessInstanceIdsHeaders, new RuntimeOptions());
                String nextToken = rsp.getBody().getResult().getNextToken();
                ids.addAll(rsp.getBody().getResult().getList());
                while (nextToken!=null&&!nextToken.isEmpty()){
                    listProcessInstanceIdsRequest.setNextToken(Long.valueOf(nextToken));
                    ListProcessInstanceIdsResponse rsp2 = DingtalkworkflowClient.listProcessInstanceIdsWithOptions(listProcessInstanceIdsRequest, listProcessInstanceIdsHeaders, new RuntimeOptions());
                    nextToken=rsp2.getBody().getResult().getNextToken();
                    ids.addAll(rsp2.getBody().getResult().getList());
                }
                return ids;
            }  catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            Log.error(err.message);
            _err.printStackTrace();
            return null;
        }
    }

    public GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult getOAInstDetail(String InstanceId) {
        com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders getProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders();
        getProcessInstanceHeaders.xAcsDingtalkAccessToken = token;
        com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest getProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest()
                .setProcessInstanceId(InstanceId);
        try {
            GetProcessInstanceResponse rsp = DingtalkworkflowClient.getProcessInstanceWithOptions(getProcessInstanceRequest, getProcessInstanceHeaders, new RuntimeOptions());
            return rsp.getBody().getResult();
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                Log.error(err.message);
                _err.printStackTrace();
            }
            return null;
        }
    }


}
