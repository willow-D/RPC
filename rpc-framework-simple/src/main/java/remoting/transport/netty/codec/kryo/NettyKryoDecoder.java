package remoting.transport.netty.codec.kryo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import serialize.kyro.Serializer;

import java.util.List;
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder  extends ByteToMessageDecoder {

    private static final int BODY_LENGTH = 4;

    private Serializer serializer;
    private Class<?> genericClass;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()>=BODY_LENGTH){
            in.markReaderIndex();
            int dataLength = in.readInt();
            if(dataLength < 0 || in.readableBytes() < 0){
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            if(in.readableBytes() < dataLength){
                in.resetReaderIndex();
                return;
            }
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            Object object =serializer.deserialize(body, genericClass);
            out.add(object);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
