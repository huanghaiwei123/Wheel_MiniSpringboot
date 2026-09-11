package web.mvc;

/**
 * 路由未找到异常,用于区分 404 和其他 500 错误
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}