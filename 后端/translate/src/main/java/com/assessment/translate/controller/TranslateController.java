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

    @RequestMapping(value = "/translate", method = RequestMethod.POST)
    public Object translate(@RequestBody Map<String, String> map) {
        return translationService.translate(map);
    }

    @RequestMapping(value = "/translate", method = RequestMethod.GET)
    public Object translate1(@RequestParam Map<String, String> map) {
        return translationService.translate(map);

    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public Object submit(@RequestBody Map<String, String> map) {
        JSONObject result = new JSONObject();
        result.put("result", translationService.submit(map) ? "success" : "failed");
        return result;
    }

    @RequestMapping("/exportAsExcel")
    public Object exportAsExcel(@RequestBody Map<String, String> map) {
        return translationService.exportAsExcel(map);
    }
}
