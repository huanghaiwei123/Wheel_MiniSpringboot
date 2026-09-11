package web.mvc.annotation;

public enum RequestMethod {
    GET("GET")
    , POST("POST")
    , PUT("PUT")
    , DELETE("DELETE");
    private String value;
    RequestMethod(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}