package org.caizs.nettypush.core.bootstrap;

import org.caizs.nettypush.core.base.Config;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.caizs.nettypush.core.handler.MsgHandler;

import java.util.ArrayList;
import java.util.List;

public class LinkBootstrap {

    private List<MsgHandler> clientHandlers;

    private List<MsgHandler> serverHandlers;

    public LinkBootstrap() {
        this.clientHandlers = new ArrayList<>();
        this.serverHandlers = new ArrayList<>();
    }

    //添加发送端 业务自定义handler
    public LinkBootstrap addClientHandlers(List<MsgHandler> handlers) {
        this.clientHandlers.addAll(handlers);
        return this;
    }

    public LinkBootstrap addClientHandler(MsgHandler handler) {
        this.clientHandlers.add(handler);
        return this;
    }

    /*
     * 添加接收端 业务自定义handler
     */
    public LinkBootstrap addServerHandlers(List<MsgHandler> handlers) {
        this.serverHandlers.addAll(handlers);
        return this;
    }

    public LinkBootstrap addServerHandler(MsgHandler handler) {
        this.serverHandlers.add(handler);
        return this;
    }

    public void start() {
        ConfigLoader.load(); //IDE里调试执行需要在项目根目录放置config.properties
        if (Config.mode == 1) {
            LinkClient client = new LinkClient();
            client.addHandlers(this.clientHandlers).connect();
        } else if (Config.mode == 2) {
            new LinkClient().connect();
            new Thread(() -> new LinkServer().start()).start();
        } else if (Config.mode == 3) {
            new Thread(() -> new LinkServer().addHandlers(this.serverHandlers).start()).start();
        }

    }

}
