package pojo;

public class JdkDynamicProxyTest {

    public static void main(String[] args) throws Exception {
        /*log proxy = new JdkDynamicProxy().getInstance(logProcess.class);
        proxy.startLog();*/
        log instance = new CglibProxy().getInstance(logProcess.class);
        instance.startLog();
    }
}
