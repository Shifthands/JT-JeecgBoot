package org.jeecg.modules.demo.util;

import com.aliyun.ocr_api20210707.models.RecognizeInvoiceResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class TicketUtil {
    private com.aliyun.ocr_api20210707.Client ocrclient;

    private com.aliyun.teautil.models.RuntimeOptions runtime;
    @PostConstruct
    public void init() throws Exception {
        this.ocrclient = createClient();
    }

    public static com.aliyun.ocr_api20210707.Client createClient() throws Exception {
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }

    public String getInvoiceByOCR(InputStream inputStream) throws Exception {
        com.aliyun.ocr_api20210707.models.RecognizeInvoiceRequest recognizeInvoiceRequest = new com.aliyun.ocr_api20210707.models.RecognizeInvoiceRequest()
                .setBody(inputStream);
        this.runtime = new com.aliyun.teautil.models.RuntimeOptions();
        // 复制代码运行请自行打印 API 的返回值
        RecognizeInvoiceResponse ocrrsp = ocrclient.recognizeInvoiceWithOptions(recognizeInvoiceRequest, runtime);
        return ocrrsp.getBody().getData();
    }
}
