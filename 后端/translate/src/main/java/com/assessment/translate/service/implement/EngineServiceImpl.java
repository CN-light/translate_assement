package com.assessment.translate.service.implement;

import com.assessment.translate.entity.Dictionary;
import com.assessment.translate.entity.Engine;
import com.assessment.translate.mapper.EngineMapper;
import com.assessment.translate.service.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("engineService")
public class EngineServiceImpl implements EngineService {
    @Autowired
    private EngineMapper engineMapper;

    @Override
    public List<Engine> findAllLanguageByEngineName(String name) {
        return engineMapper.findAllLanguageByEngineName(name);
    }

    @Override
    public String findAbbreviation(String name, String language) {
        return engineMapper.findAbbreviation(name, language);
    }
}
