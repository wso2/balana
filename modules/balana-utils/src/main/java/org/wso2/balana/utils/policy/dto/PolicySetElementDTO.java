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

package org.wso2.balana.utils.policy.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulate the policy set attribute
 */
public class PolicySetElementDTO {

    private String policySetId;

    private String policyCombiningAlgId;

    private String version;

    private TargetElementDTO targetElementDTO;

    private String description;

    private List<String> policySets = new ArrayList<String>();

    private List<String> policies = new ArrayList<String>();

    private List<String> policySetIdReferences = new ArrayList<String>();

    private List<String> PolicyIdReferences = new ArrayList<String>();

    private List<ObligationElementDTO> obligationElementDTOs = new ArrayList<ObligationElementDTO>();

    public String getPolicySetId() {
        return policySetId;
    }

    public void setPolicySetId(String policySetId) {
        this.policySetId = policySetId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPolicyCombiningAlgId() {
        return policyCombiningAlgId;
    }

    public void setPolicyCombiningAlgId(String policyCombiningAlgId) {
        this.policyCombiningAlgId = policyCombiningAlgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPolicySets() {
        return policySets;
    }

    public void setPolicySets(List<String> policySets) {
        this.policySets = policySets;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicy(String policy) {
        this.policies.add(policy);
    }

    public List<String> getPolicySetIdReferences() {
        return policySetIdReferences;
    }

    public void setPolicySetIdReferences(List<String> policySetIdReferences) {
        this.policySetIdReferences = policySetIdReferences;
    }

    public List<String> getPolicyIdReferences() {
        return PolicyIdReferences;
    }

    public void setPolicyIdReferences(List<String> policyIdReferences) {
        PolicyIdReferences = policyIdReferences;
    }

    public TargetElementDTO getTargetElementDTO() {
        return targetElementDTO;
    }

    public void setTargetElementDTO(TargetElementDTO targetElementDTO) {
        this.targetElementDTO = targetElementDTO;
    }

    public List<ObligationElementDTO> getObligationElementDTOs() {
        return obligationElementDTOs;
    }

    public void setObligationElementDTOs(List<ObligationElementDTO> obligationElementDTOs) {
        this.obligationElementDTOs = obligationElementDTOs;
    }
}
