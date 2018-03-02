package org.caizs.nettypush.core.codec;

import org.caizs.nettypush.core.common.JsonUtil;
import org.caizs.nettypush.core.msg.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class DelimiterJsonDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() < Protocol.HEAD_BYTE_LENGTH) {
            return;
        }
        buf.markReaderIndex();

        byte ver = buf.readByte();
        byte type = buf.readByte();
        int length = buf.readInt();
        if (buf.readableBytes() < length) {
            buf.resetReaderIndex();
            return;
        }
        Msg msg = readObject(length, buf);
        out.add(new MsgProtocol(msg, ver, type, length));
    }

    private Msg readObject(int length, ByteBuf byteBuf) {
        if (length > 0) {
            byte[] decoded = new byte[length];
            byteBuf.readBytes(decoded);
            return JsonUtil.toObject(decoded, Msg.class);
        }
        return null;
    }
}
