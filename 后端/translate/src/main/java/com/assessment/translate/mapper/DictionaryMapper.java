package com.assessment.translate.mapper;

import com.assessment.translate.entity.Dictionary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictionaryMapper {
    List<Dictionary> findAllLanguage();

    Dictionary findLanguageByName(String name);

    List<Dictionary> findAllEngine();

    Dictionary findEngineByName(String name);
}
