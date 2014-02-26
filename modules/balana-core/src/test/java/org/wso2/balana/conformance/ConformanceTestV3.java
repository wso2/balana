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
import org.wso2.balana.ctx.AbstractRequestCtx;
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
 *  This XACML 3.0 conformation test. But this is the correct tests published bu OASIS
 */
public class ConformanceTestV3 extends TestCase {


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
    private final static String VERSION_DIRECTORY  = "3";

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

        for(int i = 1; i < 29 ; i++){
            
            if(i < 10){
                policyNumber = "00" + i;
            } else if(9 < i && i < 100) {
                policyNumber = "0" + i;
            } else {
                policyNumber = Integer.toString(i);
            }

            log.info("Conformance Test IIIA" + policyNumber + " is started");

            String request = TestUtil.createRequest(ROOT_DIRECTORY, VERSION_DIRECTORY,
                                                            "IIIA" + policyNumber + "Request.xacml3.xml");
            if(request != null){
                log.info("Request that is sent to the PDP :  " + request);
                Set<String> policies = new HashSet<String>();
                policies.add("IIIA" + policyNumber + "Policy.xacml3.xml");
                ResponseCtx response = TestUtil.evaluate(getPDPNewInstance(policies), request);
                if(response != null){
                    ResponseCtx expectedResponseCtx = TestUtil.createResponse(ROOT_DIRECTORY,
                                        VERSION_DIRECTORY, "IIIA" + policyNumber + "Response.xacml3.xml");
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

            log.info("Conformance Test IIIA" + policyNumber + " is finished");
        }
    }


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
