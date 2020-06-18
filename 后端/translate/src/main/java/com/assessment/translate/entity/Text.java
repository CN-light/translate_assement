package com.assessment.translate.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Text {
    @JSONField(name = "id")
    private String id;

    @JSONField(name = "type")
    private Dictionary type;

    @JSONField(name = "text")
    private String text;

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

    public Dictionary getType() {
        return type;
    }

    public void setType(Dictionary type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null || "".equals(text))
            this.text = "";
        else
            this.text = text;
    }
}
