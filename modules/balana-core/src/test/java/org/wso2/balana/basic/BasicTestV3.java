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

package org.wso2.balana.basic;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.*;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *  This XACML 3.0 basic polciy test. This would test a basic policy, basic policy with obligations and
 *   basic policy with advices. 
 */
public class BasicTestV3 extends TestCase {

    /**
     * directory name that states the test type
     */
    private final static String ROOT_DIRECTORY  = "basic";

    /**
     * directory name that states XACML version
     */
    private final static String VERSION_DIRECTORY  = "3";

    /**
     * the logger we'll use for all messages
     */
	private static Log log = LogFactory.getLog(BasicTestV3.class);

    public void testBasicTest0001() throws Exception {

        String reqResNo;
        Set<String> policies = new HashSet<String>();
        policies.add("TestPolicy_0001.xml");
        log.info("Basic Test 0001 is started");

        for(int i = 1; i < 8 ; i++){
            
            if(i < 10){
                reqResNo = "0" + i;
            } else {
                reqResNo = Integer.toString(i);
            }

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                        "request_0001_" + reqResNo + ".xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    log.info("Response that is received from the PDP :  " + response.encode());
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                    VERSION_DIRECTORY, "response_0001_" + reqResNo + ".xml");
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

            log.info("Basic Test 0001 is finished");
        }
    }


    public void testBasicTest0002() throws Exception {

        String reqResNo;
        Set<String> policies = new HashSet<String>();
        policies.add("TestPolicy_0002.xml");
        log.info("Basic Test 0002 is started");

        for(int i = 1; i < 4 ; i++){

            if(i < 10){
                reqResNo = "0" + i;
            } else {
                reqResNo = Integer.toString(i);
            }

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                        "request_0002_" + reqResNo + ".xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    log.info("Response that is received from the PDP :  " + response.encode());
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                    VERSION_DIRECTORY, "response_0002_" + reqResNo + ".xml");
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

            log.info("Basic Test 0002 is finished");
        }
    }

    public void testBasicTest0003() throws Exception {

        String reqResNo;
        Set<String> policies = new HashSet<String>();
        policies.add("TestPolicy_0003.xml");
        log.info("Basic Test 0003 is started");

        for(int i = 1; i < 4 ; i++){

            if(i < 10){
                reqResNo = "0" + i;
            } else {
                reqResNo = Integer.toString(i);
            }

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                        "request_0003_" + reqResNo + ".xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    log.info("Response that is received from the PDP :  " + response.encode());
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                    VERSION_DIRECTORY, "response_0003_" + reqResNo + ".xml");
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

            log.info("Basic Test 0003 is finished");
        }
    }

    public void testBasicTest0004() throws Exception {

        String reqResNo;
        Set<String> policies = new HashSet<String>();
        policies.add("TestPolicy_0004.xml");
        log.info("Basic Test 0004 is started");

        for(int i = 1; i < 4 ; i++){

            if(i < 10){
                reqResNo = "0" + i;
            } else {
                reqResNo = Integer.toString(i);
            }

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                        "request_0004_" + reqResNo + ".xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    log.info("Response that is received from the PDP :  " + response.encode());
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                    VERSION_DIRECTORY, "response_0004_" + reqResNo + ".xml");
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

            log.info("Basic Test 0003 is finished");
        }
    }

    public void testBasicTest0005() throws Exception {

        String reqResNo;
        Set<String> policies = new HashSet<String>();
        policies.add("TestPolicy_0005.xml");
        log.info("Basic Test 0005 is started");

        for(int i = 1; i < 4 ; i++){

            if(i < 10){
                reqResNo = "0" + i;
            } else {
                reqResNo = Integer.toString(i);
            }

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                        "request_0005_" + reqResNo + ".xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    log.info("Response that is received from the PDP :  " + response.encode());
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                    VERSION_DIRECTORY, "response_0005_" + reqResNo + ".xml");
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

            log.info("Basic Test 0003 is finished");
        }
    }

    /**
     * Returns a new PDP instance with new XACML policies
     *
     * @param policies  Set of XACML policy file names
     * @return a  PDP instance
     */
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
                                                            pdpConfig.getResourceFinder(), true);
        return new PDP(pdpConfig);

    }

}
