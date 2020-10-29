package com.serviceCall;

import com.EchoService;
import com.annotation.RpcCall;
import org.springframework.stereotype.Component;

@Component
public class EchoServiceCall {

    @RpcCall
    public EchoService echoService;

}
