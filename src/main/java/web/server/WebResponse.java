package web.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class WebResponse {
    private static ObjectMapper mapper = new ObjectMapper();
    public static FullHttpResponse ok(Object content) {
        try {
            String json = mapper.writeValueAsString(content);
            DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(json.getBytes(StandardCharsets.UTF_8)));
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            res.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(json.getBytes(StandardCharsets.UTF_8).length));
            return res;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
