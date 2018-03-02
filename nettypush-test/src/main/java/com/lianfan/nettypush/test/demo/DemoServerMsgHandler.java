package com.lianfan.nettypush.test.demo;

import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.bootstrap.LinkServer;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.handler.MsgHandler;
import org.caizs.nettypush.core.msg.Msg;

public class DemoServerMsgHandler implements MsgHandler<DemoBean> {

    @Override public String group() {
        return "NC_LINK";
    }

    @Override public String type() {
        return "query";
    }

    @Override public void handle(Msg msg, DemoBean msgBody, Identity identity) {
        System.out.println(JsonUtil.toJson(msgBody));


        msg.setType("resp query");
        msg.setBody(new DemoBean("response","respone id"));
        LinkServer.push(new Identity("100000", "NC_LINK"), msg);
    }


}
