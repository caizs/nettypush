package org.caizs.nettypush.stub.redis;

import org.caizs.nettypush.core.bootstrap.LinkServer;
import org.caizs.nettypush.core.msg.Msg;
import org.caizs.nettypush.stub.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class LinkMessageSubscriber implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(LinkMessageSubscriber.class);

    public static final ChannelTopic topic = new ChannelTopic("nc_link");

    @Override public void onMessage(Message message, byte[] pattern) {
        logger.info(message.toString());
        LinkServer.push(JsonUtil.toObject(message.toString(), Msg.class));
    }
}
