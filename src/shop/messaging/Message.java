package shop.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty
    private String message;
    @JsonProperty
    private Object data = null;

    public Message(String m, Object o){
        message = m;
        data= o;
    }
    public Message(String m){
        message = m;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
