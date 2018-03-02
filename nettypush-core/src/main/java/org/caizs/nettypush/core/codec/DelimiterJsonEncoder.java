package org.caizs.nettypush.core.codec;

import org.caizs.nettypush.core.common.BizException;
import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.base.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class DelimiterJsonEncoder extends MessageToMessageEncoder<MsgProtocol> {

    @Override protected void encode(ChannelHandlerContext ctx, MsgProtocol msg, List<Object> out) throws Exception {

        byte[] data = JsonUtil.toJsonByte(msg.getMsg());
        if (data.length >= (Config.maxFrameLength - Protocol.HEAD_BYTE_LENGTH)) {
            throw new BizException("消息体不能超过" + Config.maxFrameLength + "个字节");
        }

        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(msg.getVer());
        buf.writeByte(msg.getType());
        buf.writeInt(data.length);
        if (data.length > 0) {
            buf.writeBytes(data);
        }
        buf.writeBytes(Protocol.DELIMITER_BYTES);//自定义分隔符
        out.add(buf);
    }


}
