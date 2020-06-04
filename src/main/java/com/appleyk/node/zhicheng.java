package com.appleyk.node;/*
@author lss
@date 2020/5/16-13:13
*/

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class zhicheng extends BaseEntity {

    private long zhichengid;
    private String name;

    public long getZhichengid() {
        return zhichengid;
    }

    public void setZhichengid(long zhichengid) {
        this.zhichengid = zhichengid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
