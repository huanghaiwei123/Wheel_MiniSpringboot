package test.aspect;

import web.aop.annotation.method.After;
import web.aop.annotation.method.Before;
import web.aop.annotation.type.Aspect;
@Aspect
public class AspectTest {
    @Before("hello")
    public void before() {
        System.out.println("before");
    }
    @After("hello")
    public void after() {
        System.out.println("after");
    }
}
