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

/**
 *
 */
public class AttributeDesignatorDTO {

    private String elementName ;

    private int ElementId;

    private int applyElementNumber;

    private String applyElementId;

    private String attributeId;

    private String dataType;

    private String issuer;

    private String mustBePresent;

    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getMustBePresent() {
        return mustBePresent;
    }

    public void setMustBePresent(String mustBePresent) {
        this.mustBePresent = mustBePresent;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public int getElementId() {
        return ElementId;
    }

    public void setElementId(int elementId) {
        ElementId = elementId;
    }

    public int getApplyElementNumber() {
        return applyElementNumber;
    }

    public void setApplyElementNumber(int applyElementNumber) {
        this.applyElementNumber = applyElementNumber;
    }

    public String getApplyElementId() {
        return applyElementId;
    }

    public void setApplyElementId(String applyElementId) {
        this.applyElementId = applyElementId;
    }
}
