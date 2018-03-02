package org.caizs.nettypush.core.handler;


import org.caizs.nettypush.core.client.Client;
import org.caizs.nettypush.core.base.ChannelHandler;
import org.caizs.nettypush.core.msg.Msg;
import io.netty.channel.ChannelHandlerContext;

public class DefaultClientHandler extends ChannelHandler {

    private Client client;

    public DefaultClientHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void handle(ChannelHandlerContext channelHandlerContext, Object raw, Msg msg) {
        client.getRegistry().distribute(raw, msg);
    }

    @Override protected void handleWriterIdle(ChannelHandlerContext ctx) {
        super.handleWriterIdle(ctx);
        ping(ctx);//发生写空闲事件，则ping服务器
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.reConnect(ctx); //连接失效重连
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        client.reConnect(ctx); //连接异常重连
    }
}