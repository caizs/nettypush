package org.caizs.nettypush.core.handler;

import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.msg.Msg;

public interface MsgHandler<T> {
    //identity group
    String group();

    //msg type
    String type();

    void handle(Msg msg, T msgBody, Identity identity);

}
