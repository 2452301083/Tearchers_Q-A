package com.appleyk.entity;

/*
@author lss
@date 2020/5/20-14:08
*/
//@Component
public class tea_tui {

    private String name;
    private String touxiang;

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    private int times;
    private String tea_lianjie;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getTea_lianjie() {
        return tea_lianjie;
    }

    public void setTea_lianjie(String tea_lianjie) {
        this.tea_lianjie = tea_lianjie;
    }
}
