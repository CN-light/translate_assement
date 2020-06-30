package com.assessment.translate.service.implement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.assessment.translate.entity.Dictionary;
import com.assessment.translate.entity.Text;
import com.assessment.translate.entity.Translation;
import com.assessment.translate.mapper.TranslationMapper;
import com.assessment.translate.service.DictionaryService;
import com.assessment.translate.service.TextService;
import com.assessment.translate.service.TranslationService;
import com.assessment.translate.utils.ParameterUtils;
import com.assessment.translate.utils.concurrent.TranslateThreadPool;
import com.assessment.translate.utils.translation.TranslateEngine;
import com.assessment.translate.utils.translation.TranslateEngineFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service("translateService")
public class TranslationServiceImpl implements TranslationService {
    @Autowired
    private TranslationMapper translationMapper;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TextService textService;

    @Value("${max-request-number}")
    private String max;

    private TranslateThreadPool translateThreadPool;

    private final Logger log = Logger.getLogger(TranslationServiceImpl.class);

    @PostConstruct
    private void init() {
        /*每秒的任务数*/
        final int tasks = Integer.parseInt(max);

        int corePoolSize = ((int) (tasks * 0.75) <= 0 ? 1 : (int) (tasks * 0.75));
        int queueCapacity = ((int) (tasks * 0.05) <= 0 ? 1 : (int) (tasks * 0.05));
        int maxPoolSize = (Math.max((int) (tasks * 1.5), corePoolSize));
        translateThreadPool = new TranslateThreadPool(corePoolSize, maxPoolSize, queueCapacity);
    }

    @Override
    public List<Translation> findAllByIp(String ip) {
        return translationMapper.findAllByIp(ip, ParameterUtils.localDateToDate(LocalDate.now()));
    }

    @Override
    public void addTranslation(Translation translation) {
        translationMapper.addTranslation(translation);
    }

    @Override
    public Object translate(Map<String, String> map) {
        TranslateEngine engine = TranslateEngineFactory.getTranslateEngine(map.get("engine"));
        if (engine != null) {
            if (engine.build(translateThreadPool, map.get("source"), map.get("target"), map.get("text"))) {
                return engine.translateByPost();
            } else {
                log.error("failed to build engine");
                JSONObject error = new JSONObject();
                error.put("translation", "");
                error.put("error", "failed to build engine");
                return error;
            }
        } else {
            log.error("engine is not existed");
            JSONObject error = new JSONObject();
            error.put("translation", "");
            error.put("error", "no such translate engine");
            return error;
        }
    }

    @Override
    public JSONArray exportAsExcel(Map<String, String> map) {
        JSONArray result = new JSONArray();
        List<Translation> translations = findAllByIp(map.get("ip"));
        if (translations != null) {
            for (Translation translation : translations) {
                JSONObject object = new JSONObject();
                object.put("engine", dictionaryService.findEngineByName(translation.getEngine().getName()).getAlias());
                object.put("source", dictionaryService.findLanguageByName(translation.getSource().getType().getName()).getAlias());
                object.put("target", dictionaryService.findLanguageByName(translation.getTarget().getType().getName()).getAlias());
                object.put("sourceText", translation.getSource().getText());
                object.put("targetText", translation.getTarget().getText());
                object.put("grade", translation.getGrade());
                result.add(object);
            }
        }
        return result;
    }

    @Transactional
    @Override
    public boolean submit(Map<String, String> map) {
        Translation translation = new Translation();
        translation.setId(ParameterUtils.getUUID());
        translation.setIp(map.get("ip"));
        translation.setDate(ParameterUtils.localDateToDate(LocalDate.now()));
        Dictionary engine = dictionaryService.findEngineByName(map.get("engine"));
        if (engine == null) {
            log.error("no such translate engine");
            return false;
        }
        translation.setEngine(engine);
        Text source = new Text();
        source.setId(ParameterUtils.getUUID());
        source.setText(map.get("sourceText"));
        Dictionary sourceType = dictionaryService.findLanguageByName(map.get("source"));
        if (sourceType == null) {
            log.error("no such language type");
            return false;
        }
        source.setType(sourceType);
        translation.setSource(source);
        Text target = new Text();
        target.setId(ParameterUtils.getUUID());
        target.setText(map.get("targetText"));
        Dictionary targetType = dictionaryService.findLanguageByName(map.get("target"));
        if (targetType == null) {
            log.error("no such language type");
            return false;
        }
        target.setType(targetType);
        translation.setTarget(target);
        translation.setGrade(map.get("grade"));
        try {
            textService.addText(target);
            textService.addText(source);
            translationMapper.addTranslation(translation);
        } catch (Exception e) {
            //手动回滚
            log.error(e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
}
