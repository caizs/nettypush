package org.caizs.nettypush.core.codec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.msg.Msg;

import java.io.Serializable;

public class MsgProtocol extends Protocol implements Serializable {
    //消息体
    private Msg msg;

    public MsgProtocol(Msg msg) {
        super(Protocol.TYPE_CUSTOM);
        this.msg = msg;
    }

    protected MsgProtocol(Msg msg, byte ver, byte type, int length) {
        super(ver, type, length);
        this.msg = msg;
    }

    @JsonIgnore
    public Identity getIdentity() {
        return msg;
    }

    public MsgProtocol(byte type) {
        super(type);
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }
}
