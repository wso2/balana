/*
*  Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.balana.utils.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.balana.utils.PolicyUtils;
import org.wso2.balana.utils.Utils;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.dto.BasicPolicyDTO;
import org.wso2.balana.utils.policy.dto.PolicyElementDTO;
import org.wso2.balana.utils.policy.dto.PolicySetElementDTO;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *   // TODO
 */
public class PolicyBuilder {


    private static final Object lock = new Object();

    private static PolicyBuilder policyBuilder;

    private static final Log log = LogFactory.getLog(PolicyBuilder.class);

    /**
     * Get a PolicyBuilder instance. This method will return an
     * PolicyBuilder instance if exists, or creates a new one
     *
     * @return PolicyBuilder instance for that tenant
     */
    public static PolicyBuilder getInstance() {

        if (policyBuilder == null) {
            synchronized (lock){
                if (policyBuilder == null) {
                    policyBuilder = new PolicyBuilder();
                }
            }
        }
        return policyBuilder;
    }

    private PolicyBuilder() {
    }

    public String build(BasicPolicyDTO basicPolicyDTO) throws PolicyBuilderException {

        Document doc = null;
        try {
            doc = Utils.createNewDocument();
        } catch (ParserConfigurationException e) {
            throw new PolicyBuilderException("While creating Document Object", e);
        }
        if(doc != null) {
            doc.appendChild(BasicPolicyHelper.createPolicyElement(basicPolicyDTO, doc));
            try {
                return Utils.getStringFromDocument(doc);
            } catch (TransformerException e) {
                throw new PolicyBuilderException("Error while converting Policy element to String", e);
            }
        }
        return null;
    }

    public String build(PolicyElementDTO policyElementDTO) throws PolicyBuilderException {

        Document doc = null;
        try {
            doc = Utils.createNewDocument();
        } catch (ParserConfigurationException e) {
            throw new PolicyBuilderException("While creating Document Object", e);
        }
        if(doc != null) {
            doc.appendChild(PolicyUtils.createPolicyElement(policyElementDTO, doc));
            try {
                return Utils.getStringFromDocument(doc);
            } catch (TransformerException e) {
                throw new PolicyBuilderException("Error while converting Policy element to String", e);
            }
        }
        return null;
    }

    public String build(PolicySetElementDTO policyElementDTO) throws PolicyBuilderException {

        Document doc = null;
        try {
            doc = Utils.createNewDocument();
        } catch (ParserConfigurationException e) {
            throw new PolicyBuilderException("While creating Document Object", e);
        }
        if(doc != null) {
            doc.appendChild(PolicyUtils.createPolicySetElement(policyElementDTO, doc));
            try {
                return Utils.getStringFromDocument(doc);
            } catch (TransformerException e) {
                throw new PolicyBuilderException("Error while converting Policy element to String", e);
            }
        }
        return null;
    }

    /**
     *
     * @param requestElementDTO
     * @return
     * @throws PolicyBuilderException
     */
    public String buildRequest(RequestElementDTO requestElementDTO) throws PolicyBuilderException {

        Document doc = null;
        try {
            doc = Utils.createNewDocument();
        } catch (ParserConfigurationException e) {
            throw new PolicyBuilderException("While creating Document Object", e);
        }
        if(doc != null) {
            doc.appendChild(PolicyUtils.createRequestElement(requestElementDTO, doc));
            try {
                return Utils.getStringFromDocument(doc);
            } catch (TransformerException e) {
                throw new PolicyBuilderException("Error while converting request element to String", e);
            }
        }
        return null;
    }

    public String[] getFunctions(){
        return new String[0];
    }

    public String[] getRuleAlgorithms(){
        return new String[0];
    }

    public String[] getPolicyAlgorithms(){
        return new String[0];
    }

    public String[] getDataTypes(){
        return new String[0];
    }
}
