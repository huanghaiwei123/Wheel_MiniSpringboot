package web.mvc;

import java.util.Map;

/**
 * 路由匹配结果:命中的执行器 + 解析出的路径参数
 */
public class MappingResult {
    private final HandleExecution execution;
    private final Map<String, String> pathParams;   // 路径参数名 -> 值,如 {id: "123"}

    public MappingResult(HandleExecution execution, Map<String, String> pathParams) {
        this.execution = execution;
        this.pathParams = pathParams;
    }

    public HandleExecution getExecution() {
        return execution;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }
}