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

@Component("baidu")
public class BaiduTranslateEngine extends TranslateEngine {
    private static final String NAME = "baidu";

    @Value("${baidu.url}")
    private String url;

    @Value("${baidu.app-id}")
    private String appId;

    @Value("${baidu.secret-key}")
    private String accessKeySecret;

    @Autowired
    private EngineService engineService;

    @PostConstruct
    private void init() {
        setName(NAME);
        log = Logger.getLogger(BaiduTranslateEngine.class);
    }

    @Override
    public boolean build(TranslateThreadPool translateThreadPool, String source, String target, String text) {
        //父类初始化
        setTranslateThreadPool(translateThreadPool);

        //参数

        /*随机数*/
        String salt = String.valueOf(System.currentTimeMillis());
        /*源语言简称*/
        String sourceAbb = engineService.findAbbreviation(NAME, source);
        /*目标语言简称*/
        String targetAbb = engineService.findAbbreviation(NAME, target);
        /*签名*/
        String sign = EncryptionUtils.toMD5String(appId + text + salt + accessKeySecret);
        if (sourceAbb == null) {
            log.error("invalid source type");
            return false;
        }
        if (targetAbb == null) {
            log.error("invalid target type");
            return false;
        }
        if (sign == null) {
            log.error("invalid sign");
            return false;
        }
        addParameters("from", sourceAbb);
        addParameters("to", targetAbb);
        addParameters("q", text);
        addParameters("appid", appId);
        addParameters("salt", salt);
        addParameters("sign", sign);
        return true;
    }

    @Override
    public Object translateByPost() {
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
            if (body.get("error_code") != null) {
                result.put("translation", "");
                result.put("error", body.getString("error_msg"));
            } else {
                result.put("translation", EncryptionUtils.utf8Decode(body.getJSONArray("trans_result").getJSONObject(0).getString("dst")));
            }
        }
        return result;
    }
}
