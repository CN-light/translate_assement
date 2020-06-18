package com.assessment.translate.service.implement;

import com.assessment.translate.entity.Dictionary;
import com.assessment.translate.mapper.DictionaryMapper;
import com.assessment.translate.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private DictionaryMapper dictionaryMapper;

    /**
     * 返回所有语言类别
     *
     * @return
     */
    @Override
    public List<Dictionary> findAllLanguage() {
        return dictionaryMapper.findAllLanguage();
    }

    @Override
    public Dictionary findLanguageByName(String name) {
        return dictionaryMapper.findLanguageByName(name);
    }


    @Override
    public List<Dictionary> findAllEngine() {
        return dictionaryMapper.findAllEngine();
    }

    @Override
    public Dictionary findEngineByName(String name) {
        return dictionaryMapper.findEngineByName(name);
    }
}
