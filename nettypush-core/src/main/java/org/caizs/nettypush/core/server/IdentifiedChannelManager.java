package org.caizs.nettypush.core.server;

import org.caizs.nettypush.core.base.ChannelManager;
import org.caizs.nettypush.core.base.Identity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdentifiedChannelManager {

    //group->pool
    private ConcurrentMap<String, ChannelManager<IdentifiedChannel>> pool;
    private ConcurrentMap<ChannelId, Identity> idPool;

    public IdentifiedChannelManager() {
        pool = new ConcurrentHashMap<>();
        idPool = new ConcurrentHashMap<>();
    }

    public IdentifiedChannel get(Identity identity) {
        ChannelManager<IdentifiedChannel> groupPool = pool.get(identity.getGroup());
        if (groupPool == null) {
            return null;
        }
        return groupPool.get(identity.toId());
    }

    public boolean add(IdentifiedChannel channel) {
        ChannelManager<IdentifiedChannel> groupPool = pool.get(channel.getIdentity().getGroup());
        if (groupPool == null) {
            groupPool = new ChannelManager<>();
            pool.putIfAbsent(channel.getIdentity().getGroup(), groupPool);
        }
        boolean result = groupPool.add(channel);
        if (result) {
            idPool.putIfAbsent(channel.getChannel().id(), channel.getIdentity());
        }
        return result;
    }

    public void close(ChannelHandlerContext ctx) {
        Identity identity = idPool.get(ctx.channel().id());
        if (identity != null) {
            remove(ctx.channel().id(), identity);
        }
        ctx.close();
    }

    private void remove(ChannelId channelId, Identity identity) {
        ChannelManager<IdentifiedChannel> groupPool = pool.get(identity.getGroup());
        if (groupPool == null) {
            return;
        }
        groupPool.remove(channelId);
    }

    public Collection<IdentifiedChannel> collect(Identity identity) {
        ChannelManager<IdentifiedChannel> groupPool = pool.get(identity.getGroup());
        if (groupPool == null) {
            return new ArrayList<>();
        }
        return groupPool.collect();
    }
}
