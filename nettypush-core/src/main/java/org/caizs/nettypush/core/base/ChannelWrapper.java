package org.caizs.nettypush.core.base;

import org.caizs.nettypush.core.codec.MsgProtocol;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.msg.Msg;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChannelWrapper {
    private static Logger logger = LoggerFactory.getLogger(ChannelWrapper.class);

    private Channel channel;

   public abstract Identity getIdentity();

    public ChannelWrapper(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void write(Msg obj) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new MsgProtocol(obj));
        } else {
            logger.error("消息发送失败 [" + JsonUtil.toJson(obj) + "]");
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

}
