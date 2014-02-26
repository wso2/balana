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
 * //TODO
 */
public class RequestElementDTO {

    private boolean multipleRequest;

    private boolean returnPolicyIdList;

    private boolean combinedDecision;

    private List<AttributesElementDTO> attributesElementDTOs = new ArrayList<AttributesElementDTO>();

    public boolean isMultipleRequest() {
        return multipleRequest;
    }

    public void setMultipleRequest(boolean multipleRequest) {
        this.multipleRequest = multipleRequest;
    }

    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    public void setReturnPolicyIdList(boolean returnPolicyIdList) {
        this.returnPolicyIdList = returnPolicyIdList;
    }

    public boolean isCombinedDecision() {
        return combinedDecision;
    }

    public void setCombinedDecision(boolean combinedDecision) {
        this.combinedDecision = combinedDecision;
    }

    public List<AttributesElementDTO> getAttributesElementDTOs() {
        return attributesElementDTOs;
    }

    public void setAttributesElementDTOs(List<AttributesElementDTO> attributesElementDTOs) {
        this.attributesElementDTOs = attributesElementDTOs;
    }
}
