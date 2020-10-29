package com.remoting.transport.netty.handler;

import com.remoting.constants.RpcConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


// 基于长度的拆包器
public class Spliter extends LengthFieldBasedFrameDecoder {

    private static final int LENGTH_FIELD_OFFSET = 6;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //读取前4个字节的数据，检测魔数是否匹配
        if(in.getInt(in.readerIndex())!= RpcConstants.MAGIC_NUMBER){
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
