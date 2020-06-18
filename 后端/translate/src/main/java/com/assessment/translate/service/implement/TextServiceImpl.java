package com.assessment.translate.service.implement;

import com.assessment.translate.entity.Text;
import com.assessment.translate.mapper.TextMapper;
import com.assessment.translate.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("textService")
public class TextServiceImpl implements TextService {
    @Autowired
    private TextMapper textMapper;

    @Override
    public void addText(Text text) {
        textMapper.addText(text);
    }
}
