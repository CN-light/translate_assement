package com.assessment.translate.mapper;

import com.assessment.translate.entity.Engine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EngineMapper {
    List<Engine> findAllLanguageByEngineName(String name);

    String findAbbreviation(String name, String language);
}
