package com.assessment.translate.mapper;

import com.assessment.translate.entity.Translation;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface TranslationMapper {
    List<Translation> findAllByIp(String ip, Date date);

    void addTranslation(Translation translation);
}
