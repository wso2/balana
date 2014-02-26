/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
public class ApplyElementDTO {

    private int applyElementNumber;

    private String applyElementId;

    private String functionId;

    private String variableId;

    private String functionFunctionId;

    private int addApplyElementPageNumber;

    private int attributeDesignatorsElementCount;

    private int attributeValueElementCount;

    private int attributeSelectorElementCount;

    private List<ApplyElementDTO> applyElements = new ArrayList<ApplyElementDTO>();

    private List<AttributeDesignatorDTO> attributeDesignators = new ArrayList<AttributeDesignatorDTO>();

    private List<AttributeSelectorDTO> attributeSelectors = new ArrayList<AttributeSelectorDTO>();

    private List<AttributeValueElementDTO> attributeValueElementDTOs = new ArrayList<AttributeValueElementDTO>();

    public String getApplyElementId() {
        return applyElementId;
    }

    public void setApplyElementId(String applyElementId) {
        this.applyElementId = applyElementId;
    }

    public int getAddApplyElementPageNumber() {
        return addApplyElementPageNumber;
    }

    public void setAddApplyElementPageNumber(int addApplyElementPageNumber) {
        this.addApplyElementPageNumber = addApplyElementPageNumber;
    }

    public List<ApplyElementDTO> getApplyElements() {
        return applyElements;
    }

    public void setApplyElement(ApplyElementDTO applyElement) {
        this.applyElements.add(applyElement);
    }

    public List<AttributeValueElementDTO> getAttributeValueElementDTOs() {
        return attributeValueElementDTOs;
    }

    public void setAttributeValueElementDTO(AttributeValueElementDTO attributeValueElementDTO) {
        this.attributeValueElementDTOs.add(attributeValueElementDTO);
    }

    public int getApplyElementNumber() {
        return applyElementNumber;
    }

    public void setApplyElementNumber(int applyElementNumber) {
        this.applyElementNumber = applyElementNumber;
    }

    public String getFunctionFunctionId() {
        return functionFunctionId;
    }

    public void setFunctionFunctionId(String functionFunctionId) {
        this.functionFunctionId = functionFunctionId;
    }

    public List<AttributeDesignatorDTO> getAttributeDesignators() {
        return attributeDesignators;
    }

    public void setAttributeDesignators(AttributeDesignatorDTO attributeDesignator) {
        this.attributeDesignators.add(attributeDesignator);
    }

    public List<AttributeSelectorDTO> getAttributeSelectors() {
        return attributeSelectors;
    }

    public void setAttributeSelectors(AttributeSelectorDTO attributeSelector) {
        this.attributeSelectors.add(attributeSelector);
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public int getAttributeDesignatorsElementCount() {
        return attributeDesignatorsElementCount;
    }

    public void setAttributeDesignatorsElementCount(int attributeDesignatorsElementCount) {
        this.attributeDesignatorsElementCount = attributeDesignatorsElementCount;
    }

    public int getAttributeValueElementCount() {
        return attributeValueElementCount;
    }

    public void setAttributeValueElementCount(int attributeValueElementCount) {
        this.attributeValueElementCount = attributeValueElementCount;
    }

    public int getAttributeSelectorElementCount() {
        return attributeSelectorElementCount;
    }

    public void setAttributeSelectorElementCount(int attributeSelectorElementCount) {
        this.attributeSelectorElementCount = attributeSelectorElementCount;
    }
}
