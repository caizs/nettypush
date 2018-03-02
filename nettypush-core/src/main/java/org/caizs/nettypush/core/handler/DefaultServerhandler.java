package org.caizs.nettypush.core.handler;

import org.caizs.nettypush.core.codec.MsgProtocol;
import org.caizs.nettypush.core.common.BizException;
import org.caizs.nettypush.core.base.ChannelHandler;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.server.IdentifiedChannel;
import org.caizs.nettypush.core.server.IdentifiedChannelManager;
import org.caizs.nettypush.core.server.Server;
import org.caizs.nettypush.core.msg.Msg;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServerhandler extends ChannelHandler {

    private static Logger logger = LoggerFactory.getLogger(DefaultServerhandler.class);

    private Server server;

    public DefaultServerhandler(Server server) {
        this.server = server;
    }

    @Override protected void handle(ChannelHandlerContext ctx, Object raw, Msg msg) {
        server.getRegistry().distribute(raw, msg);
    }

    @Override protected void preHandle(ChannelHandlerContext ctx, MsgProtocol msgProtocol) {
        super.preHandle(ctx, msgProtocol);
        IdentifiedChannelManager manager = Server.getInstance().getManager();
        if (msgProtocol.getMsg() == null) {
            return;
        }
        String identity = msgProtocol.getMsg().getIdentity();
        String group = msgProtocol.getMsg().getGroup();
        if (identity == null || group == null) {
            return;
        }
        manager.add(new IdentifiedChannel(new Identity(identity, group), ctx.channel()));
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        logger.info("读取超时，address[" + ctx.channel().remoteAddress().toString() + "]");
        IdentifiedChannelManager manager = Server.getInstance().getManager();
        manager.close(ctx); //发生读空闲事件，服务端即关闭无用连接，等待客户端重试
    }

    @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.error("连接失效，address[" + ctx.channel().remoteAddress().toString() + "]");
        IdentifiedChannelManager manager = Server.getInstance().getManager();
        manager.close(ctx);//连接失效，关闭连接，等待客户端重试
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        logger.info("连接异常，address[" + ctx.channel().remoteAddress().toString() + "]");
        if (cause instanceof BizException == false) {
            IdentifiedChannelManager manager = Server.getInstance().getManager();
            manager.close(ctx);//出现非业务异常，即关闭连接，等待客户端重试
        }
    }

}