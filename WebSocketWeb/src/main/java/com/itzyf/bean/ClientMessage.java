package com.itzyf.bean;

/**
 * 
 * @ClassName: ClientMessage
 * @Description: 客户端发送消息实体
 * @author cheng
 * @date 2017年9月27日 下午4:24:11
 */
public class ClientMessage {
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}