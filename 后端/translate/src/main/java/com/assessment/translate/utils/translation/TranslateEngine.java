package com.assessment.translate.utils.translation;

import com.assessment.translate.utils.concurrent.TranslateThreadPool;
import org.apache.log4j.Logger;

import java.util.HashMap;

public abstract class TranslateEngine {
    /*翻译引擎名称*/
    private String name;

    /*参数*/
    private final HashMap<String, String> parameters = new HashMap<String,String>();

    /*线程池*/
    private TranslateThreadPool translateThreadPool;

    /*日志*/
    public Logger log;

    /**
     * 使用post请求进行翻译
     *
     * @return 翻译结果
     */
    public abstract String translateByPost();

    /**
     * 使用get请求进行翻译
     *
     * @return 翻译结果
     */
    public abstract String translateByGet();

    /**
     * 初始化
     */
    public abstract boolean build(TranslateThreadPool translateThreadPool, String source, String target, String text);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParameters(String key, String value) {
        parameters.put(key, value);
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public TranslateThreadPool getTranslateThreadPool() {
        return translateThreadPool;
    }

    public void setTranslateThreadPool(TranslateThreadPool translateThreadPool) {
        this.translateThreadPool = translateThreadPool;
    }
}
