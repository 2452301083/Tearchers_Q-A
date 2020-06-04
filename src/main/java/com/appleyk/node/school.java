package com.appleyk.node;/*
@author lss
@date 2020/5/15-22:27
*/

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class school extends BaseEntity {

    private long schoolid;
    private String name;

    public long getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(long schoolid) {
        this.schoolid = schoolid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
