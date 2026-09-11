package web.mvc.annotation.method;

import web.mvc.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * POST 请求便捷注解,只标在方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {
    String value() default "";
    RequestMethod RequestMethod() default RequestMethod.POST;
}