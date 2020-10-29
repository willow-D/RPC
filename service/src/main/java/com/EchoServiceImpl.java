package com;

import com.annotation.RpcService;
import com.entity.TestUser;


@RpcService
public class EchoServiceImpl implements EchoService {

    public EchoServiceImpl(){
        System.out.println("echo 实例化");
    }

    @Override
    public TestUser echo(TestUser user) {
        user.age = "19";
        return  user;
    }
}
