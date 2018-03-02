package org.caizs.nettypush.core.client;

import org.caizs.nettypush.core.common.ConfigLoader;
import org.caizs.nettypush.core.handler.DefaultClientHandler;
import org.caizs.nettypush.core.codec.DelimiterJsonDecoder;
import org.caizs.nettypush.core.codec.DelimiterJsonEncoder;
import org.caizs.nettypush.core.codec.Protocol;
import org.caizs.nettypush.core.common.Util;
import org.caizs.nettypush.core.base.Config;
import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.handler.MsgHandlerRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static Client getInstance() {
        return InnerClient.client;
    }

    public void connect(String host, Integer port, Identity identity, Callback callback) {
        logger.info("发起连接，host[" + host + "], port[" + port + "]");
        Util.checkNull(host, "host");
        Util.checkNull(port, "port");
        Util.checkNull(identity, "identity");
        ChannelFuture future = bootstrap.connect(host, port);
        RequestWrapper wrapper = new RequestWrapper(host, port, identity, callback);
        future.addListener(wrapper);
    }

    public void reConnect(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            return;
        }
        RequestChannelManager manager = Client.getInstance().getManager();
        RequestChannel channel = manager.get(ctx.channel().id());
        if (channel == null) {
            return;
        }
        manager.close(ctx);
        logger.info("重新发起连接，host[" + channel.getRequestWrapper().getHost() + "], port[" + channel.getRequestWrapper().getHost()
                + "]");
        ChannelFuture future = bootstrap.connect(channel.getRequestWrapper().getHost(), channel.getRequestWrapper().getPort());
        future.addListener(channel.getRequestWrapper());
    }

    private NioEventLoopGroup group;
    private Bootstrap bootstrap;
    private RequestChannelManager requestChannelManager;
    private MsgHandlerRegistry registry;

    static class InnerClient {
        private static Client client;
        private static NioEventLoopGroup group;
        private static Bootstrap bootstrap;
        private static MsgHandlerRegistry registry;

        static {
            ConfigLoader.load();
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            registry = new MsgHandlerRegistry();
            client = new Client(group, bootstrap, new RequestChannelManager(), registry);
            bootstrap.group(group).channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             //1个客户端心跳周期没有写入就触发写事件，即发起心跳
                             ch.pipeline().addLast(new IdleStateHandler(0, Config.heartbeatTick, 0));//
                             ch.pipeline()
                               .addLast(new DelimiterBasedFrameDecoder(Config.maxFrameLength, Unpooled.copiedBuffer(
                                       Protocol.DELIMITER_BYTES)));//maxFramelength保证一次能获取完整消息
                             ch.pipeline().addLast(new DelimiterJsonDecoder());//从上个decoder拿到完整消息后反序列化
                             ch.pipeline().addLast(new DelimiterJsonEncoder());//用json序列化
                             ch.pipeline().addLast(new DefaultClientHandler(client));
                         }
                     });
        }
    }

    @Override protected void finalize() throws Throwable {
        group.shutdownGracefully();
        super.finalize();
    }

    public RequestChannelManager getManager() {
        return requestChannelManager;
    }

    private Client(NioEventLoopGroup group, Bootstrap bootstrap, RequestChannelManager manager, MsgHandlerRegistry registry) {
        this.group = group;
        this.bootstrap = bootstrap;
        this.requestChannelManager = manager;
        this.registry = registry;
    }

    public MsgHandlerRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(MsgHandlerRegistry registry) {
        this.registry = registry;
    }
}
