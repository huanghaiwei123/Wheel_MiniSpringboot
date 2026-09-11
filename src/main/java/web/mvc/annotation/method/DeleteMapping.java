package web.mvc.annotation.method;

import web.mvc.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GET 请求便捷注解,只标在方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteMapping {
    String value() default "";
    RequestMethod RequestMethod() default RequestMethod.DELETE;
}