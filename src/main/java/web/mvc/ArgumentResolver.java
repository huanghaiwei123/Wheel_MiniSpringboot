package web.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import web.mvc.annotation.RequestMethod;
import web.mvc.exception.BadRequestException;
import web.mvc.annotation.parameter.PathVariable;
import web.mvc.annotation.parameter.RequestBody;
import web.mvc.annotation.parameter.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * 用来做dispatcher的参数解析
 */
public class ArgumentResolver {
    private MappingResult result;
    private Map<String, String> queryParams;
    private FullHttpRequest req;
    private RequestMethod requestMethod;
    private final ObjectMapper objectMapper=new ObjectMapper();
    public ArgumentResolver(FullHttpRequest req,MappingResult result, Map<String, String> queryParams, RequestMethod requestMethod) {
        this.result = result;
        this.queryParams = queryParams;
        this.req = req;
        this.requestMethod = requestMethod;
    }

    public Object[] resolver(){
        //3. 遍历方法参数,按注解解析值,组装参数数组
        Method method = result.getExecution().getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(RequestParam.class)) {
                String name = p.getAnnotation(RequestParam.class).value();
                boolean required = p.getAnnotation(RequestParam.class).required();
                String queryParam = queryParams.get(name);
                if (required && queryParam == null) {
                    throw new BadRequestException("Missing query parameter " + name);
                }
                try {
                    args[i] = TypeConverter.convert(p.getType(), queryParam);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new BadRequestException("捕获类型转换失败");
                }
            } else if (p.isAnnotationPresent(PathVariable.class)) {
                String name = p.getAnnotation(PathVariable.class).value();
                if(result.getPathParams().get(name)==null){
                    throw new BadRequestException("Missing path variable " + name);
                }
                try {
                    args[i] = TypeConverter.convert(p.getType(), result.getPathParams().get(name));
                }catch (Exception e){
                    e.printStackTrace();
                    throw new BadRequestException("捕获类型转换失败");
                }

            }else if(p.isAnnotationPresent(RequestBody.class)) {
                try {
                    //req.content()返回的是请求体，并且以bytebuf这种二进制数据，要转为字符串必须指定编码类型
                    args[i] = objectMapper.readValue(req.content().toString(StandardCharsets.UTF_8), p.getType());
                } catch(JsonProcessingException e) {
                    e.printStackTrace();
                    throw new BadRequestException("请求体json格式错误");
                }
            }
            else {
                args[i] = null;   //没有注解的参数暂不支持,填 null
            }
        }
        return args;
    }


}
