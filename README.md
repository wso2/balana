WSO2 Balana Implementation
==========================

---

|  Branch | Build Status |
| :------------ |:-------------
| master      | [![Build Status](https://wso2.org/jenkins/buildStatus/icon?job=forked-dependencies/wso2-balana)](https://wso2.org/jenkins/job/forked-dependencies/wso2-balana) |

---

## Welcome to WSO2 Balana
Balana is WSO2's open source implementation of the XACML specification building on Sun's XACML Implementation[1]. As the name suggests Balana(the fortress) is a powerful entitlement engine to externalize authorization from your applications. With it's modular architecture you can easily develop a fully fledged entitlement solution in no time.

[1] http://sunxacml.sourceforge.net/

[2] http://sunxacml.sourceforge.net/license.txt

[3] http://www.apache.org/licenses/LICENSE-2.0

###### Specifications Supported by Balana
Balana supports the XACML 3.0, 2.0, 1.1 and 1.0 specifications.

### Installation
Add the following dependency to your pom.
```xml
<dependency>
    <groupId>org.wso2.balana</groupId>
    <artifactId>org.wso2.balana</artifactId>
    <version>1.1.12</version>
 </dependency>
```

### Getting Started
You can easily create a default instance of Balana with a file based policy repository as follows.

```java
private static Balana balana;

private static void initBalana() {

        try{
            // using file based policy repository. so set the policy location as system property
            String policyLocation = (new File(".")).getCanonicalPath() + File.separator + "resources";
            System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policyLocation);
        } catch (IOException e) {
            System.err.println("Can not locate policy repository");
        }
        // create default instance of Balana
        balana = Balana.getInstance();
    }
```



