/*
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.balana;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.wso2.balana.advance.AdvanceTestV3;
import org.wso2.balana.basic.TestFunctionV3;
import org.wso2.balana.basic.BasicTestV3;
import org.wso2.balana.basic.TestMultipleRequestV3;
import org.wso2.balana.basic.TestXPathV3;
import org.wso2.balana.conformance.ConformanceTestV2;
import org.wso2.balana.conformance.ConformanceTestV3;

/**
 * Test suite for Balana 
 */
public class BaseTestCase extends TestSuite {

    public static Test suite() throws Exception {

        TestSuite testSuite = new TestSuite();
        // conformance test for XACML 2.0
        testSuite.addTestSuite(ConformanceTestV2.class);
        // conformance test for XACML 3.0
        testSuite.addTestSuite(ConformanceTestV3.class);
        // basic test of XACML version 3.0. Simple policy and obligation, advice
        testSuite.addTestSuite(BasicTestV3.class);
        // basic function test of XACML version 3.0
        testSuite.addTestSuite(TestFunctionV3.class);
        // multiple decision profile
        testSuite.addTestSuite(TestMultipleRequestV3.class);
        // XPath test
        testSuite.addTestSuite(TestXPathV3.class);

        testSuite.addTestSuite(ConformanceTestV3.class);
        // test that has been written for jira issue
        testSuite.addTestSuite(AdvanceTestV3.class);
        return testSuite;
    }
}
