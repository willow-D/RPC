package com.remoting.packet;

import com.remoting.constants.RpcConstants;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcRequestPacket extends Packet   {

    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    @Override
    public Byte getCommand() {
        return RpcConstants.REQUEST;
    }
}
