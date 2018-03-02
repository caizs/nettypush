package org.caizs.nettypush.core.handler;

import org.caizs.nettypush.core.client.Client;
import org.caizs.nettypush.core.client.RequestChannel;
import org.caizs.nettypush.core.client.RequestChannelManager;
import org.caizs.nettypush.core.codec.MsgProtocol;
import org.caizs.nettypush.core.server.IdentifiedChannel;
import org.caizs.nettypush.core.server.IdentifiedChannelManager;
import org.caizs.nettypush.core.server.Server;
import io.netty.channel.ChannelHandlerContext;

public class DefaultBridgeHandler {

    public static void bridgeWrite(ChannelHandlerContext ctx, MsgProtocol raw) {
        //从上游发往下游, 从云上通道发往内网通道
        RequestChannelManager requestChannelManager = Client.getInstance().getManager();
        RequestChannel clientChannel = requestChannelManager.get(ctx.channel().id());
        if (clientChannel != null && clientChannel.getChannel().isActive()) {
            upToDown(ctx, raw);
            return;
        }

        //从下游发往上游，从内网通道发往云上通道
        IdentifiedChannelManager serverChannelManager = Server.getInstance().getManager();
        IdentifiedChannel serverChannel = serverChannelManager.get(raw.getIdentity());
        if (serverChannel != null && serverChannel.getChannel().isActive()) {
            downToUp(ctx, raw);
            return;
        }
    }

    private static void upToDown(ChannelHandlerContext ctx, MsgProtocol raw) {
        IdentifiedChannelManager serverChannelManager = Server.getInstance().getManager();
        for (IdentifiedChannel identifiedChannel : serverChannelManager.collect(raw.getIdentity())) {
            if (identifiedChannel.getChannel().isActive()) {
                identifiedChannel.getChannel().writeAndFlush(raw);
            }
        }
    }

    private static void downToUp(ChannelHandlerContext ctx, MsgProtocol raw) {
        RequestChannelManager manager = Client.getInstance().getManager();
        for (RequestChannel addressChannel : manager.collect()) {
            if (addressChannel.getChannel().isActive()) {
                addressChannel.getChannel().writeAndFlush(raw);
            }
        }
    }

}
