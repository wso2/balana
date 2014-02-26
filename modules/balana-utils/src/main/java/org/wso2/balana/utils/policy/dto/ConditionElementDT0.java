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
public class ConditionElementDT0 {

    private ApplyElementDTO applyElement;

    private AttributeDesignatorDTO attributeDesignator;

    private AttributeSelectorDTO  attributeSelector;

    private String variableId;

    private String functionFunctionId;

    private AttributeValueElementDTO attributeValueElementDTO;

    public AttributeValueElementDTO getAttributeValueElementDTO() {
        return attributeValueElementDTO;
    }

    public void setAttributeValueElementDTO(AttributeValueElementDTO attributeValueElementDTO) {
        this.attributeValueElementDTO = attributeValueElementDTO;
    }

    public AttributeSelectorDTO getAttributeSelector() {
        return attributeSelector;
    }

    public void setAttributeSelector(AttributeSelectorDTO attributeSelector) {
        this.attributeSelector = attributeSelector;
    }

    public ApplyElementDTO getApplyElement() {
        return applyElement;
    }

    public void setApplyElement(ApplyElementDTO applyElement) {
        this.applyElement = applyElement;
    }

    public AttributeDesignatorDTO getAttributeDesignator() {
        return attributeDesignator;
    }

    public void setAttributeDesignator(AttributeDesignatorDTO attributeDesignator) {
        this.attributeDesignator = attributeDesignator;
    }

    

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getFunctionFunctionId() {
        return functionFunctionId;
    }

    public void setFunctionFunctionId(String functionFunctionId) {
        this.functionFunctionId = functionFunctionId;
    }

}
