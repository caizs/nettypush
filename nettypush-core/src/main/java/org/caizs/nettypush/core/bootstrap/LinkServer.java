package org.caizs.nettypush.core.bootstrap;

import org.caizs.nettypush.core.base.Config;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.common.Util;
import org.caizs.nettypush.core.msg.Msg;
import org.caizs.nettypush.core.server.IdentifiedChannel;
import org.caizs.nettypush.core.server.Server;
import org.caizs.nettypush.core.handler.MsgHandler;
import org.caizs.nettypush.core.handler.MsgHandlerRegistry;

import java.util.List;

public class LinkServer {

    public LinkServer addHandler(MsgHandler handler) {
        Server.getInstance().getRegistry().addHandler(handler);
        return this;
    }

    public LinkServer addHandlers(List<MsgHandler> handlers) {
        MsgHandlerRegistry registry = Server.getInstance().getRegistry();
        handlers.stream().forEach(h -> registry.addHandler(h));
        return this;
    }

    /**
     * 启动，方法会同步阻塞
     */
    public void start() {
        Server.getInstance().start(Config.listenPort);
    }

    public static void push(Msg msg) {
        Util.checkNull(msg.getIdentity(), "identity");
        Util.checkNull(msg.getGroup(), "group");
        Util.checkNull(msg.getType(), "type");
        Util.checkNull(msg.getBody(), "body");

        IdentifiedChannel channel = Server.getInstance().getManager().get(new Identity(msg.getIdentity(), msg.getGroup()));
        channel.write(msg);

    }

    public static void push(Identity identity, Msg msg) {
        IdentifiedChannel channel = Server.getInstance().getManager().get(identity);
        if (channel != null) {
            Util.checkNull(identity.getGroup(), "group");
            Util.checkNull(identity.getIdentity(), "identity");
            msg.setIdentity(identity.getIdentity());
            msg.setGroup(identity.getGroup());
            channel.write(msg);
        }
    }

}
