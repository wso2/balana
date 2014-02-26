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
public class AttributeSelectorDTO {

    private String attributeSelectorRequestContextPath;

    private String attributeSelectorDataType;

    private String attributeSelectorMustBePresent;

    private int applyElementNumber;

    private String applyElementId;

    private int elementNumber;

    public String getAttributeSelectorRequestContextPath() {
        return attributeSelectorRequestContextPath;
    }

    public void setAttributeSelectorRequestContextPath(String attributeSelectorRequestContextPath) {
        this.attributeSelectorRequestContextPath = attributeSelectorRequestContextPath;
    }

    public String getAttributeSelectorDataType() {
        return attributeSelectorDataType;
    }

    public void setAttributeSelectorDataType(String attributeSelectorDataType) {
        this.attributeSelectorDataType = attributeSelectorDataType;
    }

    public String getAttributeSelectorMustBePresent() {
        return attributeSelectorMustBePresent;
    }

    public void setAttributeSelectorMustBePresent(String attributeSelectorMustBePresent) {
        this.attributeSelectorMustBePresent = attributeSelectorMustBePresent;
    }

    public int getApplyElementNumber() {
        return applyElementNumber;
    }

    public void setApplyElementNumber(int applyElementNumber) {
        this.applyElementNumber = applyElementNumber;
    }

    public int getElementNumber() {
        return elementNumber;
    }

    public void setElementNumber(int elementNumber) {
        this.elementNumber = elementNumber;
    }

    public String getApplyElementId() {
        return applyElementId;
    }

    public void setApplyElementId(String applyElementId) {
        this.applyElementId = applyElementId;
    }
}
