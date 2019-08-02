package top.pinyougou.manager.SystemLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import top.takefly.core.pojo.log.SysLog;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Date;

@Aspect
@Component
public class OptionLog {

    @Autowired
    private HttpServletRequest request;

    /**
     * 当前访问的类
     */
    private Class targetClazz;

    /**
     * 当前访问的方法
     */
    private Method visitMethod;

    /**
     * 访问的时间
     */
    private Date visitTime;


    @Before("execution(* top.pinyougou.manager.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws Exception {
        //获取当前访问的时间
        visitTime = new Date();
        //获取当前操作的类
        targetClazz = jp.getTarget().getClass();
        //获取当前访问的方法的名称
        String methodName = jp.getSignature().getName();
        //获取携带的参数
        Object[] args = jp.getArgs();
        if (args == null) {
            visitMethod = targetClazz.getMethod(methodName);
        } else {
            Class[] argsClass = new Class[args.length];
            for (int i = 0; i < argsClass.length; i++) {
                argsClass[i] = args[i].getClass();
            }
            visitMethod = targetClazz.getMethod(methodName, argsClass);
        }
    }

    @After("execution(* top.pinyougou.manager.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {
        //获取执行花费的时间
        Long spendTime = System.currentTimeMillis() - visitTime.getTime();
        //获取url
        if (targetClazz != null && targetClazz != OptionLog.class && visitMethod != null) {
            RequestMapping ClassAnnotation = (RequestMapping) targetClazz.getAnnotation(RequestMapping.class);
            if (ClassAnnotation != null) {
                RequestMapping methodAnnotation = visitMethod.getAnnotation(RequestMapping.class);
                //获取url
                if (methodAnnotation != null) {
                    StringBuilder sb = new StringBuilder("url:");
                    if (ClassAnnotation.value().length > 0 && methodAnnotation.value().length > 0) {
                        sb.append(ClassAnnotation.value()[0])
                                .append(methodAnnotation.value()[0]);
                    }
                    //获取当前用户姓名
                    String userName = SecurityContextHolder.getContext().getAuthentication().getName();

                    //获取访问的ip
                    String ip = request.getRemoteAddr();
                    //封装成对象
                    SysLog sysLog = new SysLog();
                    sysLog.setMethod("[类名] " + targetClazz.getName() + "[方法名] " + visitMethod.getName());
                    sysLog.setIp(ip);
                    sysLog.setUrl(sb.toString());
                    sysLog.setVisitTime(visitTime);
                    sysLog.setExecutionTime(spendTime.intValue());

                    System.out.println(sysLog);
                }
            }
        }
    }

}
