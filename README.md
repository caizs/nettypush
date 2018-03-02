# nettypush
  - 基于netty实现消息推送
  - 可通过配置实现多节点桥接，类似nginx stream可实现长连接转发
  - 基于tcp，自定义传输协议
  - 双工，心跳保持,失败重连
  - 消息分组，按自定义消息类型订阅

## 技术栈
  1. springboot
  2. jdk8
  3. netty
  4. redis
  5. logback
  7. gradle

## 配置文件说明：
```xml
# 客户端心跳周期,客户端在写空闲时发起心跳
link.heartbeatTick=3
# 连接重试间隔
link.retryTime=5
#100KB, 消息最大字节数
link.maxFrameLength=102400
# 模式
#1 为发起模式，用于发起服务器
#2 为桥接模式，用户中间桥接
#3 为接收模式，用于接收服务器
link.mode=1
# 连接目的服务器ip
# 发起模式和桥接模式必填
link.connToIp=127.0.0.1
# 连接目的服务器端口
# 发起模式和桥接桥接必填
link.connToPort=30003
# 监听端口
# 桥接和接收模式必填
link.listenPort=
# 消息分组
link.group=NC_LINK
# 发起身份/设备标识
link.identity=aj4afv57
# 是否开启日志打印,默认false
link.logging=true
```


