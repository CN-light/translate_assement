package com.assessment.translate.service;

import com.assessment.translate.entity.Dictionary;

import java.util.List;

public interface DictionaryService {
    /**
     * @return 所有语言
     */
    List<Dictionary> findAllLanguage();

    /**
     * @return 根据name查询
     */
    Dictionary findLanguageByName(String name);

    /**
     * @return 所有翻译引擎
     */
    List<Dictionary> findAllEngine();

    /**
     * @return 根据name查询
     */
    Dictionary findEngineByName(String name);
}
