package org.caizs.nettypush.core.client;

import io.netty.channel.ChannelFuture;

public interface Callback {

    void complete(ChannelFuture future, RequestWrapper wrapper);
}
