package remoting.dto;


import enumeration.RpcErrorMessageEnum;
import enumeration.RpcMessageTypeEnum;
import enumeration.RpcResponseCode;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RpcMessageChecker {
    private static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker(){};

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if(rpcResponse==null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if(!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if(rpcResponse.getCode()==null||!rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
