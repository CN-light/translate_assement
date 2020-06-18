package com.assessment.translate.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Dictionary {
    @JSONField(name = "id")
    private String id;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "alias")
    private String alias;

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

    public String getName() {
        return name;
    }

    public void setName(String type) {
        this.name = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
