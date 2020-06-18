package com.assessment.translate.utils.translation;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class TranslateEngineFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final Logger log = Logger.getLogger(TranslateEngineFactory.class);

    /**
     * 根据注入的名字获得对应的翻译引擎
     *
     * @param name
     * @return
     */
    public static TranslateEngine getTranslateEngine(String name) {
        if (name == null || "".equals(name))
            return null;
        TranslateEngine result;
        try {
            if (applicationContext != null) {
                result = applicationContext.getBean(name, TranslateEngine.class);
                if (result.getName().equals(name)) {
                    return result;
                } else {
                    log.error("annotation does not match its own name");
                    return null;
                }
            }
        } catch (BeansException e) {
            log.error(e.toString());
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TranslateEngineFactory.applicationContext = applicationContext;
    }
}
