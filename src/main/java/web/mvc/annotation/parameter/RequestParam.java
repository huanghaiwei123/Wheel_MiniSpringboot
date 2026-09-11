package web.mvc.annotation.parameter;

import java.lang.annotation.*;

/**
 * 从 query 参数取值的注解,如 /hello2?name=xx
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value();   // 参数名
}