package pojo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;

public class JdkDynamicProxy implements InvocationHandler {

    private Object target = null;

    public <T> T getInstance(Class<T> targetClass) throws Exception{
        target = targetClass.newInstance();
        //返回代理对象
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader() ,
                targetClass.getInterfaces() , this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        startTime();
        Object invoke = method.invoke(target, args);
        endTime();
        return invoke;
    }

    public void startTime(){
        System.out.println("程序开始时间:"+LocalDate.now());
    }

    public void endTime(){
        System.out.println("程序解释时间:"+LocalDate.now());
    }
}
