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
 *
 */
public class RuleElementDTO {

    private String ruleId;

    private ConditionElementDT0 conditionElementDT0;

    private String ruleEffect;

    private String ruleDescription;

    private TargetElementDTO targetElementDTO;

    private List<ObligationElementDTO> obligationElementDTOs = new ArrayList<ObligationElementDTO>();

    public ConditionElementDT0 getConditionElementDT0() {
        return conditionElementDT0;
    }

    public void setConditionElementDT0(ConditionElementDT0 conditionElementDT0) {
        this.conditionElementDT0 = conditionElementDT0;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public String getRuleEffect() {
        return ruleEffect;
    }

    public void setRuleEffect(String ruleEffect) {
        this.ruleEffect = ruleEffect;
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

    public TargetElementDTO getTargetElementDTO() {
        return targetElementDTO;
    }

    public void setTargetElementDTO(TargetElementDTO targetElementDTO) {
        this.targetElementDTO = targetElementDTO;
    }
}
