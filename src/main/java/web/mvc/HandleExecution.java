package web.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandleExecution {
    private final Object controller;
    private final Method method;
    public HandleExecution(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }
    public Method getMethod() {
        return method;
    }
    public  Object execute(Object... args){
        try {
            return method.invoke(controller, args);
        } catch (IllegalAccessException  | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
