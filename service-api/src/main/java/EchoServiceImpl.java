import entity.TestUser;

public class EchoServiceImpl implements EchoService {
    @Override
    public TestUser echo(TestUser user) {
        user.age = "19";
        return  user;
    }
}
