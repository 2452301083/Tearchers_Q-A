package com.appleyk.node;/*
@author lss
@date 2020/5/16-12:59
*/

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class subject extends BaseEntity {

    private long subid;
    private String name;

    public long getSubid() {
        return subid;
    }

    public void setSubid(long subid) {
        this.subid = subid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
