package web.mvc.exception;

public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String msg;
    public BadRequestException(){
        super();
    }
    public BadRequestException(String msg) {
        super(msg);
    }
}
