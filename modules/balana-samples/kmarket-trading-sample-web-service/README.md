# Balana Sample - Web Service for Kmarket Trading Sample
It is a simple web service project for  [Kmarket Trading Sample](https://github.com/wso2/balana/tree/master/modules/balana-samples/kmarket-trading-sample) - Another sample in balana repository.
## How to run sample
1. Run application on Server.
2. Send http post to http://localhost:8080/evaluate
### Request Object
```
Sample Request :
{
	"username":"bob",
	"productName":"Fruit",
	"numberOfProducts":10
}
```
### Response Object
```
Sample Response :
{
    "message": "\nbob is NOT authorized to perform this purchase\nAdvice :  You are not allowed to do more than $100 purchase\n    from KMarket on-line trading system\n\n"
}
```
## More Details

* http://xacmlinfo.org/2012/08/16/xacml-sample-for-on-line-trading-application/
* https://github.com/wso2/balana/tree/master/modules/balana-samples/kmarket-trading-sample
