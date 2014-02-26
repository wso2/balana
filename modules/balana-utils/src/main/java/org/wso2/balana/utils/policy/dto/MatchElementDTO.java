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

/**
 *
 */
public class MatchElementDTO {

    
    private String matchElementName;

    private String ruleElementName;

    private String matchId;

    private AttributeValueElementDTO attributeValueElementDTO;

    private AttributeDesignatorDTO attributeDesignatorDTO;

    private AttributeSelectorDTO attributeSelectorDTO;

    private int elementId;

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public AttributeDesignatorDTO getAttributeDesignatorDTO() {
        return attributeDesignatorDTO;
    }

    public void setAttributeDesignatorDTO(AttributeDesignatorDTO attributeDesignatorDTO) {
        this.attributeDesignatorDTO = attributeDesignatorDTO;
    }

    public AttributeSelectorDTO getAttributeSelectorDTO() {
        return attributeSelectorDTO;
    }

    public void setAttributeSelectorDTO(AttributeSelectorDTO attributeSelectorDTO) {
        this.attributeSelectorDTO = attributeSelectorDTO;
    }

    public String getRuleElementName() {
        return ruleElementName;
    }

    public void setRuleElementName(String ruleElementName) {
        this.ruleElementName = ruleElementName;
    }

    public String getMatchElementName() {
        return matchElementName;
    }
             
    public void setMatchElementName(String matchElementName) {
        this.matchElementName = matchElementName;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public AttributeValueElementDTO getAttributeValueElementDTO() {
        return attributeValueElementDTO;
    }

    public void setAttributeValueElementDTO(AttributeValueElementDTO attributeValueElementDTO) {
        this.attributeValueElementDTO = attributeValueElementDTO;
    }
}
