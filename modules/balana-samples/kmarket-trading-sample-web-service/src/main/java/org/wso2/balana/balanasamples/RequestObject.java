package org.wso2.balana.balanasamples;
/**
 * This is request object to generate XACML Request upon to class attributes.
 * */
public class RequestObject {

    /**
     * Example : bob, alice, peter
     * */
    private String username;
    /**
     * Example : Food, Drink, Fruit, Liquor, Medicine
     * */
    private String productName;
    /**
     * Example : 1,10,20
     * */
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
