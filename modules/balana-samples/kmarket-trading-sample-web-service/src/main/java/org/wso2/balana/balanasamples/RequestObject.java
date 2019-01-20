package org.wso2.balana.balanasamples;

public class RequestObject {

    private String username;
    private String productName;
    private int numberOfProducts;


    public RequestObject(String username, String productName, int numberOfProducts){

        this.username = username;
        this.productName = productName;
        this.numberOfProducts = numberOfProducts;
    }


    public String getUsername(){
        return this.username;
    }

    public int getNumberOfProducts(){
        return this.numberOfProducts;
    }

    public String getProductName(){
        return this.productName;
    }

}
