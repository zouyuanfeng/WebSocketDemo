package com.itzyf.websocketdemo;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/31 09:40
 */
public class ClientMessage {
    private String name;
    private String id;

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
