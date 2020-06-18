package com.assessment.translate.service;

import com.assessment.translate.entity.Translation;

import java.util.List;
import java.util.Map;

public interface TranslationService {
    /**
     * 根据ip获得当日提交的数据
     *
     * @param ip
     * @return
     */
    List<Translation> findAllByIp(String ip);

    /**
     * 插入一条translation数据
     *
     * @param translation
     */
    void addTranslation(Translation translation);

    /**
     * 翻译
     *
     * @param map
     * @return
     */
    String translate(Map<String, String> map);

    /**
     * 导出数据到excel表格
     *
     * @param map
     * @return
     */
    String exportAsExcel(Map<String, String> map);

    /**
     * 提交翻译质量评估分数
     *
     * @param map
     * @return
     */
    boolean submit(Map<String, String> map);
}
