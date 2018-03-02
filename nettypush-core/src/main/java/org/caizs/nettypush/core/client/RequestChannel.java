package org.caizs.nettypush.core.client;

import io.netty.channel.Channel;
import org.caizs.nettypush.core.base.ChannelWrapper;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.codec.MsgProtocol;
import org.caizs.nettypush.core.codec.Protocol;
import org.caizs.nettypush.core.msg.Msg;

public class RequestChannel extends ChannelWrapper {

    private RequestWrapper requestWrapper;

    public RequestChannel(Channel channel, RequestWrapper wrapper) {
        super(channel);
        this.requestWrapper = wrapper;
    }

    public RequestWrapper getRequestWrapper() {
        return requestWrapper;
    }

    public void setRequestWrapper(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
    }

    public void ping() {
        MsgProtocol protocol = new MsgProtocol(Protocol.TYPE_PING);
        Msg msg = new Msg(null, null);
        msg.setIdentity(requestWrapper.getIdentity().getIdentity());
        msg.setGroup(requestWrapper.getIdentity().getGroup());
        protocol.setMsg(msg);
        this.getChannel().writeAndFlush(protocol);
    }

    public String toIdentityId(){
        return this.getRequestWrapper().getIdentity().toId();
    }

    @Override public Identity getIdentity() {
        return this.requestWrapper.getIdentity();
    }

    @Override public void write(Msg obj) {
        obj.setIdentity(requestWrapper.getIdentity().getIdentity());
        obj.setGroup(requestWrapper.getIdentity().getGroup());
        super.write(obj);
    }
}
