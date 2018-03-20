/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.balana.extension;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.PDP;
import org.wso2.balana.TestUtil;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *  This XACML 3.0 extension test
 */
public class ExtensionTestV3 extends TestCase {

    /**
     * Configuration store
     */
    private static ConfigurationStore store;

    /**
     * the logger we'll use for all messages
     */
    private static Log log = LogFactory.getLog(ExtensionTestV3.class);

    @Override
    protected void setUp() throws Exception {

        String path = ExtensionTestV3.class.getResource("/extension/balana-config.xml").getPath();
        System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, path);

        store = new ConfigurationStore();
    }

    public void testExtendedFunctionIdTestA() throws Exception {

        Balana balana = Balana.getInstance();

        PolicyFinder finder= new PolicyFinder();
        Set<String> policyLocations = new HashSet<>();

        String policyPath = ExtensionTestV3.class.getResource("/extension/policies/TestPolicy_0001.xml").getPath();
        policyLocations.add(policyPath);

        FileBasedPolicyFinderModule testPolicyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
        Set<PolicyFinderModule> policyModules = new HashSet<>();
        policyModules.add(testPolicyFinderModule);
        finder.setModules(policyModules);

        PDPConfig pdpConfig = new PDPConfig(balana.getPdpConfig().getAttributeFinder(), finder,
                balana.getPdpConfig().getResourceFinder(), false);

        PDP pdp = new PDP(pdpConfig);

        String request = createRequest(ExtensionTestV3.class.getResource("/extension/requests/request_0001_01.xml").getPath());
        if(request != null) {
            log.info("Request that is sent to the PDP :  "+ request);
            ResponseCtx response = TestUtil.evaluate(pdp, request);
            if(response != null) {
                log.info("Response that is received from the PDP :  " + response.encode());
                String respFilePath = ExtensionTestV3.class.getResource("/extension/responses/response_0001_01.xml").getPath();
                ResponseCtx expectedResponseCtx = createResponse(respFilePath);
                if(expectedResponseCtx != null) {
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
    }

    private ResponseCtx createResponse(String filePath) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(filePath));
            return ResponseCtx.getInstance(doc.getDocumentElement());
        } catch (Exception e) {
            log.error("Error while reading expected response from file ", e);
        }

        return null;
    }

    private static String createRequest(String filePath) {

        StringWriter writer = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(filePath));
            DOMSource domSource = new DOMSource(doc);
            writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (Exception e) {
            log.error("Error while reading expected response from file ", e);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error("Error closing stream ", e);
                }
            }
        }
        return null;
    }
}
