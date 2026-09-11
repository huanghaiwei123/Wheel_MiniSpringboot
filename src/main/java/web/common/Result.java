package web.common;

public class Result<T> {
    private Integer code;
    private String msg;
    private Object data;
    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(0,"success",data);
    }
}
