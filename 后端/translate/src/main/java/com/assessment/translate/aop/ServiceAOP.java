package com.assessment.translate.aop;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Aspect
@Component
public class ServiceAOP {
    @Value("${max-request-number}")
    private String max;

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        rateLimiter = RateLimiter.create(Integer.parseInt(max) * 1.0);
    }

    @Pointcut("execution(* com.assessment.translate.controller..*(..))")
    public void service() {
    }

    @Around("service()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        if (rateLimiter.tryAcquire()) {
            return pjp.proceed();
        } else {
            JSONObject object = new JSONObject();
            object.put("error", "服务器繁忙，请休息一会再来");
            return object;
        }
    }
}
