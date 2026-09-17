package web.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import web.mvc.exception.NotFoundException;
import web.mvc.Dispatcher;
import web.mvc.annotation.RequestMethod;
import web.mvc.exception.BadRequestException;

import static io.netty.handler.codec.http.HttpUtil.getContentLength;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * 核心处理http请求的类，分发到 Dispatcher 执行 Controller
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String FAVICON_ICO = "/favicon.ico";   //网站图标,浏览器会自动请求,直接忽略
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    private final Dispatcher dispatcher;

    public HttpRequestHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, FullHttpRequest req) throws Exception {
        if (FAVICON_ICO.equals(req.uri())) {
            return;   //浏览器自动请求的网站图标,直接忽略
        }
        if (is100ContinueExpected(req)) {   //100-Continue机制，需要服务器同意后才能发送数据
            if (getContentLength(req, 0) > 512 * 1024) {
                chc.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE))
                        .addListener(ChannelFutureListener.CLOSE);
                return;
            } else {
                chc.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
        }
        //分发请求到对应的Controller方法,并把结果转成JSON响应
        FullHttpResponse response;
        try {
            Object result = dispatcher.dispatch(req, RequestMethod.valueOf(req.method().name()));
            response = WebResponse.ok(result);
        } catch (NotFoundException e) {
            //路由没找到 → 返回真正的 404 状态码
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,
                    Unpooled.copiedBuffer(e.getMessage(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        }catch(BadRequestException e) {
            //请求异常->返回400
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(e.getMessage(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        }catch (Exception e) {
            //其他异常 → 500 内部错误
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.copiedBuffer(e.toString(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        }
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (!keepAlive) {
            chc.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            chc.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}