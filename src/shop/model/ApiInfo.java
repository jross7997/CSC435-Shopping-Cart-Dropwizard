package shop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiInfo {

    @JsonProperty
    private String operator;
    @JsonProperty
    private String URI;
    @JsonProperty
    private String description;

    public ApiInfo(String o, String u,String d){
        operator = o;
        URI = u;
        description = d;
    }

    public String getOperator(){
        return operator;
    }

    public String getDescription() {
        return description;
    }

    public String getURI() {
        return URI;
    }

}
