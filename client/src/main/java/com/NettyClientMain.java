package com;

import com.entity.TestUser;
import com.serviceCall.EchoServiceCall;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@Slf4j
@ComponentScan("com")
public class NettyClientMain {
    @SneakyThrows
    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(NettyClientMain.class);
        EchoServiceCall echoServiceCall = (EchoServiceCall) ctx.getBean("echoServiceCall");

        long start = System.currentTimeMillis();
        for(int i=0;i<10000;i++){
            TestUser user = echoServiceCall.echoService.echo(new TestUser());
            log.info(user.age);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
