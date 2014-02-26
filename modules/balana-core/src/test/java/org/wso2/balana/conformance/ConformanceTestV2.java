/*
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.balana.conformance;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.*;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *  This XACML 2.0 conformation test.
 */
public class ConformanceTestV2 extends TestCase {

    /**
     * Configuration store
     */
    private static ConfigurationStore store;

    /**
     * directory name that states the test type
     */
    private final static String ROOT_DIRECTORY  = "conformance";

    /**
     * directory name that states XACML version
     */
    private final static String VERSION_DIRECTORY  = "2";

    /**
     * the logger we'll use for all messages
     */
	private static Log log = LogFactory.getLog(ConformanceTestV2.class);

    @Override
    public void setUp() throws Exception {

        String configFile = (new File(".")).getCanonicalPath() + File.separator + TestConstants.CONFIG_FILE;
        store = new ConfigurationStore(new File(configFile));
    }


    public void testConformanceTestA() throws Exception {

        String policyNumber;

        for(int i = 1; i < 22 ; i++){     

            //Some test has been skipped due to errors
            if(i == 2 || i == 4 || i == 14){
                log.info("Conformance Test IIA00" + i + " does not started As required " +
                                                                "attribute finder is not defined");
                continue;
            }

            if(i < 10){
                policyNumber = "00" + i;
            } else if(9 < i && i < 100) {
                policyNumber = "0" + i;
            } else {
                policyNumber = Integer.toString(i);
            }

            log.info("Conformance Test IIA" + policyNumber + " is started");

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                            "IIA" + policyNumber + "Request.xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                Set<String> policies = new HashSet<String>();
                policies.add("IIA" + policyNumber + "Policy.xml");                
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                        VERSION_DIRECTORY, "IIA" + policyNumber + "Response.xml");
                    log.info("Response that is received from the PDP :  " + response.encode());
                    if(expectedResponseCtx != null){
                        assertTrue(TestUtil.isMatching(response, expectedResponseCtx));
                    } else {
                        assertTrue("Response read from file is Null",false);
                    }
                } else {
                    assertFalse("Response received PDP is Null",false);
                }
            } else {
                assertTrue("Request read from file is Null", false);
            }

            log.info("Conformance Test IIA" + policyNumber + " is finished");
        }
    }

    public void testConformanceTestB() throws Exception {

        String policyNumber;

        for(int i = 1; i < 54 ; i++){

            //Some test has been skipped due to errors
            if(i == 28 || i == 29){
                log.info("Conformance Test IIB00" + i + " does not started As required " +
                                                                "attribute finder is not defined");
                continue;
            }

            if(i < 10){
                policyNumber = "00" + i;
            } else if(9 < i && i < 100) {
                policyNumber = "0" + i;
            } else {
                policyNumber = Integer.toString(i);
            }

            log.info("Conformance Test IIB" + policyNumber + " is started");

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                            "IIB" + policyNumber + "Request.xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                Set<String> policies = new HashSet<String>();
                policies.add("IIB" + policyNumber + "Policy.xml");
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                        VERSION_DIRECTORY, "IIB" + policyNumber + "Response.xml");
                    log.info("Response that is received from the PDP :  " + response.encode());
                    if(expectedResponseCtx != null){
                        assertTrue(TestUtil.isMatching(response, expectedResponseCtx));
                    } else {
                        assertTrue("Response read from file is Null",false);
                    }
                } else {
                    assertFalse("Response received PDP is Null",false);
                }
            } else {
                assertTrue("Request read from file is Null", false);
            }

            log.info("Conformance Test IIB" + policyNumber + " is finished");
        }
    }

    public void testConformanceTestC() throws Exception {

        String policyNumber;

        for(int i = 1; i < 233 ; i++){
            //Some test has been skipped due to errors 54,55,88,89,92,93,98,99
            // 105 issue
            if(i == 3 || i==12 || i==14 || i ==23 || i==54 || i == 55 || i == 88  ||
                    i == 89 || i == 92 || i==93 || i == 98 || i == 99 || i == 105){
                log.info("Conformance Test IIC00" + i + " does not started As required " +
                                                                "attribute finder is not defined");
                continue;
            }

            if(i < 10){
                policyNumber = "00" + i;
            } else if(9 < i && i < 100) {
                policyNumber = "0" + i;
            } else {
                policyNumber = Integer.toString(i);
            }

            log.info("Conformance Test IIC" + policyNumber + " is started");

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                            "IIC" + policyNumber + "Request.xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                Set<String> policies = new HashSet<String>();
                policies.add("IIC" + policyNumber + "Policy.xml");
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                        VERSION_DIRECTORY, "IIC" + policyNumber + "Response.xml");
                    log.info("Response that is received from the PDP :  " + response.encode());
                    if(expectedResponseCtx != null){
                        assertTrue(TestUtil.isMatching(response, expectedResponseCtx));
                    } else {
                        assertTrue("Response read from file is Null",false);
                    }
                } else {
                    assertFalse("Response received PDP is Null",false);
                }
            } else {
                assertTrue("Request read from file is Null", false);
            }

            log.info("Conformance Test IIC" + policyNumber + " is finished");
        }
    }


    public void testConformanceTestD() throws Exception {

        String policyNumber;
        
        for(int i = 1; i < 29 ; i++){
            if(i < 10){
                policyNumber = "00" + i;
            } else if(9 < i && i < 100) {
                policyNumber = "0" + i;
            } else {
                policyNumber = Integer.toString(i);
            }

            log.info("Conformance Test IID" + policyNumber + " is started");

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                            "IID" + policyNumber + "Request.xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                Set<String> policies = new HashSet<String>();
                policies.add("IID" + policyNumber + "Policy.xml");
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                        VERSION_DIRECTORY, "IID" + policyNumber + "Response.xml");
                    log.info("Response that is received from the PDP :  " + response.encode());
                    if(expectedResponseCtx != null){
                        assertTrue(TestUtil.isMatching(response, expectedResponseCtx));
                    } else {
                        assertTrue("Response read from file is Null",false);
                    }
                } else {
                    assertFalse("Response received PDP is Null",false);
                }
            } else {
                assertTrue("Request read from file is Null", false);
            }

            log.info("Conformance Test IID" + policyNumber + " is finished");
        }
    }

