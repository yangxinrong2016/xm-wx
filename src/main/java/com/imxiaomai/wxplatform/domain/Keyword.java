package com.imxiaomai.wxplatform.domain;

public class Keyword {
    private Integer id;

    private String wxid;

    private String content;

    private String responsetype;

    private Integer responseid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid == null ? null : wxid.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getResponsetype() {
        return responsetype;
    }

    public void setResponsetype(String responsetype) {
        this.responsetype = responsetype == null ? null : responsetype.trim();
    }

    public Integer getResponseid() {
        return responseid;
    }

    public void setResponseid(Integer responseid) {
        this.responseid = responseid;
    }
}