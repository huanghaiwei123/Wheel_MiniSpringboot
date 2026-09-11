package web.mvc.annotation.type;

import web.mvc.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * 通用路由注解:可标在类上(作为前缀)和方法上
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";               // url 路径
    RequestMethod[] method() default {};     // 支持的请求方式,空数组 = 任意方式
}