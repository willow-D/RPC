import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl  implements HelloService{
    @Override
    public String hello(Hello hello) {
        log.info("helloServiceImpl 收到：{}", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回：{}", result);
        return result;
    }
}
