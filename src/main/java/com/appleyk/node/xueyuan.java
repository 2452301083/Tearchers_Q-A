package com.appleyk.node;/*
@author lss
@date 2020/5/16-13:01
*/

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class xueyuan extends BaseEntity {

    private long xueyuanid;
    private String name;

    public long getXueyuanid() {
        return xueyuanid;
    }

    public void setXueyuanid(long xueyuanid) {
        this.xueyuanid = xueyuanid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
