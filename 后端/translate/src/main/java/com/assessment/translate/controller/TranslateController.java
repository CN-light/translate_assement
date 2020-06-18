package com.assessment.translate.controller;

import com.alibaba.fastjson.JSONObject;
import com.assessment.translate.service.TranslationService;
import com.assessment.translate.utils.ParameterUtils;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/translate")
public class TranslateController {
    @Autowired
    private TranslationService translationService;

    @Value("${max-request-number}")
    private String max;

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init(){
        rateLimiter = RateLimiter.create(Integer.parseInt(max) * 1.0);
    }

    @RequestMapping(value = "/translate", method = RequestMethod.POST)
    public String translate(@RequestBody Map<String, String> map) {
        String result;
        if(rateLimiter.tryAcquire()){
            result = translationService.translate(map);
        } else{
            JSONObject object = new JSONObject();
            object.put("error", "服务器繁忙，请休息一会再来");
            result = object.toString();
        }
        return result;
    }

    @RequestMapping(value = "/translate", method = RequestMethod.GET)
    public String translate1(@RequestParam Map<String, String> map) {
        String result;
        if(rateLimiter.tryAcquire()){
            result = translationService.translate(map);
        } else{
            JSONObject object = new JSONObject();
            object.put("error", "服务器繁忙，请休息一会再来");
            result = object.toString();
        }
        return result;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(@RequestBody Map<String, String> map) {
        JSONObject result = new JSONObject();
        if(rateLimiter.tryAcquire()){
            result.put("result", translationService.submit(map) ? "success" : "failed");
        } else {
            result.put("error", "服务器繁忙，请休息一会再来");
        }
        return result.toJSONString();
    }

    @RequestMapping("/exportAsExcel")
    public String exportAsExcel(@RequestBody Map<String, String> map) {
        String result;
        if(rateLimiter.tryAcquire()){
            result = translationService.exportAsExcel(map);
        }else{
            JSONObject object = new JSONObject();
            object.put("error", "服务器繁忙，请休息一会再来");
            result = object.toString();
        }
        return result;
    }
}