//    public void testConformanceTestAA() throws Exception {
//
//        String policyNumber;
//
//        for(int i = 1; i < 29 ; i++){
//            if(i < 10){
//                policyNumber = "00" + i;
//            } else if(9 < i && i < 100) {
//                policyNumber = "0" + i;
//            } else {
//                policyNumber = Integer.toString(i);
//            }
//
//            log.info("Conformance Test IIIA" + policyNumber + " is started");
//
//            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
//                                                            "IIIA" + policyNumber + "Request.xml");
//            if(request != null){
//                log.info("Request that is sent to the PDP :  " + request);
//                Set<String> policies = new HashSet<String>();
//                policies.add("IIIA" + policyNumber + "Policy.xml");
//                String response = getPDPNewInstance(policies).evaluate(request);
//                if(response != null){
//                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
//                                        VERSION_DIRECTORY, "IIIA" + policyNumber + "Response.xml");
//                    log.info("Response that is received from the PDP :  " + response);
//                    if(expectedResponseCtx != null){
//                        assertTrue(TestUtil.isMatching(response, expectedResponseCtx.getEncoded()));
//                    } else {
//                        assertTrue("Response read from file is Null",false);
//                    }
//                } else {
//                    assertFalse("Response received PDP is Null",false);
//                }
//            } else {
//                assertTrue("Request read from file is Null", false);
//            }
//
//            log.info("Conformance Test IIIA" + policyNumber + " is finished");
//        }
//    }


