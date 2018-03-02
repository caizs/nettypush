package org.caizs.nettypush.core.client;

import org.caizs.nettypush.core.base.Config;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RetryCallback implements Callback {

    private static Logger logger = LoggerFactory.getLogger(RetryCallback.class);

    private void connect(RequestWrapper wrapper) {
        Client.getInstance().connect(wrapper.getHost(), wrapper.getPort(),wrapper.getIdentity(), wrapper.getCallback());
    }

    @Override public void complete(ChannelFuture future, RequestWrapper wrapper) {
        if (!future.isSuccess()) {
            logger.error("fail connect to " +wrapper.getHost() + ":" + wrapper.getPort());
            future.channel().eventLoop().schedule(() -> connect(wrapper), Config.retryTime, TimeUnit.SECONDS);
        }
    }
}
