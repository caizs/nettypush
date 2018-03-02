package org.caizs.nettypush.core.server;

import org.caizs.nettypush.core.base.Config;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.caizs.nettypush.core.handler.MsgHandlerRegistry;
import org.caizs.nettypush.core.codec.DelimiterJsonDecoder;
import org.caizs.nettypush.core.codec.DelimiterJsonEncoder;
import org.caizs.nettypush.core.codec.Protocol;
import org.caizs.nettypush.core.handler.DefaultServerhandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static Server getInstance() {
        return InnerServer.server;
    }

    public void start(int port) {
        try {
            ChannelFuture f = bootstrap.bind(port).sync();//绑定端口 等待绑定成功
            f.channel().closeFuture().sync();//同步等待服务器退出
        } catch (Exception e) {
            logger.error("启动服务失败", e);
        } finally {
            //释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private IdentifiedChannelManager serverChannelManager;
    private MsgHandlerRegistry registry;

    static class InnerServer {
        private static Server server;
        private static EventLoopGroup bossGroup;
        private static EventLoopGroup workerGroup;
        private static ServerBootstrap bootstrap;
        private static MsgHandlerRegistry registry;

        static {
            ConfigLoader.load();
            bossGroup = new NioEventLoopGroup(); //接收客户端连接用
            workerGroup = new NioEventLoopGroup();//处理网络读写事件
            bootstrap = new ServerBootstrap();
            registry = new MsgHandlerRegistry();
            server = new Server(bossGroup, workerGroup, bootstrap, new IdentifiedChannelManager(), registry);

            //配置服务器启动类
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .option(ChannelOption.SO_BACKLOG, 1024)//客户端连接请求队列大小
                     //.option(ChannelOption.TCP_NODELAY, true)
                     .handler(new LoggingHandler(LogLevel.INFO))//配置日志输出
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             //3个客户端心跳秒未收到消息就触发空闲读事件
                             ch.pipeline().addLast(new IdleStateHandler(3 * Config.heartbeatTick, 0, 0));
                             ch.pipeline()
                               .addLast(new DelimiterBasedFrameDecoder(Config.maxFrameLength, Unpooled.copiedBuffer(
                                       Protocol.DELIMITER_BYTES)));//maxFramelength保证一次能获取完整消息
                             ch.pipeline().addLast(new DelimiterJsonDecoder());//从上个decoder拿到完整消息后反序列化
                             ch.pipeline().addLast(new DelimiterJsonEncoder());//用json序列化
                             ch.pipeline().addLast(new DefaultServerhandler(server));
                         }
                     });
        }
    }

    public IdentifiedChannelManager getManager() {
        return serverChannelManager;
    }

    public Server(EventLoopGroup bossGroup, EventLoopGroup workerGroup, ServerBootstrap bootstrap,
            IdentifiedChannelManager manager, MsgHandlerRegistry registry) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.bootstrap = bootstrap;
        this.serverChannelManager = manager;
        this.registry = registry;
    }

    public MsgHandlerRegistry getRegistry() {
        return registry;
    }

    public static void main(String[] args) throws Exception {
        Server.getInstance().start(Config.listenPort);
    }

}