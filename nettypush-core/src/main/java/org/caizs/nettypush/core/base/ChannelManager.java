package org.caizs.nettypush.core.base;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChannelManager<T extends ChannelWrapper> {

    private ConcurrentMap<ChannelId, T> pool;

    private ConcurrentMap<String, ChannelId> idPool;

    public ChannelManager() {
        pool = new ConcurrentHashMap<>();
        idPool = new ConcurrentHashMap<>();
    }

    public T get(ChannelId id) {
        return pool.get(id);
    }

    public T get(String identityId) {
        ChannelId channelId = idPool.get(identityId);
        if (channelId == null) {
            return null;
        }
        return pool.get(channelId);
    }

    public boolean add(T channel) {
        String identityId = channel.getIdentity().toId();
        ChannelId existChannelId = idPool.get(identityId);
        if (existChannelId != null) {
            T existChannel = pool.get(existChannelId);
            if (existChannel != null && existChannel.getChannel().isActive()) {
                return false;//如果pool中已有有效连接，则立即返回；否则覆盖掉失效连接引用
            }
        }
        ChannelId channelId = channel.getChannel().id();
        idPool.putIfAbsent(identityId, channelId);
        pool.putIfAbsent(channelId, channel);
        return true;
    }

    public void close(ChannelHandlerContext ctx) {
        remove(ctx.channel().id());
        ctx.close();
    }

    public void remove(ChannelId id) {
        T channel = get(id);
        if (channel == null) {
            return;
        }
        idPool.remove(channel.getIdentity().toId());
        pool.remove(id);
    }

    public Collection<T> collect() {
        return pool.values();
    }
}
