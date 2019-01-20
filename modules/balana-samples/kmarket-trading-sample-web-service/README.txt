How to run sample
=================

1. Run application on Server.
2. Send http post to http://localhost:8080/evaluate.

Sample Request :
{
	"username":"bob",
	"productName":"Fruit",
	"numberOfProducts":10
}

Sample Response :
{
    "message": "\nbob is NOT authorized to perform this purchase\nAdvice :  You are not allowed to do more than $100 purchase\n    from KMarket on-line trading system\n\n"
}

More details - http://xacmlinfo.org/2012/08/16/xacml-sample-for-on-line-trading-application/


