package com.assessment.translate.mapper;

import com.assessment.translate.entity.Text;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TextMapper {
    void addText(Text text);
}
