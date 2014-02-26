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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AttributeElementDTO {

    private String attributeId;

    private boolean includeInResult;

    private String dataType;

    private String issuer;

    private List<String> attributeValues = new ArrayList<String>();

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public boolean isIncludeInResult() {
        return includeInResult;
    }

    public void setIncludeInResult(boolean includeInResult) {
        this.includeInResult = includeInResult;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<String> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<String> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public void addAttributeValue(String attributeValue) {
        this.attributeValues.add(attributeValue);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
