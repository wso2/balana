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
 * class for keeping policy element data
 */
public class PolicyElementDTO {

    private String policyName;

    private String ruleCombiningAlgorithms;

    private String policyDescription;

    private String version;
    
    private TargetElementDTO targetElementDTO;
    
    private List<RuleElementDTO>  ruleElementDTOs = new ArrayList<RuleElementDTO>();
    
    private List<ObligationElementDTO> obligationElementDTOs = new ArrayList<ObligationElementDTO>();

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getRuleCombiningAlgorithms() {
        return ruleCombiningAlgorithms;
    }

    public void setRuleCombiningAlgorithms(String ruleCombiningAlgorithms) {
        this.ruleCombiningAlgorithms = ruleCombiningAlgorithms;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public TargetElementDTO getTargetElementDTO() {
        return targetElementDTO;
    }

    public void setTargetElementDTO(TargetElementDTO targetElementDTO) {
        this.targetElementDTO = targetElementDTO;
    }

    public List<RuleElementDTO> getRuleElementDTOs() {
        return ruleElementDTOs;
    }

    public void setRuleElementDTOs(List<RuleElementDTO> ruleElementDTOs) {
        this.ruleElementDTOs = ruleElementDTOs;
    }

    public void addRuleElementDTO(RuleElementDTO ruleElementDTO) {
        this.ruleElementDTOs.add(ruleElementDTO);
    }

    public List<ObligationElementDTO> getObligationElementDTOs() {
        return obligationElementDTOs;
    }

    public void setObligationElementDTOs(List<ObligationElementDTO> obligationElementDTOs) {
        this.obligationElementDTOs = obligationElementDTOs;
    }

    public void addObligationElementDTO(ObligationElementDTO obligationElementDTO) {
        this.obligationElementDTOs.add(obligationElementDTO);
    }
}
