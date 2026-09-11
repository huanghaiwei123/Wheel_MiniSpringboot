package web.mvc.annotation.method;

import web.mvc.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * GET 请求便捷注解,只标在方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String value() default "";
    RequestMethod RequestMethod() default RequestMethod.GET;
}