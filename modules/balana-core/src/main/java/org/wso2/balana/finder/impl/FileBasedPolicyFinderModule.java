/*
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

package org.wso2.balana.finder.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.*;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * This is file based policy repository.  Policies can be inside the directory in a file system.
 * Then you can set directory location using "org.wso2.balana.PolicyDirectory" JAVA property   
 */
public class FileBasedPolicyFinderModule extends PolicyFinderModule{

    private PolicyFinder finder = null;

    private Map<URI, AbstractPolicy> policies ;

    private  Set<String> policyLocations;

    private PolicyCombiningAlgorithm combiningAlg;

    /**
     * the logger we'll use for all messages
     */
	private static Log log = LogFactory.getLog(FileBasedPolicyFinderModule.class);

    public static final String POLICY_DIR_PROPERTY = "org.wso2.balana.PolicyDirectory";

    public FileBasedPolicyFinderModule() {
        policies = new HashMap<URI, AbstractPolicy>();
        if(System.getProperty(POLICY_DIR_PROPERTY) != null){
            policyLocations = new HashSet<String>();
            policyLocations.add(System.getProperty(POLICY_DIR_PROPERTY));            
        }
    }

    public FileBasedPolicyFinderModule(Set<String> policyLocations) {
        policies = new HashMap<URI, AbstractPolicy>();
        this.policyLocations = policyLocations;
    }

    @Override
    public void init(PolicyFinder finder) {

        this.finder = finder;
        loadPolicies();
        combiningAlg = new DenyOverridesPolicyAlg();
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        
        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<AbstractPolicy>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for (Map.Entry<URI, AbstractPolicy> entry : entrySet) {
            
            AbstractPolicy policy = entry.getValue();
            MatchResult match = policy.match(context);
            int result = match.getResult();

            // if target matching was indeterminate, then return the error
            if (result == MatchResult.INDETERMINATE)
                return new PolicyFinderResult(match.getStatus());

            // see if the target matched
            if (result == MatchResult.MATCH) {

                if ((combiningAlg == null) && (selectedPolicies.size() > 0)) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<String>();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable "
                                               + "top-level policies");
                    return new PolicyFinderResult(status);
                }

                // this is the first match we've found, so remember it
                selectedPolicies.add(policy);
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch (selectedPolicies.size()) {
        case 0:
            if(log.isDebugEnabled()){
                log.debug("No matching XACML policy found");
            }
            return new PolicyFinderResult();
        case 1:
             return new PolicyFinderResult((selectedPolicies.get(0)));
        default:
            return new PolicyFinderResult(new PolicySet(null, combiningAlg, null, selectedPolicies));
        }
    }

    @Override
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, 
                                         PolicyMetaData parentMetaData) {

        AbstractPolicy policy = policies.get(idReference);
        if(policy != null){
            if (type == PolicyReference.POLICY_REFERENCE) {
                if (policy instanceof Policy){
                    return new PolicyFinderResult(policy);
                }
            } else {
                if (policy instanceof PolicySet){
                    return new PolicyFinderResult(policy);
                }
            }
        }

        // if there was an error loading the policy, return the error
        ArrayList<String> code = new ArrayList<String>();
        code.add(Status.STATUS_PROCESSING_ERROR);
        Status status = new Status(code,
                                   "couldn't load referenced policy");
        return new PolicyFinderResult(status);
    }

    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    /**
     * Re-sets the policies known to this module to those contained in the
     * given files.
     *
     */
    public void loadPolicies() {

        policies.clear();

        for(String policyLocation : policyLocations){

            File file = new File(policyLocation);
            if(!file.exists()){
                continue;
            }

            if(file.isDirectory()){
                String[] files = file.list();
                for(String policyFile : files){
                    File fileLocation = new File(policyLocation + File.separator + policyFile);
                    if(!fileLocation.isDirectory()){
                        loadPolicy(policyLocation + File.separator + policyFile, finder);
                    }
                }
            } else {
                loadPolicy(policyLocation, finder);    
            }
        }
    }    

    /**
     * Private helper that tries to load the given file-based policy, and
     * returns null if any error occurs.
     *
     * @param policyFile file path to policy
     * @param finder policy finder
     * @return  <code>AbstractPolicy</code>
     */
    private AbstractPolicy loadPolicy(String policyFile, PolicyFinder finder) {

        AbstractPolicy policy = null;
        InputStream stream = null;

        try {
            // create the factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            // create a builder based on the factory & try to load the policy
            DocumentBuilder db = factory.newDocumentBuilder();
            stream = new FileInputStream(policyFile);
            Document doc = db.parse(stream);

            // handle the policy, if it's a known type
            Element root = doc.getDocumentElement();
            String name = DOMHelper.getLocalName(root);

            if (name.equals("Policy")) {
                policy = Policy.getInstance(root);
            } else if (name.equals("PolicySet")) {
                policy = PolicySet.getInstance(root, finder);
            }
        } catch (Exception e) {
            // just only logs
            log.error("Fail to load policy : " + policyFile , e);
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("Error while closing input stream");
                }
            }
        }

        if(policy != null){
            policies.put(policy.getId(), policy);
        }

        return policy;
    }
    
}
