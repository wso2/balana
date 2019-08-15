/*
*  Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.balana.utils.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.utils.Constants.PolicyConstants;
import org.wso2.balana.utils.PolicyUtils;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class BasicPolicyHelper {

    private static final Log log = LogFactory.getLog(BasicPolicyHelper.class);

    public static Element createPolicyElement(BasicPolicyDTO basicPolicyDTO, Document doc)
                                                                    throws PolicyBuilderException {

        Element policyElement = doc.createElement(PolicyConstants.POLICY_ELEMENT);

        policyElement.setAttribute("xmlns", PolicyConstants.XACMLData.XACML3_POLICY_NAMESPACE);

        if(basicPolicyDTO.getPolicyId() != null && basicPolicyDTO.getPolicyId().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.POLICY_ID, basicPolicyDTO.
                    getPolicyId());
        } else {
            throw new PolicyBuilderException("Policy name can not be null");
        }

        if(basicPolicyDTO.getRuleAlgorithm() != null && basicPolicyDTO.
                getRuleAlgorithm().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.RULE_ALGORITHM, basicPolicyDTO.
                    getRuleAlgorithm());
        } else {
            policyElement.setAttribute(PolicyConstants.RULE_ALGORITHM,
                    PolicyConstants.RuleCombiningAlog.DENY_OVERRIDE_ID); // TODO
            log.warn("Rule combining algorithm is not defined. Use default algorithm; Deny Override");
        }

        if(basicPolicyDTO.getVersion() != null && basicPolicyDTO.getVersion().trim().length() > 0){
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION,
                    basicPolicyDTO.getVersion());
        } else {
            // policy version is can be handled by policy registry.  therefore we can ignore it, although it
            // is a required attribute
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION, "1.0");
        }

        if(basicPolicyDTO.getDescription() != null && basicPolicyDTO.getDescription().trim().length() > 0) {

            Element descriptionElement = doc.createElement(PolicyConstants.
                    DESCRIPTION_ELEMENT);
            descriptionElement.setTextContent(basicPolicyDTO.getDescription());
            policyElement.appendChild(descriptionElement);
        }

        BasicTargetDTO basicTargetDTO =  basicPolicyDTO.getTargetDTO();
        List<BasicRuleDTO> basicRuleDTOs = basicPolicyDTO.getBasicRuleDTOs();

        if(basicTargetDTO != null){
            policyElement.appendChild(BasicPolicyHelper.
                    createTargetElement(basicTargetDTO, doc));
        } else {
            policyElement.appendChild(doc.createElement(PolicyConstants.
                    TARGET_ELEMENT));
        }
        if(basicRuleDTOs != null && basicRuleDTOs.size() > 0) {
            for(BasicRuleDTO basicRuleDTO : basicRuleDTOs) {
                policyElement.appendChild(BasicPolicyHelper.
                        createRuleElement(basicRuleDTO, doc));
            }
        } else {
            BasicRuleDTO basicRuleDTO = new BasicRuleDTO();
            basicRuleDTO.setRuleId(UUID.randomUUID().toString());
            basicRuleDTO.setRuleEffect(PolicyConstants.RuleEffect.DENY);
            policyElement.appendChild(BasicPolicyHelper.createRuleElement(basicRuleDTO,doc));
        }

        return policyElement;
    }

    public static Element createRuleElement(BasicRuleDTO basicRuleDTO,
                                                    Document doc) throws PolicyBuilderException {

        String functionOnResources =  basicRuleDTO.getFunctionOnResources();
        String functionOnSubjects = basicRuleDTO.getFunctionOnSubjects();
        String functionOnActions = basicRuleDTO.getFunctionOnActions();
        String functionOnEnvironment = basicRuleDTO.getFunctionOnEnvironment();
        String preFunctionOnResources =  basicRuleDTO.getPreFunctionOnResources();
        String preFunctionOnSubjects = basicRuleDTO.getPreFunctionOnSubjects();
        String preFunctionOnActions = basicRuleDTO.getPreFunctionOnActions();
        String preFunctionOnEnvironment = basicRuleDTO.getPreFunctionOnEnvironment();
        String resourceNames = basicRuleDTO.getResourceList();
        String actionNames = basicRuleDTO.getActionList();
        String subjectNames = basicRuleDTO.getSubjectList();
        String environmentNames = basicRuleDTO.getEnvironmentList();
        String resourceId = basicRuleDTO.getResourceId();
        String subjectId = basicRuleDTO.getSubjectId();
        String actionId = basicRuleDTO.getActionId();
        String environmentId = basicRuleDTO.getEnvironmentId();
        String resourceDataType = basicRuleDTO.getResourceDataType();
        String subjectDataType = basicRuleDTO.getSubjectDataType();
        String actionDataType = basicRuleDTO.getActionDataType();
        String environmentDataType = basicRuleDTO.getEnvironmentDataType();


        Element resourcesElement = null;
        Element actionsElement = null;
        Element subjectsElement = null;
        Element environmentsElement = null;
        Element targetElement = null;
        Element applyElement = null;
        Element conditionElement = null;
        Element ruleElement =  null ;

        ApplyElementDTO rootApplyElementDTO = new ApplyElementDTO();
        ApplyElementDTO subjectApplyElementDTO = null;
        ApplyElementDTO resourceApplyElementDTO = null;
        ApplyElementDTO actionApplyElementDTO = null;
        ApplyElementDTO environmentApplyElementDTO = null;
        List<ApplyElementDTO> applyElementDTOs = new ArrayList<ApplyElementDTO>();

        if(resourceNames != null  && resourceNames.trim().length() > 0) {
            String[] resources = resourceNames.split(PolicyConstants.ATTRIBUTE_SEPARATOR);
            if(resourceId == null || resourceId.trim().length() < 1){
                resourceId = PolicyConstants.RESOURCE_ID;
            }
            
            boolean notPrefix = false;
            if(PolicyConstants.PreFunctions.PRE_FUNCTION_NOT.equals(preFunctionOnResources)){
                notPrefix = true;
            }
            if(Arrays.asList(PolicyConstants.Functions.targetFunctions).contains(functionOnResources)
                    && !notPrefix){

                resourcesElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
                Element resourceElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(functionOnResources,
                        resources[0].trim(), PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    resourceElement.appendChild(matchElement);
                }
                resourcesElement.appendChild(resourceElement);

            } else if(Arrays.asList(PolicyConstants.Functions.advanceRuleFunctions).contains(functionOnResources)){

                AttributeDesignatorDTO designatorDTO = new AttributeDesignatorDTO();
                designatorDTO.setCategory(PolicyConstants.RESOURCE_CATEGORY_URI);
                designatorDTO.setAttributeId(resourceId);
                designatorDTO.setDataType(resourceDataType);
                designatorDTO.setMustBePresent("true");
                resourceApplyElementDTO = processGreaterLessThanFunctions(functionOnResources,
                        resourceDataType, resourceNames, designatorDTO);

            } else if(Arrays.asList(PolicyConstants.Functions.simpleBagRuleFunctions).contains(functionOnResources)){
                resourceApplyElementDTO = createApplyElementForBagFunctions(functionOnResources,
                        PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resources, resourceDataType);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleRuleFunctions).contains(functionOnSubjects)){
                resourceApplyElementDTO = createApplyElementForNonBagFunctions(functionOnResources,
                        PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resources[0].trim(), resourceDataType);
            }

            if(resourceApplyElementDTO != null){
                if(notPrefix){
                    ApplyElementDTO notElementDTO = new ApplyElementDTO();
                    notElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_NOT);
                    notElementDTO.setApplyElement(resourceApplyElementDTO);
                    resourceApplyElementDTO = notElementDTO;
                }
                applyElementDTOs.add(resourceApplyElementDTO);
            }

        }

        if(actionNames != null  && actionNames.trim().length() > 0) {

            String[] actions = actionNames.split(PolicyConstants.ATTRIBUTE_SEPARATOR);
            if(actionId == null || actionId.trim().length() < 1){
                actionId = PolicyConstants.ACTION_ID;
            }

            boolean notPrefix = false;
            if(PolicyConstants.PreFunctions.PRE_FUNCTION_NOT.equals(preFunctionOnActions)){
                notPrefix = true;
            }

            if(Arrays.asList(PolicyConstants.Functions.targetFunctions).contains(functionOnActions)
                    && !notPrefix){
                actionsElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
                Element actionElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(functionOnActions,
                        actions[0].trim(), PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    actionElement.appendChild(matchElement);
                }
                actionsElement.appendChild(actionElement);
            } else if(Arrays.asList(PolicyConstants.Functions.advanceRuleFunctions).contains(functionOnActions)){

                AttributeDesignatorDTO designatorDTO = new AttributeDesignatorDTO();
                designatorDTO.setCategory(PolicyConstants.ACTION_CATEGORY_URI);
                designatorDTO.setAttributeId(actionId);
                designatorDTO.setDataType(actionDataType);
                designatorDTO.setMustBePresent("true");
                actionApplyElementDTO = processGreaterLessThanFunctions(functionOnActions,
                        actionDataType, actionNames, designatorDTO);   //TODO
            } else if(Arrays.asList(PolicyConstants.Functions.simpleBagRuleFunctions).contains(functionOnActions)){
                actionApplyElementDTO = createApplyElementForBagFunctions(functionOnActions,
                        PolicyConstants.ACTION_CATEGORY_URI, actionId, actions, actionDataType);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleRuleFunctions).contains(functionOnActions)){
                actionApplyElementDTO = createApplyElementForNonBagFunctions(functionOnActions,
                        PolicyConstants.ACTION_CATEGORY_URI, actionId, actions[0].trim(), actionDataType);
            }

            if(actionApplyElementDTO != null){
                if(notPrefix){
                    ApplyElementDTO notElementDTO = new ApplyElementDTO();
                    notElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_NOT);
                    notElementDTO.setApplyElement(actionApplyElementDTO);
                    actionApplyElementDTO = notElementDTO;
                }
                applyElementDTOs.add(actionApplyElementDTO);
            }
        }

        if(environmentNames != null  && environmentNames.trim().length() > 0) {

            String[] environments = environmentNames.split(PolicyConstants.ATTRIBUTE_SEPARATOR);
            if(environmentId == null || environmentId.trim().length() < 1){
                environmentId = PolicyConstants.ENVIRONMENT_ID;
            }

            boolean notPrefix = false;
            if(PolicyConstants.PreFunctions.PRE_FUNCTION_NOT.equals(preFunctionOnEnvironment)){
                notPrefix = true;
            }

            if(Arrays.asList(PolicyConstants.Functions.targetFunctions).contains(functionOnEnvironment)
                    && !notPrefix){
                environmentsElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
                Element environmentElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(functionOnEnvironment,
                        environments[0].trim(), PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    environmentElement.appendChild(matchElement);
                }
                environmentsElement.appendChild(environmentElement);
            } else if(Arrays.asList(PolicyConstants.Functions.advanceRuleFunctions).contains(functionOnEnvironment)){

                AttributeDesignatorDTO designatorDTO = new AttributeDesignatorDTO();
                designatorDTO.setCategory(PolicyConstants.ENVIRONMENT_CATEGORY_URI);
                designatorDTO.setAttributeId(environmentId);
                designatorDTO.setDataType(environmentDataType);
                designatorDTO.setMustBePresent("true");
                environmentApplyElementDTO = processGreaterLessThanFunctions(functionOnEnvironment,
                        environmentDataType, environmentNames, designatorDTO);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleBagRuleFunctions).contains(functionOnEnvironment)){
                environmentApplyElementDTO = createApplyElementForBagFunctions(functionOnEnvironment,
                        PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environments, environmentDataType);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleRuleFunctions).contains(functionOnEnvironment)){
                environmentApplyElementDTO = createApplyElementForNonBagFunctions(functionOnEnvironment,
                        PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId,
                        environments[0].trim(), environmentDataType);

            }

            if(environmentApplyElementDTO != null){
                if(notPrefix){
                    ApplyElementDTO notElementDTO = new ApplyElementDTO();
                    notElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_NOT);
                    notElementDTO.setApplyElement(environmentApplyElementDTO);
                    environmentApplyElementDTO = notElementDTO;
                }
                applyElementDTOs.add(environmentApplyElementDTO);
            }
        }

        if(subjectNames != null  && subjectNames.trim().length() > 0) {

            String[] subjects = subjectNames.split(PolicyConstants.ATTRIBUTE_SEPARATOR);
            if(subjectId == null || subjectId.trim().length() < 1){
                subjectId = PolicyConstants.SUBJECT_ID_DEFAULT;
            }

            boolean notPrefix = false;
            if(PolicyConstants.PreFunctions.PRE_FUNCTION_NOT.equals(preFunctionOnSubjects)){
                notPrefix = true;
            }

            if(Arrays.asList(PolicyConstants.Functions.targetFunctions).contains(functionOnSubjects)){
                subjectApplyElementDTO = createApplyElementForNonBagFunctionsWithAnyOf(functionOnSubjects,
                        PolicyConstants.SUBJECT_CATEGORY_URI,subjectId, subjectDataType, subjects[0].trim());

            } else if(Arrays.asList(PolicyConstants.Functions.advanceRuleFunctions).contains(functionOnSubjects)){

                AttributeDesignatorDTO designatorDTO = new AttributeDesignatorDTO();
                designatorDTO.setCategory(PolicyConstants.SUBJECT_CATEGORY_URI);
                designatorDTO.setAttributeId(subjectId);
                designatorDTO.setDataType(subjectDataType);
                designatorDTO.setMustBePresent("true");
                subjectApplyElementDTO = processGreaterLessThanFunctions(functionOnSubjects, subjectDataType,
                                subjectNames, designatorDTO);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleBagRuleFunctions).contains(functionOnSubjects)){
                subjectApplyElementDTO = createApplyElementForBagFunctions(functionOnSubjects,
                        PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjects, subjectDataType);
            } else if(Arrays.asList(PolicyConstants.Functions.simpleRuleFunctions).contains(functionOnSubjects)){
                subjectApplyElementDTO = createApplyElementForNonBagFunctions(functionOnSubjects,
                        PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjects[0].trim(), subjectDataType);
            }

            if(subjectApplyElementDTO != null){
                if(notPrefix){
                    ApplyElementDTO notElementDTO = new ApplyElementDTO();
                    notElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_NOT);
                    notElementDTO.setApplyElement(subjectApplyElementDTO);
                    subjectApplyElementDTO = notElementDTO;
                }
                applyElementDTOs.add(subjectApplyElementDTO);
            }
        }

        if(applyElementDTOs.size() > 1) {
            if(applyElementDTOs.size()  == 2){
                rootApplyElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                rootApplyElementDTO.setApplyElement(applyElementDTOs.get(0));
                rootApplyElementDTO.setApplyElement(applyElementDTOs.get(1));
                applyElement = PolicyUtils.createApplyElement(rootApplyElementDTO, doc);
            } else if(applyElementDTOs.size()  == 3){
                rootApplyElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                ApplyElementDTO andElementDTO = new ApplyElementDTO();
                andElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                andElementDTO.setApplyElement(applyElementDTOs.get(0));
                andElementDTO.setApplyElement(applyElementDTOs.get(1));
                rootApplyElementDTO.setApplyElement(andElementDTO);
                rootApplyElementDTO.setApplyElement(applyElementDTOs.get(2));
                applyElement = PolicyUtils.createApplyElement(rootApplyElementDTO, doc);
            } else if(applyElementDTOs.size() == 4){
                rootApplyElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                ApplyElementDTO andElementDTO1 = new ApplyElementDTO();
                andElementDTO1.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                andElementDTO1.setApplyElement(applyElementDTOs.get(0));
                andElementDTO1.setApplyElement(applyElementDTOs.get(1));
                ApplyElementDTO andElementDTO2 = new ApplyElementDTO();
                andElementDTO2.setFunctionId(PolicyConstants.XACMLData.FUNCTION_AND);
                andElementDTO2.setApplyElement(applyElementDTOs.get(2));
                andElementDTO2.setApplyElement(applyElementDTOs.get(3));
                rootApplyElementDTO.setApplyElement(andElementDTO1);
                rootApplyElementDTO.setApplyElement(andElementDTO2);
                applyElement = PolicyUtils.createApplyElement(rootApplyElementDTO, doc);
            }
        } else if(applyElementDTOs.size() == 1){
            applyElement = PolicyUtils.createApplyElement(applyElementDTOs.get(0), doc);
        }

        if(resourcesElement != null || actionsElement != null || subjectsElement != null ||
                environmentsElement != null) {
            targetElement = doc.createElement(PolicyConstants.TARGET_ELEMENT);
            if(resourcesElement != null) {
                targetElement.appendChild(resourcesElement);
            }
            if(actionsElement != null) {
                targetElement.appendChild(actionsElement);
            }
            if(subjectsElement != null) {
                targetElement.appendChild(subjectsElement);
            }

            if(environmentsElement != null){
                targetElement.appendChild(environmentsElement);
            }
        }

        if(applyElement != null) {
            conditionElement = doc.createElement(PolicyConstants.CONDITION_ELEMENT);
            conditionElement.appendChild(applyElement);
        }

        if(basicRuleDTO.getRuleId() != null && basicRuleDTO.getRuleId().trim().length() > 0 &&
                basicRuleDTO.getRuleEffect() != null && basicRuleDTO.getRuleEffect().
                trim().length() > 0){

            ruleElement = doc.createElement(PolicyConstants.RULE_ELEMENT);
            ruleElement.setAttribute(PolicyConstants.RULE_ID, basicRuleDTO.
                    getRuleId());
            ruleElement.setAttribute(PolicyConstants.RULE_EFFECT,
                    basicRuleDTO.getRuleEffect());

            if(basicRuleDTO.getRuleDescription() != null && basicRuleDTO.
                    getRuleDescription().trim().length() > 0){
                ruleElement.setAttribute(PolicyConstants.RULE_DESCRIPTION,
                        basicRuleDTO.getRuleDescription());
            }

            if(targetElement != null) {
                ruleElement.appendChild(targetElement);
            }
            if(conditionElement != null) {
                ruleElement.appendChild(conditionElement);
            }
        }

        return ruleElement;
    }


    public static Element createTargetElement(BasicTargetDTO basicTargetDTO,
                                                      Document doc) throws PolicyBuilderException {

        //TODO
        String functionOnResources =  basicTargetDTO.getFunctionOnResources();
        String functionOnSubjects = basicTargetDTO.getFunctionOnSubjects();
        String functionOnActions = basicTargetDTO.getFunctionOnActions();
        String functionOnEnvironment = basicTargetDTO.getFunctionOnEnvironment();
        String resourceNames = basicTargetDTO.getResourceList();
        String actionNames = basicTargetDTO.getActionList();
        String subjectNames = basicTargetDTO.getSubjectList();
        String environmentNames = basicTargetDTO.getEnvironmentList();
        String resourceId = basicTargetDTO.getResourceId();
        String subjectId = basicTargetDTO.getSubjectId();
        String actionId = basicTargetDTO.getActionId();
        String environmentId = basicTargetDTO.getEnvironmentId();
        String resourceDataType = basicTargetDTO.getResourceDataType();
        String subjectDataType = basicTargetDTO.getSubjectDataType();
        String actionDataType = basicTargetDTO.getActionDataType();
        String environmentDataType = basicTargetDTO.getEnvironmentDataType();

        Element resourcesElement = null;
        Element actionsElement = null;
        Element subjectsElement = null;
        Element environmentsElement = null;
        Element targetElement = doc.createElement(PolicyConstants.TARGET_ELEMENT);

        if(resourceNames != null  && resourceNames.trim().length() > 0) {
            resourcesElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
            Element resourceElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
            String[] resources = resourceNames.split(PolicyConstants.ATTRIBUTE_SEPARATOR);
            if(resourceId == null || resourceId.trim().length() < 1) {
                resourceId = PolicyConstants.RESOURCE_ID;
            }
            if(functionOnResources.equals(PolicyConstants.Functions.FUNCTION_EQUAL) ||
                    functionOnResources.equals(PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP) ) {
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(functionOnResources,
                        resources[0].trim(), PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    resourceElement.appendChild(matchElement);
                }
                resourcesElement.appendChild(resourceElement);
            } else if(functionOnResources.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE)) {
                for(String resource : resources){
                    resource = resource.trim();
                    Element resourceEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            PolicyConstants.Functions.FUNCTION_EQUAL,
                            resource, PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        resourceEle.appendChild(matchElement);
                    }
                    resourcesElement.appendChild(resourceEle);
                }
            } else if(functionOnResources.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE_MATCH_REGEXP)) {
                for(String resource : resources){
                    resource = resource.trim();
                    Element resourceEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            resource, PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        resourceEle.appendChild(matchElement);
                    }
                    resourcesElement.appendChild(resourceEle);
                }
            } else if(functionOnResources.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS)) {
                for(String resource : resources){
                    resource = resource.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            resource, PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        resourceElement.appendChild(matchElement);
                    }
                }
                resourcesElement.appendChild(resourceElement);
            }else if(functionOnResources.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS_MATCH_REGEXP)) {
                for(String resource : resources){
                    resource = resource.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            resource, PolicyConstants.RESOURCE_CATEGORY_URI, resourceId, resourceDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        resourceElement.appendChild(matchElement);
                    }
                }
                resourcesElement.appendChild(resourceElement);
            }
        }

        if(actionNames != null  && actionNames.trim().length() > 0) {
            actionsElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
            Element actionElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
            String[] actions = actionNames.split(",");
            if(actionId == null || actionId.trim().length() < 1) {
                actionId = PolicyConstants.ACTION_ID;
            }
            if(functionOnActions.equals(PolicyConstants.Functions.FUNCTION_EQUAL) ||
                    functionOnActions.equals(PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP)) {
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                        (functionOnActions),
                        actions[0].trim(), PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    actionElement.appendChild(matchElement);
                }
                actionsElement.appendChild(actionElement);
            } else if(functionOnActions.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE)) {
                for(String action : actions){
                    action = action.trim();
                    Element actionEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            action, PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        actionEle.appendChild(matchElement);
                    }
                    actionsElement.appendChild(actionEle);
                }
            } else if(functionOnActions.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE_MATCH_REGEXP)) {
                for(String action : actions){
                    action = action.trim();
                    Element actionEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            action, PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        actionEle.appendChild(matchElement);
                    }
                    actionsElement.appendChild(actionEle);
                }
            } else if(functionOnActions.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS_MATCH_REGEXP)) {
                for(String action : actions){
                    action = action.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            action, PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        actionElement.appendChild(matchElement);
                    }
                }
                actionsElement.appendChild(actionElement);
            } else if(functionOnActions.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS)) {
                for(String action : actions){
                    action = action.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            action, PolicyConstants.ACTION_CATEGORY_URI, actionId, actionDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        actionElement.appendChild(matchElement);
                    }
                }
                actionsElement.appendChild(actionElement);
            }

        }

        if(environmentNames != null  && environmentNames.trim().length() > 0) {
            environmentsElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
            Element environmentElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
            String[] environments = environmentNames.split(",");
            if(environmentId == null || environmentId.trim().length() < 1) {
                environmentId = PolicyConstants.ENVIRONMENT_ID;
            }
            if(functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_EQUAL) ||
                    functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP)) {
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                        (functionOnEnvironment),
                        environments[0].trim(), PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    environmentElement.appendChild(matchElement);
                }
                environmentsElement.appendChild(environmentElement);
            } else if(functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE)) {
                for(String environment : environments){
                    environment = environment.trim();
                    Element environmentEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            environment, PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        environmentEle.appendChild(matchElement);
                    }
                    environmentsElement.appendChild(environmentEle);
                }
            } else if(functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE_MATCH_REGEXP)) {
                for(String environment : environments){
                    environment = environment.trim();
                    Element environmentEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            environment, PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        environmentEle.appendChild(matchElement);
                    }
                    environmentsElement.appendChild(environmentEle);
                }
            }else if(functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS_MATCH_REGEXP)) {
                for(String environment : environments){
                    environment = environment.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            environment, PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        environmentElement.appendChild(matchElement);
                    }
                }
                environmentsElement.appendChild(environmentElement);
            }else if(functionOnEnvironment.equals(PolicyConstants.Functions.FUNCTION_SET_EQUALS)) {
                for(String environment : environments){
                    environment = environment.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            environment, PolicyConstants.ENVIRONMENT_CATEGORY_URI, environmentId, environmentDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        environmentElement.appendChild(matchElement);
                    }
                }
                environmentsElement.appendChild(environmentElement);
            }
        }

        if(subjectNames != null  && subjectNames.trim().length() > 0) {
            subjectsElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
            Element subjectElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
            String[] subjects = subjectNames.split(",");
            if(subjectId == null || subjectId.trim().length() < 1){
                subjectId = PolicyConstants.SUBJECT_ID_DEFAULT;
            }

            if(PolicyConstants.Functions.FUNCTION_EQUAL.equals(functionOnSubjects) ||
                    PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP.equals(functionOnSubjects)) {
                MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                        (functionOnSubjects),
                        subjects[0].trim(), PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjectDataType);
                Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                if(matchElement != null){
                    subjectElement.appendChild(matchElement);
                }
                subjectsElement.appendChild(subjectElement);
            } else if(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE.equals(functionOnSubjects)){
                for(String subject : subjects){
                    subject = subject.trim();
                    Element subjectEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            subject, PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjectDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        subjectEle.appendChild(matchElement);
                    }
                    subjectsElement.appendChild(subjectEle);
                }
            } else if(PolicyConstants.Functions.FUNCTION_AT_LEAST_ONE_MATCH_REGEXP.equals(functionOnSubjects)){
                for(String subject : subjects){
                    subject = subject.trim();
                    Element subjectEle = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            subject, PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjectDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        subjectEle.appendChild(matchElement);
                    }
                    subjectsElement.appendChild(subjectEle);
                }
            } else if(PolicyConstants.Functions.FUNCTION_SET_EQUALS.equals(functionOnSubjects)){
                for(String subject : subjects){
                    subject = subject.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL),
                            subject, PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjectDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        subjectElement.appendChild(matchElement);
                    }
                }
                subjectsElement.appendChild(subjectElement);
            } else if(PolicyConstants.Functions.FUNCTION_SET_EQUALS_MATCH_REGEXP.equals(functionOnSubjects)){
                for(String subject : subjects){
                    subject = subject.trim();
                    MatchElementDTO matchElementDTO = createMatchElementForNonBagFunctions(
                            (PolicyConstants.Functions.FUNCTION_EQUAL_MATCH_REGEXP),
                            subject, PolicyConstants.SUBJECT_CATEGORY_URI, subjectId, subjectDataType);
                    Element matchElement= PolicyUtils.createMatchElement(matchElementDTO, doc);
                    if(matchElement != null){
                        subjectElement.appendChild(matchElement);
                    }
                }
                subjectsElement.appendChild(subjectElement);
            }
        }

        if(resourcesElement != null) {
            targetElement.appendChild(resourcesElement);
        }
        if(actionsElement != null) {
            targetElement.appendChild(actionsElement);
        }
        if(subjectsElement != null) {
            targetElement.appendChild(subjectsElement);
        }

        if(environmentsElement != null){
            targetElement.appendChild(environmentsElement);
        }

        return targetElement;
    }


    public static ApplyElementDTO createApplyElementForBagFunctions(String functionId,
                                                                    String category,
                                                                    String attributeId,
                                                                    String[] attributeValues,
                                                                    String dataType){

        ApplyElementDTO applyElementDTO = new ApplyElementDTO();
        functionId = processFunction(functionId, dataType);
        if(attributeValues != null && functionId != null && functionId.trim().length() > 0 &&
                category != null && category.trim().length() > 0 &&
                attributeId != null && attributeId.trim().length() > 0){

            ApplyElementDTO applyElementDTOBag = new ApplyElementDTO();
            for(String attributeValue :attributeValues){
                attributeValue = attributeValue.trim();
                AttributeValueElementDTO attributeValueElementDTO = new AttributeValueElementDTO();
                if(dataType != null && dataType.trim().length() > 0){
                    attributeValueElementDTO.setAttributeDataType(dataType);
                } else {
                    attributeValueElementDTO.setAttributeDataType(PolicyConstants.STRING_DATA_TYPE);
                }
                attributeValueElementDTO.setAttributeValue(attributeValue.trim());
                applyElementDTOBag.setAttributeValueElementDTO(attributeValueElementDTO);
            }

            applyElementDTOBag.setFunctionId(PolicyConstants.XACMLData.FUNCTION_BAG);

            AttributeDesignatorDTO attributeDesignatorDTO = new AttributeDesignatorDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeDesignatorDTO.setDataType(dataType);
            } else {
                attributeDesignatorDTO.setDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeDesignatorDTO.setAttributeId(attributeId);
            attributeDesignatorDTO.setCategory(category);

            applyElementDTO.setApplyElement(applyElementDTOBag);
            applyElementDTO.setAttributeDesignators(attributeDesignatorDTO);
            applyElementDTO.setFunctionId(functionId);

        }

        return applyElementDTO;
    }

    public static ApplyElementDTO createApplyElementForNonBagFunctions(String functionId,
                                                                       String category,
                                                                       String attributeId,
                                                                       String attributeValue,
                                                                       String dataType){

        ApplyElementDTO applyElementDTO = new ApplyElementDTO();
        functionId = processFunction(functionId, dataType);
        if(attributeValue != null && attributeValue.trim().length() > 0 && functionId != null &&
                functionId.trim().length() > 0 && category != null &&
                category.trim().length() > 0 && attributeId != null &&
                attributeId.trim().length() > 0) {

            AttributeValueElementDTO attributeValueElementDTO = new AttributeValueElementDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeValueElementDTO.setAttributeDataType(dataType);
            } else {
                attributeValueElementDTO.setAttributeDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeValueElementDTO.setAttributeValue(attributeValue.trim());

            AttributeDesignatorDTO attributeDesignatorDTO = new AttributeDesignatorDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeDesignatorDTO.setDataType(dataType);
            } else {
                attributeDesignatorDTO.setDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeDesignatorDTO.setAttributeId(attributeId);
            attributeDesignatorDTO.setCategory(category);

            applyElementDTO.setAttributeValueElementDTO(attributeValueElementDTO);
            applyElementDTO.setAttributeDesignators(attributeDesignatorDTO);
            applyElementDTO.setFunctionId(functionId);

        }

        return applyElementDTO;
    }

    public static ApplyElementDTO createApplyElementForNonBagFunctionsWithAnyOf(String functionId,
                                                                                String category,
                                                                                String attributeDesignatorId,
                                                                                String dataType,
                                                                                String attributeValue){
        ApplyElementDTO applyElementDTO = null;
        functionId = processFunction(functionId, dataType);
        if(attributeValue != null && attributeValue.trim().length() > 0 && functionId != null &&
                functionId.trim().length() > 0 && category != null &&
                category.trim().length() > 0 && attributeDesignatorId != null &&
                attributeDesignatorId.trim().length() > 0) {
            applyElementDTO = new ApplyElementDTO();
            AttributeValueElementDTO attributeValueElementDTO = new AttributeValueElementDTO();
            attributeValueElementDTO.setAttributeDataType(PolicyConstants.STRING_DATA_TYPE);
            if(dataType != null && dataType.trim().length() > 0){
                attributeValueElementDTO.setAttributeDataType(dataType);
            } else {
                attributeValueElementDTO.setAttributeDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeValueElementDTO.setAttributeValue(attributeValue.trim());

            AttributeDesignatorDTO attributeDesignatorDTO = new AttributeDesignatorDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeDesignatorDTO.setDataType(dataType);
            } else {
                attributeDesignatorDTO.setDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeDesignatorDTO.setAttributeId(attributeDesignatorId);
            attributeDesignatorDTO.setCategory(category);

            applyElementDTO.setFunctionFunctionId(functionId);
            applyElementDTO.setAttributeValueElementDTO(attributeValueElementDTO);
            applyElementDTO.setAttributeDesignators(attributeDesignatorDTO);
            applyElementDTO.setFunctionId(PolicyConstants.XACMLData.FUNCTION_ANY_OF);

        }

        return applyElementDTO;
    }


    public static MatchElementDTO createMatchElementForNonBagFunctions(String functionId,
                                                                       String attributeValue,
                                                                       String category,
                                                                       String attributeId,
                                                                       String dataType) {

        MatchElementDTO matchElementDTO = new MatchElementDTO();
        functionId = processFunction(functionId, dataType);
        if(functionId != null && functionId.trim().length() > 0 && attributeValue != null &&
                attributeValue.trim().length() > 0&& category != null &&
                category.trim().length() > 0 && attributeId != null &&
                attributeId.trim().length() > 0) {
            AttributeValueElementDTO attributeValueElementDTO = new AttributeValueElementDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeValueElementDTO.setAttributeDataType(dataType);
            } else {
                attributeValueElementDTO.setAttributeDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeValueElementDTO.setAttributeValue(attributeValue.trim());

            AttributeDesignatorDTO attributeDesignatorDTO = new AttributeDesignatorDTO();
            if(dataType != null && dataType.trim().length() > 0){
                attributeDesignatorDTO.setDataType(dataType);
            } else {
                attributeDesignatorDTO.setDataType(PolicyConstants.STRING_DATA_TYPE);
            }
            attributeDesignatorDTO.setAttributeId(attributeId);
            attributeDesignatorDTO.setCategory(category);

            matchElementDTO.setMatchId(functionId);
            matchElementDTO.setAttributeValueElementDTO(attributeValueElementDTO);
            matchElementDTO.setAttributeDesignatorDTO(attributeDesignatorDTO);
        }

        return matchElementDTO;
    }


    /**
     * Process less than and greater than functions
     *
     * @param function
     * @param dataType
     * @param attributeValue
     * @param designatorDTO
     * @return
     * @throws PolicyBuilderException
     */
    public static ApplyElementDTO processGreaterLessThanFunctions(String function,
                                      String dataType, String attributeValue,
                                      AttributeDesignatorDTO designatorDTO) throws PolicyBuilderException {

        String[] values = attributeValue.split(PolicyConstants.ATTRIBUTE_SEPARATOR);


        if(PolicyConstants.Functions.FUNCTION_GREATER_EQUAL_AND_LESS_EQUAL.equals(function) ||
                PolicyConstants.Functions.FUNCTION_GREATER_AND_LESS_EQUAL.equals(function) ||
                PolicyConstants.Functions.FUNCTION_GREATER_EQUAL_AND_LESS.equals(function) ||
                PolicyConstants.Functions.FUNCTION_GREATER_AND_LESS.equals(function)) {

            String leftValue;
            String rightValue;

            if(values.length == 2){
                leftValue = values[0].trim();
                rightValue = values[1].trim();
            } else {
                throw new PolicyBuilderException("Can not create Apply element:" +
                        "Missing required attribute values for function : " + function);
            }

            ApplyElementDTO andApplyElement = new ApplyElementDTO();

            andApplyElement.setFunctionId(processFunction("and"));

            ApplyElementDTO greaterThanApplyElement = new ApplyElementDTO();
            if(PolicyConstants.Functions.FUNCTION_GREATER_AND_LESS.equals(function) ||
                    PolicyConstants.Functions.FUNCTION_GREATER_AND_LESS_EQUAL.equals(function)){
                greaterThanApplyElement.setFunctionId(processFunction("greater-than", dataType));
            } else {
                greaterThanApplyElement.setFunctionId(processFunction("greater-than-or-equal", dataType));
            }


            ApplyElementDTO lessThanApplyElement = new ApplyElementDTO();
            if(PolicyConstants.Functions.FUNCTION_GREATER_AND_LESS.equals(function) ||
                    PolicyConstants.Functions.FUNCTION_GREATER_EQUAL_AND_LESS.equals(function)){
                lessThanApplyElement.setFunctionId(processFunction("less-than", dataType));
            } else {
                lessThanApplyElement.setFunctionId(processFunction("less-than-or-equal", dataType));
            }

            ApplyElementDTO oneAndOnlyApplyElement = new ApplyElementDTO();
            oneAndOnlyApplyElement.setFunctionId(processFunction("one-and-only", dataType));
            oneAndOnlyApplyElement.setAttributeDesignators(designatorDTO);

            AttributeValueElementDTO leftValueElementDTO = new AttributeValueElementDTO();
            leftValueElementDTO.setAttributeDataType(dataType);
            leftValueElementDTO.setAttributeValue(leftValue);

            AttributeValueElementDTO rightValueElementDTO = new AttributeValueElementDTO();
            rightValueElementDTO.setAttributeDataType(dataType);
            rightValueElementDTO.setAttributeValue(rightValue);

            greaterThanApplyElement.setApplyElement(oneAndOnlyApplyElement);
            greaterThanApplyElement.setAttributeValueElementDTO(leftValueElementDTO);

            lessThanApplyElement.setApplyElement(oneAndOnlyApplyElement);
            lessThanApplyElement.setAttributeValueElementDTO(rightValueElementDTO);

            andApplyElement.setApplyElement(greaterThanApplyElement);
            andApplyElement.setApplyElement(lessThanApplyElement);

            return andApplyElement;

        } else {

            ApplyElementDTO applyElementDTO = new ApplyElementDTO();

            if(PolicyConstants.Functions.FUNCTION_GREATER.equals(function)){
                applyElementDTO.setFunctionId(processFunction("greater-than", dataType));
            } else if(PolicyConstants.Functions.FUNCTION_GREATER_EQUAL.equals(function)){
                applyElementDTO.setFunctionId(processFunction("greater-than-or-equal", dataType));
            } else if(PolicyConstants.Functions.FUNCTION_LESS.equals(function)){
                applyElementDTO.setFunctionId(processFunction("less-than", dataType));
            } else if(PolicyConstants.Functions.FUNCTION_LESS_EQUAL.equals(function)){
                applyElementDTO.setFunctionId(processFunction("less-than-or-equal", dataType));
            } else {
                throw new PolicyBuilderException("Can not create Apply element:" +
                        "Invalid function : " + function);
            }

            ApplyElementDTO oneAndOnlyApplyElement = new ApplyElementDTO();
            oneAndOnlyApplyElement.setFunctionId(processFunction("one-and-only", dataType));
            oneAndOnlyApplyElement.setAttributeDesignators(designatorDTO);

            AttributeValueElementDTO valueElementDTO = new AttributeValueElementDTO();
            valueElementDTO.setAttributeDataType(dataType);
            valueElementDTO.setAttributeValue(values[0]);

            applyElementDTO.setApplyElement(oneAndOnlyApplyElement);
            applyElementDTO.setAttributeValueElementDTO(valueElementDTO);

            return  applyElementDTO;

        }
    }

    /**
     * Helper method to create full XACML function URI
     *
     * @param function
     * @param type
     * @param version
     * @return
     */
    private static String processFunction(String function, String type, String version){
        return  "urn:oasis:names:tc:xacml:" + version + ":function:" + getDataTypePrefix(type) +
                "-" + function;
    }

    /**
     * Helper method to create full XACML function URI
     *
     * @param function
     * @return
     */
    private static String processFunction(String function){
        return "urn:oasis:names:tc:xacml:1.0:function:" + function;
    }

    /**
     * Helper method to create full XACML function URI
     *
     * @param function
     * @param type
     * @return
     */
    private static String processFunction(String function, String type){
        if(type == null || type.trim().length() == 0){
            type = PolicyConstants.STRING_DATA_TYPE;
        }
        return  "urn:oasis:names:tc:xacml:1.0:function:" + getDataTypePrefix(type) + "-" + function;
    }

    /**
     * Helper method to create full XACML function URI
     *
     * @param dataTypeUri
     * @return
     */
    private static String getDataTypePrefix(String dataTypeUri){

        if(dataTypeUri != null){
            if(dataTypeUri.contains("#")){
                return dataTypeUri.substring(dataTypeUri.indexOf("#") + 1);
            } else if(dataTypeUri.contains(":")){
                String[] stringArray = dataTypeUri.split(":");
                if(stringArray != null && stringArray.length > 0){
                    return stringArray[stringArray.length - 1];
                }
            }
        }
        return dataTypeUri;
    }
}
