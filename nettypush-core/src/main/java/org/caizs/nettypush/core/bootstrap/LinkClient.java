package org.caizs.nettypush.core.bootstrap;

import org.caizs.nettypush.core.base.Config;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.client.Client;
import org.caizs.nettypush.core.client.RequestChannel;
import org.caizs.nettypush.core.client.RequestChannelManager;
import org.caizs.nettypush.core.client.RetryCallback;
import org.caizs.nettypush.core.msg.Msg;
import org.caizs.nettypush.core.handler.MsgHandler;
import org.caizs.nettypush.core.handler.MsgHandlerRegistry;

import java.util.List;

public class LinkClient {

    public static final Identity linkIdentity = new Identity(Config.identity, Config.group);

    public LinkClient addHandler(MsgHandler handler) {
        Client.getInstance().getRegistry().addHandler(handler);
        return this;
    }

    public LinkClient addHandlers(List<MsgHandler> handlers) {
        MsgHandlerRegistry registry = Client.getInstance().getRegistry();
        handlers.stream().forEach(h -> registry.addHandler(h));
        return this;
    }

    public LinkClient connect() {
        Client.getInstance().connect(Config.connToIp, Config.connToPort, linkIdentity, new RetryCallback());
        return this;
    }

    public static void push(Msg msg) {
        RequestChannelManager manager = Client.getInstance().getManager();
        RequestChannel channel = manager.get(linkIdentity.toId());
        if (channel != null) {
            channel.write(msg);
        }
    }


}
