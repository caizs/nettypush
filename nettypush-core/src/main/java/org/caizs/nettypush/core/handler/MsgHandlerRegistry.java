package org.caizs.nettypush.core.handler;

import org.caizs.nettypush.core.base.Identity;
import org.caizs.nettypush.core.common.BizException;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.msg.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.caizs.nettypush.core.common.Util.checkBlank;
import static org.caizs.nettypush.core.common.Util.isBlank;

public class MsgHandlerRegistry {

    private static Logger logger = LoggerFactory.getLogger(DefaultServerhandler.class);

    private Map<String, Map<String, MsgHandler>> registry;

    public MsgHandlerRegistry() {
        registry = new HashMap<>();
    }

    public void addHandler(MsgHandler handler) {
        checkBlank(handler.group(), "group");
        checkBlank(handler.type(), "type");
        Map<String, MsgHandler> groupMap = registry.get(handler.group());
        if (groupMap == null) {
            groupMap = new HashMap<>();
            registry.put(handler.group(), groupMap);
        }
        groupMap.put(handler.type(), handler);
    }

    public void distribute(Object raw, Msg msg) {
        if (isBlank(msg.getGroup()) || isBlank(msg.getType())) {
            logger.error("消息处理失败, 缺少group或者type, msg[" + JsonUtil.toJson(msg) + "]");
            return;
        }
        Map<String, MsgHandler> groupMap = registry.get(msg.getGroup());
        if (groupMap == null) {
            logger.error("消息处理失败, 缺少对应group的hanlder, msg[" + JsonUtil.toJson(msg) + "]");
            return;
        }
        MsgHandler handler = groupMap.get(msg.getType());
        if (handler == null) {
            logger.error("消息处理失败, 缺少对应type的handler, msg[" + JsonUtil.toJson(msg) + "]");
            return;
        }
        Class type = getHandlerType(handler);
        try {
            handler.handle(msg, type == null ? msg.getBody() : JsonUtil.toObject(msg.getBody(), type),
                    new Identity(msg.getIdentity(), msg.getGroup()));
        } catch (Exception e) {
            throw new BizException("消息处理失败", e);
        }
    }

    private Class getHandlerType(MsgHandler handler) {
        Type[] interfaces = handler.getClass().getGenericInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return null;
        }
        if (interfaces[0] instanceof ParameterizedType == false) {
            return null;
        }
        Type[] args = ((ParameterizedType) interfaces[0]).getActualTypeArguments();
        if (args == null || args.length == 0) {
            return null;
        }
        return (Class) args[0];
    }

}
