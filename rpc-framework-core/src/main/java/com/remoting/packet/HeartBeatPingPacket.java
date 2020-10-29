package com.remoting.packet;

import com.remoting.constants.RpcConstants;

public class HeartBeatPingPacket extends Packet {
    @Override
    public Byte getCommand() {
        return RpcConstants.HEARTBEAT_PING;
    }
}
