package org.wso2.balana.balanasamples;
/**
 * This is response object to map XACML Response for client users.
 * */
public class ResponseObject {
    /**
     * Generic message for the client.
     * Example : "Bob is NOT authorized to perform this purchase."
     * */
    private String message;

    public ResponseObject(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
