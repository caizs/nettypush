package com.lianfan.nettypush.test.demo;

import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.handler.MsgHandler;
import org.caizs.nettypush.core.msg.Msg;

public class DemoClientMsgHandler implements MsgHandler<DemoBean> {
    @Override public String group() {
        return "NC_LINK";
    }

    @Override public String type() {
        return "resp query";
    }

    @Override public void handle(Msg msg, DemoBean msgBody, Identity identity) {
        System.out.println("接收消息[" + JsonUtil.toJson(msgBody) + "]");

    }
}
