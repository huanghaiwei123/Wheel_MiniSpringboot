package web.mvc;

import io.netty.handler.codec.http.FullHttpRequest;
import web.mvc.annotation.RequestMethod;
import web.mvc.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求进来分发:查路由 → 解析参数 → 反射执行
 */
public class Dispatcher {
    private HandleMapping handleMapping;
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

        //调用参数解析器进行参数解析
        Object[] args = new ArgumentResolver(req, result, queryParams,requestMethod).resolver();

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