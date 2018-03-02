package org.caizs.nettypush.core.client;

import org.caizs.nettypush.core.common.BizException;
import org.caizs.nettypush.core.base.Identity;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.util.internal.StringUtil.isNullOrEmpty;

public class RequestWrapper implements ChannelFutureListener {

    private static Logger logger = LoggerFactory.getLogger(RequestWrapper.class);

    private String host;
    private Integer port;
    private Identity identity;
    private Callback callback;

    @Override public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            logger.info("success Connect to " + getHost() + ":" + getPort() + " " + identity.toId());
            RequestChannelManager manager = Client.getInstance().getManager();
            boolean result = manager.add(new RequestChannel(future.channel(), RequestWrapper.this));
            if (!result) {
                logger.error("已经存在连接, 关闭新建连接");
                future.channel().close();
                return;
            }
        }
        if (callback != null) {
            callback.complete(future, RequestWrapper.this);
        }
    }

    public RequestWrapper(String host, Integer port, Identity identity, Callback callback) {
        this.host = host;
        this.port = port;
        checkIdentity(identity);
        this.identity = identity;
        this.callback = callback;
    }

    private void checkIdentity(Identity identity) {
        if (identity == null || isNullOrEmpty(identity.getIdentity()) || isNullOrEmpty(identity.getGroup())) {
            throw new BizException("identity和group不能为空");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
