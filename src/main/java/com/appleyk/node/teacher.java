package com.appleyk.node;/*
@author lss
@date 2020/5/15-22:34
*/

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class teacher extends BaseEntity {

    private long teaid;
    private String name;
    private String tel;
    private String biyexx;
    private String zhiwei;
    private String introduction;
    private String email;
    private String tea_lianjie;
    private String touxaing;

    public long getTeaid() {
        return teaid;
    }

    public void setTeaid(long teaid) {
        this.teaid = teaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBiyexx() {
        return biyexx;
    }

    public void setBiyexx(String biyexx) {
        this.biyexx = biyexx;
    }

    public String getZhiwei() {
        return zhiwei;
    }

    public void setZhiwei(String zhiwei) {
        this.zhiwei = zhiwei;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTea_lianjie() {
        return tea_lianjie;
    }

    public void setTea_lianjie(String tea_lianjie) {
        this.tea_lianjie = tea_lianjie;
    }

    public String getTouxaing() {
        return touxaing;
    }

    public void setTouxaing(String touxaing) {
        this.touxaing = touxaing;
    }
}
