package com.remoting.transport.netty.codec;

import com.remoting.packet.*;
import com.serialize.kyro.KryoSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static com.remoting.constants.RpcConstants.*;

public class PacketCodec {

    public static final int MAGIC_NUMBER = 0x12345678;

    public static final PacketCodec INSTANCE = new PacketCodec();

    private static Map<Byte, Class<? extends Packet>> packetTypeMap;

    private static KryoSerializer kryoSerializer;


    private PacketCodec(){
        packetTypeMap = new HashMap<>(){{
           put(REQUEST, RpcRequestPacket.class);
           put(RESPONSE, RpcResponsePacket.class);
           put(HEARTBEAT_PING, HeartBeatPingPacket.class);
           put(HEARTBEAT_PONG, HeartBeatPongPacket.class);
        }};

        kryoSerializer = new KryoSerializer();
    }

    private Class<? extends Packet> getRequestType(byte command){
        return packetTypeMap.get(command);
    }

    public void encode(ByteBuf byteBuf, Packet packet){
        byte[] bytes = kryoSerializer.serialize(packet);

        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public Packet decode(ByteBuf byteBuf){

        byteBuf.skipBytes(4);

        byteBuf.skipBytes(1);

        byte command = byteBuf.readByte();

        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];

        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);

        if(requestType!=null){
            return kryoSerializer.deserialize(bytes, requestType);
        }
        return null;
    }







}
