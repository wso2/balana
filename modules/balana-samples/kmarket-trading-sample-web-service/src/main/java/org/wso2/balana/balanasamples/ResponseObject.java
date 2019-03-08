package org.wso2.balana.balanasamples;

public class ResponseObject {

    private String message;

    public ResponseObject(String message){

        this.message = message;
    }

    public String getMessage(){

        return this.message;
    }
}
