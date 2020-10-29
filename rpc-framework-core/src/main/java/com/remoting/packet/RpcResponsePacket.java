package com.remoting.packet;

import com.enumeration.RpcResponseCode;
import com.remoting.constants.RpcConstants;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponsePacket<T> extends Packet {

    private static final long serialVersionUID = 1905122041950251207L;

    private String requestId;
    private Integer code;
    private String message;
    private T data;

    public static <T> RpcResponsePacket<T> success(T data, String requestId){
        RpcResponsePacket<T> response = new RpcResponsePacket<>();
        response.setCode(RpcResponseCode.SUCCESS.getCode());
        response.setMessage(RpcResponseCode.SUCCESS.getMeaages());
        response.setRequestId(requestId);
        if(data!=null){
            response.setData(data);
        }
        return response;
    }
    public static <T> RpcResponsePacket<T> fial(RpcResponseCode rpcResponseCode){
        RpcResponsePacket<T> response = new RpcResponsePacket<>();
        response.setCode(rpcResponseCode.getCode());
        response.setMessage(rpcResponseCode.getMeaages());
        return response;
    }

    @Override
    public Byte getCommand() {
        return RpcConstants.RESPONSE;
    }
}
