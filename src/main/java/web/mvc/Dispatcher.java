package web.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import web.mvc.annotation.RequestMethod;
import web.mvc.annotation.parameter.PathVariable;
import web.mvc.annotation.parameter.RequestBody;
import web.mvc.annotation.parameter.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求进来分发:查路由 → 解析参数 → 反射执行
 */
public class Dispatcher {
    private HandleMapping handleMapping;
    private final ObjectMapper objectMapper=new ObjectMapper();
    public Dispatcher(HandleMapping handleMapping) {
        this.handleMapping = handleMapping;
    }

    public Object dispatch(FullHttpRequest req, RequestMethod requestMethod) {
        String uri = req.uri();
        //1. 拆出 path(去掉 query)和 query 参数
        String path = uri.indexOf("?") >= 0 ? uri.substring(0, uri.indexOf("?")) : uri;
        Map<String, String> queryParams = parseQuery(uri);

        //2. 查路由(精确 or 动态)
        MappingResult result = handleMapping.get(path, requestMethod);
        if (result == null) {
            throw new NotFoundException("未找到路由: " + uri);
        }

        //3. 遍历方法参数,按注解解析值,组装参数数组
        Method method = result.getExecution().getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(RequestParam.class)) {
                String name = p.getAnnotation(RequestParam.class).value();
                args[i] = TypeConverter.convert(p.getType(), queryParams.get(name));
            } else if (p.isAnnotationPresent(PathVariable.class)) {
                String name = p.getAnnotation(PathVariable.class).value();
                args[i] = TypeConverter.convert(p.getType(), result.getPathParams().get(name));
            }else if(p.isAnnotationPresent(RequestBody.class)) {
                try {
                    //req.content()返回的是bytebuf这种二进制数据，要转为字符串必须指定编码类型
                    args[i] = objectMapper.readValue(req.content().toString(StandardCharsets.UTF_8), p.getType());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                args[i] = null;   //没有注解的参数暂不支持,填 null
            }
        }

        //4. 反射执行
        return result.getExecution().execute(args);
    }

    /**
     * 解析 query 字符串:?name=xx&age=18 → {name:"xx", age:"18"}
     */
    private Map<String, String> parseQuery(String uri) {
        Map<String, String> map = new HashMap<>();
        int idx = uri.indexOf("?");
        if (idx < 0) {
            return map;
        }
        String query = uri.substring(idx + 1);
        for (String pair : query.split("&")) {
            int eq = pair.indexOf("=");
            if (eq > 0) {
                map.put(pair.substring(0, eq), pair.substring(eq + 1));
            }
        }
        return map;
    }
}