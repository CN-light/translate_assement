package com.assessment.translate.utils.translation.engine;

import com.alibaba.fastjson.JSONObject;
import com.assessment.translate.service.EngineService;
import com.assessment.translate.utils.EncryptionUtils;
import com.assessment.translate.utils.ParameterUtils;
import com.assessment.translate.utils.concurrent.TranslateThreadPool;
import com.assessment.translate.utils.translation.TranslateEngine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component("tencent")
public class TencentTranslateEngine extends TranslateEngine {
    private static final String NAME = "tencent";

    @Value("${tencent.url}")
    private String url;

    @Value("${tencent.app-id}")
    private String appId;

    @Value("${tencent.secret-key}")
    private String accessKeySecret;

    @Value("${tencent.project-id}")
    private String projectId;

    @Value("${tencent.end-point}")
    private String endPoint;

    @Autowired
    private EngineService engineService;

    @PostConstruct
    private void init() {
        setName(NAME);
        log = Logger.getLogger(TencentTranslateEngine.class);
    }

    @Override
    public boolean build(TranslateThreadPool translateThreadPool, String source, String target, String text) {
        //父类初始化
        setTranslateThreadPool(translateThreadPool);

        String nonce = Integer.toString(new Random().nextInt(java.lang.Integer.MAX_VALUE));
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        /*源语言简称*/
        String sourceAbb = engineService.findAbbreviation(NAME, source);
        /*目标语言简称*/
        String targetAbb = engineService.findAbbreviation(NAME, target);
        if (!timestamp.matches("[0-9]+")) {
            log.error("Timestamp or Nonce must be integer");
            return false;
        }
        if (!projectId.matches("[0-9]+")) {
            log.error("projectId must be integer");
            return false;
        }
        if (sourceAbb == null) {
            log.error("invalid source type");
            return false;
        }
        if (targetAbb == null) {
            log.error("invalid target type");
            return false;
        }
        addParameters("Action", "TextTranslate");
        addParameters("Region", "ap-shanghai");
        addParameters("Timestamp", timestamp);
        addParameters("Nonce", nonce);
        addParameters("SecretId", appId);
        addParameters("Version", "2018-03-21");
        addParameters("SignatureMethod", "HmacSHA1");
        addParameters("ProjectId", projectId);
        addParameters("Source", sourceAbb);
        addParameters("Target", targetAbb);
        addParameters("SourceText", text);
        return true;
    }

    @Override
    public Object translateByPost() {
        String signature = createSign(getParameters(), "POST");
        addParameters("Signature", signature);
        HttpPost post = ParameterUtils.buildPost(url, getParameters());
        if (post != null) {
            post.setHeader("content-type", "application/x-www-form-urlencoded");
            log.info(post.toString());
            return translate(post);
        } else {
            log.info("post is null");
            JSONObject result = new JSONObject();
            result.put("translation", "");
            result.put("error", "uri syntax or unsupported encoding");
            return result;
        }
    }

    @Override
    public Object translateByGet() {
        String signature = createSign(getParameters(), "GET");
        addParameters("Signature", signature);
        HttpGet get = ParameterUtils.buildGet(url, getParameters());
        if (get != null) {
            log.info(get.toString());
            return translate(get);
        } else {
            log.info("get is null");
            JSONObject result = new JSONObject();
            result.put("translation", "");
            result.put("error", "uri syntax");
            return result;
        }
    }

    private <T extends HttpRequestBase> Object translate(T method) {
        JSONObject result = new JSONObject();
        //加入线程池执行
        JSONObject body = getTranslateThreadPool().translate(method);
        if (body == null) {
            result.put("translation", "");
            result.put("error", "unexpected error");
        } else {
            log.info(body);
            JSONObject o = body.getJSONObject("Response");
            if (o.get("Error") != null) {
                result.put("translation", "");
                result.put("error", o.getJSONObject("Error").getString("Code") + ":" + o.getJSONObject("Error").getString("Message"));
            } else {
                result.put("translation", EncryptionUtils.utf8Decode(o.getString("TargetText")));
            }
        }
        return result;
    }

    private String createSign(HashMap<String, String> map, String method) {
        Set<String> entrySet = getParameters().keySet();
        List<String> list = new ArrayList<String>(entrySet);
        Collections.sort(list);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method)
                .append(endPoint)
                .append("/")
                .append("?");
        for (String l : list) {
            if (!"Signature".equals(l)) {
                stringBuilder.append(l).append("=").append(map.get(l)).append("&");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return EncryptionUtils.toHMACSHA1String(stringBuilder.toString(), accessKeySecret);
    }
}
