package pojo;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.time.LocalDate;

public class CglibProxy implements MethodInterceptor {


    private Object target = null;

    private Enhancer enhance = new Enhancer();

    public <T> T getInstance(Class<T> targetClass) throws Exception{
        this.target = targetClass.newInstance();
        //设置要代理的类为父类
        enhance.setSuperclass(targetClass);
        //回响
        enhance.setCallback(this);
        return (T) enhance.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        startTime();
        Object result = methodProxy.invokeSuper(o, objects);
        endTime();
        return result;
    }

    public void startTime(){
        System.out.println("程序开始时间:"+ LocalDate.now());
    }

    public void endTime(){
        System.out.println("程序解释时间:"+LocalDate.now());
    }
}
