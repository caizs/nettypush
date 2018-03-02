package org.caizs.nettypush.core.base;

/**
 * 配置文件，被当前jar包所在目录的config.properties文件覆盖
 */
public class Config {

    public static volatile boolean loaded = false;

    /**
     * 客户端心跳周期
     * 客户端在写空闲时发起心跳
     */
    public static volatile Integer heartbeatTick = 3;

    /**
     * 连接重试间隔
     */
    public static volatile Integer retryTime = 2;

    public static volatile Integer maxFrameLength = 100 * 1024;//100KB, 消息最大字节数

    //---------------------------

    /**
     * 模式
     * 1 为发起模式，用于内网连接发起服务器
     * 2 为桥接模式，用户中间桥接
     * 3 为接收模式，用于云上接收服务器
     */
    public static volatile Integer mode = 1;

    /**
     * 连接目的服务器ip
     * 发起模式和桥接模式必填
     */
    public static volatile String connToIp = "127.0.0.1";

    /**
     * 连接目的服务器端口
     * 发起模式和桥接桥接必填
     */
    public static volatile Integer connToPort = 8001;

    /**
     * 监听端口
     * 桥接和接收模式必填
     */
    public static volatile Integer listenPort = null;

    /**
     * 发起的连接用于连接nursecare
     */
    public static volatile String group = "NC_LINK";

    /**
     * 发起身份/设备标识
     * 发起和桥接模式必填
     */
    public static volatile String identity = "100000";//例如医院id


}
