package com.assessment.translate.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.sql.Date;

public class Translation {
    @JSONField(name = "id")
    private String id;

    @JSONField(name = "ip")
    private String ip;

    @JSONField(name = "engine")
    private Dictionary engine;

    @JSONField(name = "source")
    private Text source;

    @JSONField(name = "target")
    private Text target;

    @JSONField(name = "grade")
    private String grade;

    @JSONField(name = "date")
    private Date date;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Dictionary getEngine() {
        return engine;
    }

    public void setEngine(Dictionary engine) {
        this.engine = engine;
    }

    public Text getSource() {
        return source;
    }

    public void setSource(Text source) {
        this.source = source;
    }

    public Text getTarget() {
        return target;
    }

    public void setTarget(Text target) {
        this.target = target;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        if (grade == null || "".equals(grade))
            this.grade = "0";
        else
            this.grade = grade;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
