package com.assessment.translate.service;

import com.assessment.translate.entity.Dictionary;
import com.assessment.translate.entity.Engine;

import java.util.List;

public interface EngineService {
    /**
     * @param name
     * @return 根据翻译引擎名称查询该翻译引擎当前支持的所有语言
     */
    List<Engine> findAllLanguageByEngineName(String name);

    /**
     * @param name
     * @param language
     * @return 根据翻译引擎名称和语言类别查询对应的语言缩写
     */
    String findAbbreviation(String name, String language);
}
