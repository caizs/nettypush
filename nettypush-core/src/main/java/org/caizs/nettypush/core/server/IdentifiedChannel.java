package org.caizs.nettypush.core.server;

import org.caizs.nettypush.core.base.ChannelWrapper;
import org.caizs.nettypush.core.base.Identity;
import io.netty.channel.Channel;

public class IdentifiedChannel extends ChannelWrapper {

    private Identity identity;

    public IdentifiedChannel(Identity identity, Channel channel) {
        super(channel);
        this.identity = identity;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
