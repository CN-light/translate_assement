package com.assessment.translate.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Engine {
    @JSONField(name = "id")
    private String id;

    @JSONField(name = "name")
    private Dictionary name;

    @JSONField(name = "language")
    private Dictionary language;

    @JSONField(name = "abbreviation")
    private String abbreviation;

    /**
     * 返回此类的json字符串
     *
     * @return
     */
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 返回此类的json对象
     *
     * @return
     */
    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Dictionary getName() {
        return name;
    }

    public void setName(Dictionary name) {
        this.name = name;
    }

    public Dictionary getLanguage() {
        return language;
    }

    public void setLanguage(Dictionary language) {
        this.language = language;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
