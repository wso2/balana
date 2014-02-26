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


public class AttributeValueElementDTO {

    private String attributeValue;

    private String attributeDataType;

    private int applyElementNumber;

    private String applyElementId;

    private int elementId;

    public int getElementId(){
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public int getApplyElementNumber() {
        return applyElementNumber;
    }

    public String getApplyElementId() {
        return applyElementId;
    }

    public void setApplyElementId(String applyElementId) {
        this.applyElementId = applyElementId;
    }

    public void setApplyElementNumber(int applyElementNumber) {
        this.applyElementNumber = applyElementNumber;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeDataType() {
        return attributeDataType;
    }

    public void setAttributeDataType(String attributeDataType) {
        this.attributeDataType = attributeDataType;
    }
}
