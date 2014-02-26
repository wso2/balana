How to run sample
=================

1. You need to install java jdk1.6.X or higher
2. Execute "run" script

There are three XACML policies to show how new rule combining algorithm works. 

If user name is given as "bob". Policy with three  "Permit" rules  and two "Deny" rules would be applied. Therefore fine result would be "Permit"

If user name is given as "alice". Policy with two  "Permit" rules  and three "Deny" rules would be applied. Therefore fine result would be "Deny"

If user name is given as "peter". Policy with two  "Permit" rules and two "Deny"  rules and one "Not Applicable" rule would be applied. Therefore fine result would be "Deny"

More details - http://xacmlinfo.com/2012/12/18/extending-balana-custom-combining-algorithms/

