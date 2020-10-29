package com.remoting.packet;

import com.remoting.constants.RpcConstants;

public class HeartBeatPongPacket extends Packet  {
    @Override
    public Byte getCommand() {
        return RpcConstants.HEARTBEAT_PONG;
    }
}
