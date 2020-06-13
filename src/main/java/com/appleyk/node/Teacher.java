package com.appleyk.node;/*
@author lss
@date 2020/6/12-20:34
*/

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Teacher {

    @Id
    private Long id;
    private String name;

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public String getName(){return name;}

    public void setName(String name){this.name = name;}
}
