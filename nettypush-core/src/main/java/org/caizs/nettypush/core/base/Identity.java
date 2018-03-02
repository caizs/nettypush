package org.caizs.nettypush.core.base;

import org.caizs.nettypush.core.common.Util;

import java.io.Serializable;

import static io.netty.util.internal.StringUtil.isNullOrEmpty;

public class Identity implements Serializable {
    //目的标识，用于服务端连接分组
    private String group;
    //身份标识，标识连接发起方
    private String identity;

    public String toId() {
        if (isNullOrEmpty(group) || isNullOrEmpty(identity)) {
            return null;
        }
        return group + "_" + identity;
    }

    public Identity(String identity, String group) {
        Util.checkBlank(identity, "identity");
        Util.checkBlank(group, "group");
        this.identity = identity;
        this.group = group;
    }

    public Identity() {

    }

    public String getIdentity() {
        return identity;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
