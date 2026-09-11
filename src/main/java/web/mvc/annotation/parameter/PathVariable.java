package web.mvc.annotation.parameter;

import java.lang.annotation.*;

/**
 * 从路径中取值的注解,如 /user/{id} → id=123
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value();   // 路径参数名
}