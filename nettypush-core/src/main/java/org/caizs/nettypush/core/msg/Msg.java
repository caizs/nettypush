package org.caizs.nettypush.core.msg;

import org.caizs.nettypush.core.base.Identity;

import java.io.Serializable;

public class Msg extends Identity implements Serializable {

    //自定义消息类型
    private String type;
    //消息体
    private Object body;

    public Msg(String type, Object body) {
        this.type = type;
        this.body = body;
    }

    public Msg() {
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
