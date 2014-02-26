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
public class BasicRuleDTO {

    private String ruleId;

    private String ruleEffect;

    private String ruleDescription;

    private String resourceList;

    private String actionList;

    private String subjectList;

    private String environmentList;

    private String resourceDataType;

    private String actionDataType;

    private String subjectDataType;

    private String environmentDataType;

    private String resourceId;

    private String actionId;

    private String subjectId;

    private String environmentId;

    private String functionOnActions;

    private String functionOnSubjects;

    private String functionOnResources;

    private String functionOnEnvironment;

    private String preFunctionOnActions;

    private String preFunctionOnSubjects;

    private String preFunctionOnResources;

    private String preFunctionOnEnvironment;
    
    private boolean completedRule;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getResourceList() {
        return resourceList;
    }

    public void setResourceList(String resourceList) {
        this.resourceList = resourceList;
    }

    public String getActionList() {
        return actionList;
    }

    public void setActionList(String actionList) {
        this.actionList = actionList;
    }

    public String getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(String subjectList) {
        this.subjectList = subjectList;
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

    public String getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(String environmentList) {
        this.environmentList = environmentList;
    }

    public String setEnvironmentDataTypegetResourceDataType() {
        return resourceDataType;
    }

    public void setResourceDataType(String resourceDataType) {
        this.resourceDataType = resourceDataType;
    }

    public String getResourceDataType() {
        return resourceDataType;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getEnvironmentDataType() {
        return environmentDataType;
    }

    public void setEnvironmentDataType(String environmentDataType) {
        this.environmentDataType = environmentDataType;
    }

    public String getSubjectDataType() {
        return subjectDataType;
    }

    public void setSubjectDataType(String subjectDataType) {
        this.subjectDataType = subjectDataType;
    }

    public String getActionDataType() {
        return actionDataType;
    }

    public void setActionDataType(String actionDataType) {
        this.actionDataType = actionDataType;
    }

    public String getFunctionOnActions() {
        return functionOnActions;
    }

    public void setFunctionOnActions(String functionOnActions) {
        this.functionOnActions = functionOnActions;
    }

    public String getFunctionOnEnvironment() {
        return functionOnEnvironment;
    }

    public void setFunctionOnEnvironment(String functionOnEnvironment) {
        this.functionOnEnvironment = functionOnEnvironment;
    }

    public String getFunctionOnResources() {
        return functionOnResources;
    }

    public void setFunctionOnResources(String functionOnResources) {
        this.functionOnResources = functionOnResources;
    }

    public String getFunctionOnSubjects() {
        return functionOnSubjects;
    }

    public void setFunctionOnSubjects(String functionOnSubjects) {
        this.functionOnSubjects = functionOnSubjects;
    }


    public boolean isCompletedRule() {
        return completedRule;
    }

    public void setCompletedRule(boolean completedRule) {
        this.completedRule = completedRule;
    }

    public String getPreFunctionOnActions() {
        return preFunctionOnActions;
    }

    public void setPreFunctionOnActions(String preFunctionOnActions) {
        this.preFunctionOnActions = preFunctionOnActions;
    }

    public String getPreFunctionOnResources() {
        return preFunctionOnResources;
    }

    public void setPreFunctionOnResources(String preFunctionOnResources) {
        this.preFunctionOnResources = preFunctionOnResources;
    }

    public String getPreFunctionOnSubjects() {
        return preFunctionOnSubjects;
    }

    public void setPreFunctionOnSubjects(String preFunctionOnSubjects) {
        this.preFunctionOnSubjects = preFunctionOnSubjects;
    }

    public String getPreFunctionOnEnvironment() {
        return preFunctionOnEnvironment;
    }

    public void setPreFunctionOnEnvironment(String preFunctionOnEnvironment) {
        this.preFunctionOnEnvironment = preFunctionOnEnvironment;
    }
}