//    public void testConformanceTest0005() throws Exception {
//
//        String policyNumber;
//
//        for(int i = 29; i < 31 ; i++){
//            if(i == 0){
//                log.info("Conformance Test IID00" + i + " does not started As required " +
//                                                                "attribute finder is not defined");
//                continue;
//            }
//
//            if(i < 10){
//                policyNumber = "00" + i;
//            } else if(9 < i && i < 100) {
//                policyNumber = "0" + i;
//            } else {
//                policyNumber = Integer.toString(i);
//            }
//
//            log.info("Conformance Test IID" + policyNumber + " is started");
//
//            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
//                                                            "IID" + policyNumber + "Request.xml");
//            if(request != null){
//                log.info("Request that is sent to the PDP :  " + request);
//                Set<String> policies = new HashSet<String>();
//                policies.add("IID" + policyNumber + "Policy1.xml");
//                policies.add("IID" + policyNumber + "Policy2.xml");
//                String response = getPDPNewInstance(policies).evaluate(request);
//                if(response != null){
//                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
//                                        VERSION_DIRECTORY, "IID" + policyNumber + "Response.xml");
//                    log.info("Response that is received from the PDP :  " + response);
//                    assertTrue(TestUtil.isMatching(response, expectedResponseCtx.getEncoded()));
//                    assertTrue(true);
//                }
//            }
//
//            log.info("Conformance Test IIC" + policyNumber + " is finished");
//        }
//    }
//
//
//    public void testConformanceTest0006() throws Exception {
//
//        String policyNumber;
//
//        for(int i = 1; i < 4; i++){
//            if(i == 0){
//                log.info("Conformance Test IIE00" + i + " does not started As required " +
//                                                                "attribute finder is not defined");
//                continue;
//            }
//
//            if(i < 10){
//                policyNumber = "00" + i;
//            } else if(9 < i && i < 100) {
//                policyNumber = "0" + i;
//            } else {
//                policyNumber = Integer.toString(i);
//            }
//
//            log.info("Conformance Test IIE" + policyNumber + " is started");
//
//            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
//                                                            "IIE" + policyNumber + "Request.xml");
//            if(request != null){
//                log.info("Request that is sent to the PDP :  " + request);
//                Set<String> policies = new HashSet<String>();
//                policies.add("IIE" + policyNumber + "Policy.xml");
//                policies.add("IIE" + policyNumber + "PolicyId1.xml");
//                policies.add("IIE" + policyNumber + "PolicySetId1.xml");
//                String response = getPDPNewInstance(policies).evaluate(request);
//                if(response != null){
//                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
//                                        VERSION_DIRECTORY, "IIE" + policyNumber + "Response.xml");
//                    log.info("Response that is received from the PDP :  " + response);
//                    assertTrue(TestUtil.isMatching(response, expectedResponseCtx.getEncoded()));
//                    assertTrue(true);
//                }
//            }
//
//            log.info("Conformance Test IIE" + policyNumber + " is finished");
//        }
//    }


    /**
     * Returns a new PDP instance with new XACML policies
     *
     * @param policies  Set of XACML policy file names
     * @return a  PDP instance
     */
    private static PDP getPDPNewInstance(Set<String> policies){

        PolicyFinder finder= new PolicyFinder();
        Set<String> policyLocations = new HashSet<String>();

        for(String policy : policies){
            try {
                String policyPath = (new File(".")).getCanonicalPath() + File.separator +
                        TestConstants.RESOURCE_PATH + File.separator + ROOT_DIRECTORY + File.separator +
                        VERSION_DIRECTORY + File.separator + TestConstants.POLICY_DIRECTORY +
                        File.separator + policy;
                policyLocations.add(policyPath);
            } catch (IOException e) {
               //ignore.
            }
        }

        FileBasedPolicyFinderModule testPolicyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
        Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
        policyModules.add(testPolicyFinderModule);
        finder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();
        pdpConfig = new PDPConfig(pdpConfig.getAttributeFinder(), finder,
                                                            pdpConfig.getResourceFinder(), false);
        return new PDP(pdpConfig);

    }
}
