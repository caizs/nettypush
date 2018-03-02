package org.caizs.nettypush.core.base;


import org.caizs.nettypush.core.client.Client;
import org.caizs.nettypush.core.client.RequestChannel;
import org.caizs.nettypush.core.codec.MsgProtocol;
import org.caizs.nettypush.core.codec.Protocol;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.msg.Msg;
import org.caizs.nettypush.core.handler.DefaultBridgeHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object raw) throws Exception {
        MsgProtocol protocol = (MsgProtocol) raw;
        preHandle(ctx, protocol);
        if (protocol.getType() == Protocol.TYPE_PING) {
            if (ConfigLoader.getPropertyBoolean("link.logging", false)) {
                logger.info("msg [" + JsonUtil.toJson(protocol) + "]");
                logger.info("ping from " + ctx.channel().remoteAddress());
            }
            return;
        }
        if (Config.mode == 2) {//桥接模式时传递
            DefaultBridgeHandler.bridgeWrite(ctx, protocol);
        } else {
            handle(ctx, raw, protocol.getMsg());//消息处理
        }
    }

    protected void ping(ChannelHandlerContext context) {
        if (ConfigLoader.getPropertyBoolean("link.logging", false)) {
            logger.info("ping to " + context.channel().remoteAddress());
        }
        RequestChannel channel = Client.getInstance().getManager().get(context.channel().id());
        if (channel != null) {
            channel.ping();
        }
    }

    protected abstract void handle(ChannelHandlerContext channelHandlerContext, Object raw, Msg msg);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.warn("---" + ctx.channel().remoteAddress() + " is active---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
    }

    protected void preHandle(ChannelHandlerContext ctx, MsgProtocol msgProtocol) {
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}