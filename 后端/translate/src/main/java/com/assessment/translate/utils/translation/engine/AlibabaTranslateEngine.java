package com.assessment.translate.utils.translation.engine;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceRequest;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.assessment.translate.service.EngineService;
import com.assessment.translate.utils.EncryptionUtils;
import com.assessment.translate.utils.concurrent.TranslateThreadPool;
import com.assessment.translate.utils.translation.TranslateEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("alibaba")
public class AlibabaTranslateEngine extends TranslateEngine {
    private static final String NAME = "alibaba";

    @Value("${alibaba.url}")
    private String url;

    @Value("${alibaba.app-id}")
    private String accessKeyId;

    @Value("${alibaba.secret-key}")
    private String accessKeySecret;

    @Autowired
    private EngineService engineService;

    private DefaultProfile profile;
    private TranslateECommerceRequest request;

    @PostConstruct
    private void init() {
        setName(NAME);
        log = Logger.getLogger(AlibabaTranslateEngine.class);
        request = new TranslateECommerceRequest();
        profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    }

    @Override
    public boolean build(TranslateThreadPool translateThreadPool, String source, String target, String text) {
        //父类初始化
        setTranslateThreadPool(translateThreadPool);

        /*源语言简称*/
        String sourceAbb = engineService.findAbbreviation(NAME, source);
        /*目标语言简称*/
        String targetAbb = engineService.findAbbreviation(NAME, target);
        if (sourceAbb == null) {
            log.error("invalid source type");
            return false;
        }
        if (targetAbb == null) {
            log.error("invalid target type");
            return false;
        }
        request.setSourceLanguage(sourceAbb);
        request.setTargetLanguage(targetAbb);
        request.setScene("title");
        request.setFormatType("text");
        request.setSourceText(EncryptionUtils.utf8Decode(text));
        return true;
    }

    @Override
    public Object translateByPost() {
        return translate(MethodType.POST);
    }

    @Override
    public Object translateByGet() {
        return translate(MethodType.GET);
    }

    private Object translate(MethodType method) {
        JSONObject t = new JSONObject();
        t.put("translation", "");
        t.put("error", "unexpected error");
        JSONObject r = getTranslateThreadPool().submitTask(() -> {
            IAcsClient client = new DefaultAcsClient(profile);
            request.setSysMethod(method);
            log.info(request.getSysBodyParameters());
            JSONObject result = new JSONObject();
            try {
                TranslateECommerceResponse response = client.getAcsResponse(request);
                log.info(JSONObject.toJSON(response));
                if (!(response.getCode() == 200)) {
                    result.put("translation", "");
                    result.put("error", response.getMessage());
                } else {
                    result.put("translation", EncryptionUtils.utf8Decode(response.getData().getTranslated()));
                }
            } catch (ClientException e) {
                result.put("translation", "");
                result.put("error", e.toString());
            }
            return result;
        });
        return (r == null ? t : r);
    }

    /* 调用阿里巴巴翻译api时间戳格式一直显示不对，即使和官方文档上的格式一致,因此直接使用阿里的sdk来调用api*/
//    /**
//     * 请求的参数初始化
//     */
//    @Override
//    public boolean build(TranslateThreadPool translateThreadPool, String source, String target, String text) {
//        //父类初始化
//        setTranslateThreadPool(translateThreadPool);
//
//        //参数
//        getParameters().put("AccessKeyId", accessKeyId);
//        getParameters().put("Action", "Translate");
//        getParameters().put("Format", "JSON");
//        getParameters().put("FormatType", "text");
//        getParameters().put("RegionId", "cn-hangzhou");
//        getParameters().put("Scene", "general");
//        getParameters().put("SignatureMethod", "HMAC-SHA1");
//        getParameters().put("SignatureNonce", ParameterUtils.getUUIDWithSeparator());
//        getParameters().put("SignatureVersion", "1.0");
//        String sourceAbb = engineService.findAbbreviation(NAME, source);
//        if(sourceAbb == null){
//            log.error("invalid source type");
//            return false;
//        }else{
//            getParameters().put("SourceLanguage", EncryptionUtils.percentEncode(sourceAbb));
//        }
//        getParameters().put("SourceText", EncryptionUtils.percentEncode(text));
//        String targetAbb = engineService.findAbbreviation(NAME, target);
//        if(targetAbb == null){
//            log.error("invalid target type");
//            return false;
//        }else{
//            getParameters().put("TargetLanguage", EncryptionUtils.percentEncode(targetAbb));
//        }
//        getParameters().put("Timestamp", EncryptionUtils.percentEncode(ParameterUtils.getISO8601Timestamp()));
////        getParameters().put("Timestamp", EncryptionUtils.percentEncode("2020-06-08T14:02:12Z"));
//        getParameters().put("Version", "2018-10-12");
//        return true;
//    }
//
//    @Override
//    public String translateByPost() {
//        final String s = "POST&%2F&";
//        String signature = EncryptionUtils.toBase64String(EncryptionUtils.toHMACSHA1String(s + createSign(getParameters()), accessKeySecret));
//        getParameters().put("Signature", signature);
//        HttpPost post = ParameterUtils.buildPost(url, getParameters());
//        if (post != null) {
//            log.info(post.toString());
//            return translate(post);
//        } else {
//            log.info("post is null");
//            JSONObject result = new JSONObject();
//            result.put("translation", "");
//            result.put("error", "uri syntax or unsupported encoding");
//            return result.toString();
//        }
//    }
//
//    @Override
//    public String translateByGet() {
//        final String s = "GET&%2F&";
//        System.out.println(getParameters().get("Timestamp"));
//        String signature = EncryptionUtils.toBase64String(EncryptionUtils.toHMACSHA1String(s + createSign(getParameters()), accessKeySecret));
//        getParameters().put("Signature", signature);
//        HttpGet get = ParameterUtils.buildGet(url, getParameters());
//        if (get != null) {
//            log.info(get.toString());
//            return translate(get);
//        } else {
//            log.info("get is null");
//            JSONObject result = new JSONObject();
//            result.put("translation", "");
//            result.put("error", "uri syntax");
//            return result.toString();
//        }
//    }
//
//    private String createSign(HashMap<String, String> map){
//        Set<String> entrySet = getParameters().keySet();
//        List<String> list = new ArrayList<String>(entrySet);
//        Collections.sort(list);
//        StringBuilder stringBuilder = new StringBuilder();
//        for(String l : list){
//            stringBuilder.append(l).append("=").append(map.get(l)).append("&");
//        }
//        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
//        System.out.println(stringBuilder);
//        System.out.println(EncryptionUtils.percentEncode(stringBuilder.toString()));
//        return EncryptionUtils.percentEncode(stringBuilder.toString());
//    }
//
//    private <T extends HttpRequestBase> String translate(T method) {
//        JSONObject result = new JSONObject();
//        String body = getTranslateThreadPool().translate(method);
//        System.out.println(body);
//        JSONObject o = JSONObject.parseObject(body);
//        if (o.get("error_code") != null) {
//            result.put("translation", "");
//            result.put("error", o.getString("error_msg"));
//        } else {
//            result.put("translation", EncryptionUtils.utf8Decode(o.getJSONArray("trans_result").getJSONObject(0).getString("dst")));
//        }
//        return result.toString();
//    }
}
