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

package org.wso2.balana.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.utils.Constants.PolicyConstants;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 */
public class PolicyUtils {

    private static final Log log = LogFactory.getLog(PolicyUtils.class);

    /**
     * This method creates a policy element of the XACML policy
     * @param policyElementDTO  policy element data object
     * @param doc XML document
     * @return policyElement
     * @throws PolicyBuilderException if
     */

    public static Element createPolicyElement(PolicyElementDTO policyElementDTO, Document doc)
                                                                    throws PolicyBuilderException {

        Element policyElement = doc.createElement(PolicyConstants.POLICY_ELEMENT);

        policyElement.setAttribute("xmlns", PolicyConstants.XACMLData.XACML3_POLICY_NAMESPACE);

        if(policyElementDTO.getPolicyName() != null && policyElementDTO.getPolicyName().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.POLICY_ID, policyElementDTO.
                    getPolicyName());
        } else {
            throw new PolicyBuilderException("Policy name can not be null");
        }

        if(policyElementDTO.getRuleCombiningAlgorithms() != null && policyElementDTO.
                getRuleCombiningAlgorithms().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.RULE_ALGORITHM, policyElementDTO.
                            getRuleCombiningAlgorithms());
        } else {
            policyElement.setAttribute(PolicyConstants.RULE_ALGORITHM,
                                                PolicyConstants.RuleCombiningAlog.DENY_OVERRIDE_ID); // TODO
            log.warn("Rule combining algorithm is not defined. Use default algorithm; Deny Override");
        }

        if(policyElementDTO.getVersion() != null && policyElementDTO.getVersion().trim().length() > 0){
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION,
                    policyElementDTO.getVersion());
        } else {
            // policy version is can be handled by policy registry.  therefore we can ignore it, although it
            // is a required attribute
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION, "1.0");
        }

        if(policyElementDTO.getPolicyDescription() != null && policyElementDTO.
                getPolicyDescription().trim().length() > 0) {

            Element descriptionElement = doc.createElement(PolicyConstants.
                    DESCRIPTION_ELEMENT);
            descriptionElement.setTextContent(policyElementDTO.getPolicyDescription());
            policyElement.appendChild(descriptionElement);
        }

        TargetElementDTO targetElementDTO = policyElementDTO.getTargetElementDTO();
        List<RuleElementDTO> ruleElementDTOs = policyElementDTO.getRuleElementDTOs();
        List<ObligationElementDTO> obligationElementDTOs = policyElementDTO.getObligationElementDTOs();

        if(targetElementDTO != null){
            policyElement.appendChild(createTargetElement(targetElementDTO, doc));
        } else {
            policyElement.appendChild(doc.createElement(PolicyConstants. TARGET_ELEMENT));
        }
        
        if(ruleElementDTOs != null && ruleElementDTOs.size() > 0) {
            for(RuleElementDTO ruleElementDTO : ruleElementDTOs) {
                policyElement.appendChild(createRuleElement(ruleElementDTO, doc));
            }
        } else {
            RuleElementDTO ruleElementDTO = new RuleElementDTO();
            ruleElementDTO.setRuleId(UUID.randomUUID().toString());
            ruleElementDTO.setRuleEffect(PolicyConstants.RuleEffect.DENY);
            policyElement.appendChild(createRuleElement(ruleElementDTO, doc));
        }

        if(obligationElementDTOs != null && obligationElementDTOs.size() > 0){
            List<ObligationElementDTO> obligations = new ArrayList<ObligationElementDTO>();
            List<ObligationElementDTO> advices = new ArrayList<ObligationElementDTO>();
            for(ObligationElementDTO obligationElementDTO : obligationElementDTOs){
                if(obligationElementDTO.getType() == ObligationElementDTO.ADVICE){
                    advices.add(obligationElementDTO);
                } else {
                    obligations.add(obligationElementDTO);
                }
            }
            Element obligation = createObligationsElement(obligations, doc);
            Element advice = createAdvicesElement(advices, doc);
            if(obligation != null){
                policyElement.appendChild(obligation);
            }
            if(advice != null){
                policyElement.appendChild(advice);
            }
        }

        return policyElement;
    }


    /**
     * This method creates a policy set element of the XACML policy
     * @param policyElementDTO  policy element data object
     * @param doc XML document
     * @return policyElement
     * @throws PolicyBuilderException if
     */

    public static Element createPolicySetElement(PolicySetElementDTO policyElementDTO, Document doc)
            throws PolicyBuilderException {

        Element policyElement = doc.createElement(PolicyConstants.POLICY_SET_ELEMENT);

        policyElement.setAttribute("xmlns", PolicyConstants.XACMLData.XACML3_POLICY_NAMESPACE);

        if(policyElementDTO.getPolicySetId() != null &&
                                    policyElementDTO.getPolicySetId().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.POLICY_SET_ID, policyElementDTO.
                    getPolicySetId());
        } else {
            throw new PolicyBuilderException("Policy name can not be null");
        }

        if(policyElementDTO.getPolicyCombiningAlgId() != null && policyElementDTO.
                getPolicyCombiningAlgId().trim().length() > 0) {
            policyElement.setAttribute(PolicyConstants.POLICY_ALGORITHM, policyElementDTO.
                    getPolicyCombiningAlgId());
        } else {
            policyElement.setAttribute(PolicyConstants.POLICY_ALGORITHM,
                    PolicyConstants.PolicyCombiningAlog.DENY_OVERRIDE_ID); // TODO
            log.warn("Rule combining algorithm is not defined. Use default algorithm; Deny Override");
        }

        if(policyElementDTO.getVersion() != null && policyElementDTO.getVersion().trim().length() > 0){
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION,
                    policyElementDTO.getVersion());
        } else {
            // policy version is can be handled by policy registry.  therefore we can ignore it, although it
            // is a required attribute
            policyElement.setAttribute(PolicyConstants.POLICY_VERSION, "1.0");
        }

        if(policyElementDTO.getDescription() != null && policyElementDTO.
                getDescription().trim().length() > 0) {

            Element descriptionElement = doc.createElement(PolicyConstants.
                    DESCRIPTION_ELEMENT);
            descriptionElement.setTextContent(policyElementDTO.getDescription());
            policyElement.appendChild(descriptionElement);
        }

        TargetElementDTO targetElementDTO = policyElementDTO.getTargetElementDTO();
        List<ObligationElementDTO> obligationElementDTOs = policyElementDTO.getObligationElementDTOs();

        if(targetElementDTO != null){
            policyElement.appendChild(createTargetElement(targetElementDTO, doc));
        } else {
            policyElement.appendChild(doc.createElement(PolicyConstants.TARGET_ELEMENT));
        }
        
        List<String> policySets = policyElementDTO.getPolicySets();
        if(policySets != null && policySets.size() > 0){
           // TODO 
        }

        List<String> policies = policyElementDTO.getPolicies();
        if(policies != null && policies.size() > 0){
            // TODO    
        }
        
        List<String> policySetIds = policyElementDTO.getPolicySetIdReferences();
        if(policySetIds != null && policySetIds.size() > 0){
            for(String policySetId : policySetIds){
                Element element = doc.createElement(PolicyConstants.POLICY_SET_ID_REFERENCE_ELEMENT);
                element.setTextContent(policySetId);
                policyElement.appendChild(element);
            }
        }

        List<String> policyIds = policyElementDTO.getPolicyIdReferences();
        if(policyIds != null && policyIds.size() > 0){
            for(String policyId : policyIds){
                Element element = doc.createElement(PolicyConstants.POLICY_ID_REFERENCE_ELEMENT);
                element.setTextContent(policyId);
                policyElement.appendChild(element);
            }
        }
        
        if(obligationElementDTOs != null && obligationElementDTOs.size() > 0){
            List<ObligationElementDTO> obligations = new ArrayList<ObligationElementDTO>();
            List<ObligationElementDTO> advices = new ArrayList<ObligationElementDTO>();
            for(ObligationElementDTO obligationElementDTO : obligationElementDTOs){
                if(obligationElementDTO.getType() == ObligationElementDTO.ADVICE){
                    advices.add(obligationElementDTO);
                } else {
                    obligations.add(obligationElementDTO);
                }
            }
            Element obligation = createObligationsElement(obligations, doc);
            Element advice = createAdvicesElement(advices, doc);
            if(obligation != null){
                policyElement.appendChild(obligation);
            }
            if(advice != null){
                policyElement.appendChild(advice);
            }
        }

        return policyElement;
    }

    public static Element createRequestElement(RequestElementDTO requestElementDTO, Document doc)
            throws PolicyBuilderException {

        Element requestElement = doc.createElement(PolicyConstants.Request.REQUEST_ELEMENT);
        requestElement.setAttribute("xmlns", PolicyConstants.Request.REQ_RES_CONTEXT_XACML3);
        requestElement.setAttribute(PolicyConstants.Request.RETURN_POLICY_LIST ,
                Boolean.toString(requestElementDTO.isReturnPolicyIdList()));
        requestElement.setAttribute(PolicyConstants.Request.COMBINED_DECISION ,
                Boolean.toString(requestElementDTO.isCombinedDecision()));
        
        List<AttributesElementDTO>  attributesElementDTOs = requestElementDTO.getAttributesElementDTOs();
        if(attributesElementDTOs != null && attributesElementDTOs.size() > 0){
            for(AttributesElementDTO dto : attributesElementDTOs){
                requestElement.appendChild(createAttributesElement(dto,doc));
            }
        }
        return requestElement;
    }

    /**
     * This method creates a match elementof the XACML policy
     * @param matchElementDTO match element data object
     * @param doc XML document
     * @return match Element
     * @throws PolicyBuilderException throws
     */
    public static Element createMatchElement(MatchElementDTO matchElementDTO,
                                             Document doc) throws PolicyBuilderException {

        Element matchElement = null;
        if(matchElementDTO.getMatchId() != null && matchElementDTO.getMatchId().trim().length() > 0) {

            matchElement = doc.createElement(PolicyConstants.MATCH_ELEMENT);

            matchElement.setAttribute(PolicyConstants.MATCH_ID,
                    matchElementDTO.getMatchId());

            if(matchElementDTO.getAttributeValueElementDTO() != null) {
                Element attributeValueElement = createAttributeValueElement(matchElementDTO.
                        getAttributeValueElementDTO(), doc);
                matchElement.appendChild(attributeValueElement);
            }

            if(matchElementDTO.getAttributeDesignatorDTO() != null ) {
                Element attributeDesignatorElement = createAttributeDesignatorElement(matchElementDTO.
                        getAttributeDesignatorDTO(), doc);
                matchElement.appendChild(attributeDesignatorElement);
            }

            if(matchElementDTO.getAttributeSelectorDTO() != null ) {
                Element attributeSelectorElement = createAttributeSelectorElement(matchElementDTO.
                        getAttributeSelectorDTO(), doc);
                matchElement.appendChild(attributeSelectorElement);
            }
        }
        return matchElement;
    }

    /**
     * This method creates the attribute value element
     * @param attributeValueElementDTO attribute value element data object
     * @param doc XML document
     * @return attribute value element
     */
    public static Element createAttributeValueElement(AttributeValueElementDTO
                                                              attributeValueElementDTO, Document doc) {

        Element attributeValueElement = doc.createElement(PolicyConstants.ATTRIBUTE_VALUE);

        if(attributeValueElementDTO.getAttributeValue() != null && attributeValueElementDTO.
                getAttributeValue().trim().length() > 0) {

            attributeValueElement.setTextContent(attributeValueElementDTO.getAttributeValue().trim());

            if(attributeValueElementDTO.getAttributeDataType()!= null && attributeValueElementDTO.
                    getAttributeDataType().trim().length() > 0){
                attributeValueElement.setAttribute(PolicyConstants.DATA_TYPE,
                        attributeValueElementDTO.getAttributeDataType());
            } else {
                attributeValueElement.setAttribute(PolicyConstants.DATA_TYPE,
                        PolicyConstants.STRING_DATA_TYPE);
            }

        }

        return attributeValueElement;

    }

    /**
     * This creates XML representation of Attributes Element using AttributesElementDTO object
     *
     * @param elementDTO  AttributesElementDTO
     * @param doc Document
     * @return DOM element
     */
    public static Element createAttributesElement(AttributesElementDTO elementDTO, Document doc){

        Element attributesElement = doc.createElement(PolicyConstants.ATTRIBUTES);

        attributesElement.setAttribute(PolicyConstants.CATEGORY, elementDTO.getCategory());

        List<AttributeElementDTO> attributeElementDTOs = elementDTO.getAttributeElementDTOs();
        if(attributeElementDTOs != null && attributeElementDTOs.size() > 0){
            for(AttributeElementDTO attributeElementDTO : attributeElementDTOs){
                Element attributeElement = doc.createElement(PolicyConstants.ATTRIBUTE);
                attributeElement.setAttribute(PolicyConstants.ATTRIBUTE_ID,
                        attributeElementDTO.getAttributeId());
                attributeElement.setAttribute(PolicyConstants.INCLUDE_RESULT,
                        Boolean.toString(attributeElementDTO.isIncludeInResult()));

                if(attributeElementDTO.getIssuer() != null &&
                        attributeElementDTO.getIssuer().trim().length() > 0){
                    attributeElement.setAttribute(PolicyConstants.ISSUER,
                            attributeElementDTO.getIssuer());
                }

                List<String> values = attributeElementDTO.getAttributeValues();
                for(String value : values){
                    Element attributeValueElement = doc.createElement(PolicyConstants.
                            ATTRIBUTE_VALUE);
                    attributeValueElement.setAttribute(PolicyConstants.DATA_TYPE,
                            attributeElementDTO.getDataType());
                    attributeValueElement.setTextContent(value.trim());
                    attributeElement.appendChild(attributeValueElement);
                }
                attributesElement.appendChild(attributeElement);
            }
        }
        return attributesElement;
    }

    /**
     * This creates XML representation of function Element using FunctionElementDTO object
     *
     * @param functionElementDTO  FunctionElementDTO
     * @param doc Document
     * @return DOM element
     */
    public static Element createFunctionElement(FunctionElementDTO functionElementDTO, Document doc) {

        Element functionElement = doc.createElement(PolicyConstants.FUNCTION);

        if(functionElementDTO.getFunctionId() != null && functionElementDTO.getFunctionId().trim().length() > 0) {
            functionElement.setAttribute(PolicyConstants.FUNCTION_ID,
                    functionElementDTO.getFunctionId());
        }

        return functionElement;
    }

    /**
     * This creates XML representation of attribute designator Element using AttributeDesignatorDTO object
     *
     * @param attributeDesignatorDTO  AttributeDesignatorDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createAttributeDesignatorElement(AttributeDesignatorDTO
                               attributeDesignatorDTO, Document doc) throws PolicyBuilderException {

        String attributeDesignatorElementName = PolicyConstants.ATTRIBUTE_DESIGNATOR;

        Element attributeDesignatorElement = doc.createElement(attributeDesignatorElementName);

        String attributeId = attributeDesignatorDTO.getAttributeId();
        String category = attributeDesignatorDTO.getCategory();

        if(attributeId != null && attributeId.trim().length() > 0 && category != null &&
                category.trim().length() > 0){

            attributeDesignatorElement.setAttribute(PolicyConstants.ATTRIBUTE_ID,
                    attributeDesignatorDTO.getAttributeId());

            attributeDesignatorElement.setAttribute(PolicyConstants.CATEGORY,
                    attributeDesignatorDTO.getCategory());

            if(attributeDesignatorDTO.getDataType() != null && attributeDesignatorDTO.
                    getDataType().trim().length() > 0) {
                attributeDesignatorElement.setAttribute(PolicyConstants.DATA_TYPE,
                        attributeDesignatorDTO.getDataType());
            } else {
                attributeDesignatorElement.setAttribute(PolicyConstants.DATA_TYPE,
                        PolicyConstants.STRING_DATA_TYPE);
            }

            if(attributeDesignatorDTO.getIssuer() != null && attributeDesignatorDTO.getIssuer().
                    trim().length() > 0) {
                attributeDesignatorElement.setAttribute(PolicyConstants.ISSUER,
                        attributeDesignatorDTO.getIssuer());
            }

            if(attributeDesignatorDTO.getMustBePresent() != null && attributeDesignatorDTO.
                    getMustBePresent().trim().length() > 0){
                attributeDesignatorElement.setAttribute(PolicyConstants.MUST_BE_PRESENT,
                        attributeDesignatorDTO.getMustBePresent());
            } else {
                attributeDesignatorElement.setAttribute(PolicyConstants.MUST_BE_PRESENT,
                        "true");
            }
        } else {
            throw new PolicyBuilderException("Category  name can not be null");  // TODO
        }

        return attributeDesignatorElement;
    }

    /**
     * This creates XML representation of attribute selector Element using AttributeSelectorDTO object
     *
     * @param attributeSelectorDTO  AttributeSelectorDTO
     * @param doc Document
     * @return DOM element
     */
    public static Element createAttributeSelectorElement(AttributeSelectorDTO attributeSelectorDTO,
                                                         Document doc)  {

        Element attributeSelectorElement = doc.createElement(PolicyConstants.
                ATTRIBUTE_SELECTOR);

        if(attributeSelectorDTO.getAttributeSelectorRequestContextPath() != null &&
                attributeSelectorDTO.getAttributeSelectorRequestContextPath().trim().length() > 0) {

            attributeSelectorElement.setAttribute(PolicyConstants.REQUEST_CONTEXT_PATH,
                    PolicyConstants.ATTRIBUTE_NAMESPACE + attributeSelectorDTO.
                            getAttributeSelectorRequestContextPath());

            if(attributeSelectorDTO.getAttributeSelectorDataType() != null &&
                    attributeSelectorDTO.getAttributeSelectorDataType().trim().length() > 0) {
                attributeSelectorElement.setAttribute(PolicyConstants.DATA_TYPE,
                        attributeSelectorDTO.getAttributeSelectorDataType());
            } else {
                attributeSelectorElement.setAttribute(PolicyConstants.DATA_TYPE,
                        PolicyConstants.STRING_DATA_TYPE);
            }

            if(attributeSelectorDTO.getAttributeSelectorMustBePresent() != null &&
                    attributeSelectorDTO.getAttributeSelectorMustBePresent().trim().length() > 0) {
                attributeSelectorElement.setAttribute(PolicyConstants.MUST_BE_PRESENT,
                        attributeSelectorDTO.getAttributeSelectorMustBePresent());
            }

        }

        return attributeSelectorElement;
    }

    /**
     * This creates XML representation of obligation Element  using List of ObligationElementDTO object
     *
     * @param obligationElementDTOs List of ObligationElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createObligationsElement(List<ObligationElementDTO> obligationElementDTOs,
                                                   Document doc) throws PolicyBuilderException {


        Element obligationExpressions = null;

        if(obligationElementDTOs != null && obligationElementDTOs.size() > 0){

            for(ObligationElementDTO dto : obligationElementDTOs){
                String id = dto.getId();
                String effect = dto.getEffect();

                if(id != null && id.trim().length() > 0 && effect != null){
                    if(obligationExpressions == null){
                        obligationExpressions = doc.
                                createElement(PolicyConstants.OBLIGATION_EXPRESSIONS);
                    }
                    Element obligationExpression = doc.
                            createElement(PolicyConstants.OBLIGATION_EXPRESSION);
                    obligationExpression.setAttribute(PolicyConstants.OBLIGATION_ID, id);
                    obligationExpression.setAttribute(PolicyConstants.OBLIGATION_EFFECT,
                            effect);
                    List<AttributeAssignmentElementDTO> elementDTOs = dto.getAssignmentElementDTOs();
                    if(elementDTOs != null){
                        for(AttributeAssignmentElementDTO elementDTO : elementDTOs){
                            Element element = createAttributeAssignmentElement(elementDTO, doc);
                            if(element != null){
                                obligationExpression.appendChild(element);
                            }
                        }
                    }
                    obligationExpressions.appendChild(obligationExpression);
                }
            }
        }

        return obligationExpressions;
    }

    /**
     * This creates XML representation of advice element using List of ObligationElementDTO object
     *
     * @param obligationElementDTOs List of ObligationElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createAdvicesElement(List<ObligationElementDTO> obligationElementDTOs,
                                                   Document doc) throws PolicyBuilderException {

        Element adviceExpressions = null;

        if(obligationElementDTOs != null && obligationElementDTOs.size() > 0){

            for(ObligationElementDTO dto : obligationElementDTOs){
                String id = dto.getId();
                String effect = dto.getEffect();

                if(id != null && id.trim().length() > 0 && effect != null){
                    if(adviceExpressions == null){
                        adviceExpressions = doc.
                                createElement(PolicyConstants.ADVICE_EXPRESSIONS);
                    }

                    Element adviceExpression = doc.
                            createElement(PolicyConstants.ADVICE_EXPRESSION);
                    adviceExpression.setAttribute(PolicyConstants.ADVICE_ID, id);
                    adviceExpression.setAttribute(PolicyConstants.ADVICE_EFFECT, effect);
                    List<AttributeAssignmentElementDTO> elementDTOs = dto.getAssignmentElementDTOs();
                    if(elementDTOs != null){
                        for(AttributeAssignmentElementDTO elementDTO : elementDTOs){
                            Element element = createAttributeAssignmentElement(elementDTO, doc);
                            if(element != null){
                                adviceExpression.appendChild(element);
                            }
                        }
                    }
                    adviceExpressions.appendChild(adviceExpression);
                }
            }
        }

        return adviceExpressions;
    }


    /**
     * This creates XML representation of assignment element using AttributeAssignmentElementDTO object
     *
     * @param assignmentElementDTO AttributeAssignmentElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createAttributeAssignmentElement(AttributeAssignmentElementDTO assignmentElementDTO,
                                                           Document doc) throws PolicyBuilderException {

        String attributeId = assignmentElementDTO.getAttributeId();

        if(attributeId != null && attributeId.trim().length() > 0){

            String category = assignmentElementDTO.getCategory();
            String issuer = assignmentElementDTO.getIssuer();
            ApplyElementDTO applyElementDTO = assignmentElementDTO.getApplyElementDTO();
            AttributeDesignatorDTO designatorDTO = assignmentElementDTO.getDesignatorDTO();
            AttributeValueElementDTO valueElementDTO = assignmentElementDTO.getValueElementDTO();

            Element attributeAssignment = doc.
                    createElement(PolicyConstants.ATTRIBUTE_ASSIGNMENT);
            attributeAssignment.setAttribute(PolicyConstants.ATTRIBUTE_ID,
                    attributeId);
            if(category != null && category.trim().length() > 0){
                attributeAssignment.setAttribute(PolicyConstants.CATEGORY, category);
            }

            if(issuer != null && issuer.trim().length() > 0){
                attributeAssignment.setAttribute(PolicyConstants.ISSUER, issuer);
            }

            if(applyElementDTO != null){
                attributeAssignment.appendChild(createApplyElement(applyElementDTO, doc));
            }

            if(designatorDTO != null){
                attributeAssignment.appendChild(createAttributeDesignatorElement(designatorDTO, doc));
            }

            if(valueElementDTO != null){
                attributeAssignment.appendChild(createAttributeValueElement(valueElementDTO, doc));
            }

            return attributeAssignment;
        }

        return null;
    }

    /**
     * This creates XML representation of target element using TargetElementDTO object
     *
     * @param targetElementDTO TargetElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createTargetElement(TargetElementDTO targetElementDTO,
                                                    Document doc) throws PolicyBuilderException {

        Element targetElement = doc.createElement(PolicyConstants.TARGET_ELEMENT);
        List<AnyOfElementDTO> anyOfElementDTOs = targetElementDTO.getAnyOfElementDTOs();

        for(AnyOfElementDTO anyOfElementDTO : anyOfElementDTOs){
            Element anyOfElement = doc.createElement(PolicyConstants.ANY_OF_ELEMENT);
            List<AllOfElementDTO> allOfElementDTOs = anyOfElementDTO.getAllOfElementDTOs();

            for(AllOfElementDTO allOfElementDTO : allOfElementDTOs){
                Element allOfElement = doc.createElement(PolicyConstants.ALL_OF_ELEMENT);
                List<MatchElementDTO> matchElementDTOs =  allOfElementDTO.getMatchElementDTOs();

                for(MatchElementDTO matchElementDTO : matchElementDTOs){
                    Element matchElement = createMatchElement(matchElementDTO, doc);

                    allOfElement.appendChild(matchElement);
                }

                anyOfElement.appendChild(allOfElement);
            }

            targetElement.appendChild(anyOfElement);
        }

        return targetElement;

    }

    /**
     * This creates XML representation of rule element using RuleElementDTO object
     *
     * @param ruleElementDTO RuleElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createRuleElement(RuleElementDTO ruleElementDTO, Document doc) throws PolicyBuilderException {

        TargetElementDTO targetElementDTO = ruleElementDTO.getTargetElementDTO();
        ConditionElementDT0 conditionElementDT0 = ruleElementDTO.getConditionElementDT0();
        List<ObligationElementDTO> obligationElementDTOs = ruleElementDTO.getObligationElementDTOs();

        Element ruleElement = doc.createElement(PolicyConstants.RULE_ELEMENT);

        if(ruleElementDTO.getRuleId() != null && ruleElementDTO.getRuleId().trim().length() > 0){
            ruleElement.setAttribute(PolicyConstants.RULE_ID, ruleElementDTO.getRuleId());
        }

        if(ruleElementDTO.getRuleEffect() != null && ruleElementDTO.getRuleEffect().trim().length() > 0){
            ruleElement.setAttribute(PolicyConstants.RULE_EFFECT,
                    ruleElementDTO.getRuleEffect());
        }

        if(ruleElementDTO.getRuleDescription() != null && ruleElementDTO.getRuleDescription().
                trim().length() > 0){
            Element descriptionElement = doc.createElement(PolicyConstants.
                    DESCRIPTION_ELEMENT);
            descriptionElement.setTextContent(ruleElementDTO.getRuleDescription());
            ruleElement.appendChild(descriptionElement);
        }

        if(targetElementDTO != null ){
            Element targetElement = createTargetElement(targetElementDTO, doc);
            ruleElement.appendChild(targetElement);
        }

        if(conditionElementDT0 != null){
            ruleElement.appendChild(createConditionElement(conditionElementDT0, doc));
        }

        if(obligationElementDTOs != null && obligationElementDTOs.size() > 0){
            List<ObligationElementDTO> obligations = new ArrayList<ObligationElementDTO>();
            List<ObligationElementDTO> advices = new ArrayList<ObligationElementDTO>();
            for(ObligationElementDTO obligationElementDTO : obligationElementDTOs){
                if(obligationElementDTO.getType() == ObligationElementDTO.ADVICE){
                    advices.add(obligationElementDTO);
                } else {
                    obligations.add(obligationElementDTO);
                }
            }
            Element obligation = createObligationsElement(obligations, doc);
            Element advice = createAdvicesElement(advices, doc);
            if(obligation != null){
                ruleElement.appendChild(obligation);
            }
            if(advice != null){
                ruleElement.appendChild(advice);
            }
        }

        return ruleElement;
    }

    /**
     * This creates XML representation of condition element using ConditionElementDT0 object
     *
     * @param conditionElementDT0 ConditionElementDT0
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createConditionElement(ConditionElementDT0 conditionElementDT0,
                                                 Document doc) throws PolicyBuilderException {

        Element conditionElement = doc.createElement(PolicyConstants.CONDITION_ELEMENT);

        if(conditionElementDT0.getApplyElement() != null){
            conditionElement.appendChild(createApplyElement(conditionElementDT0.getApplyElement(), doc));

        } else if(conditionElementDT0.getAttributeValueElementDTO() != null) {
            Element attributeValueElement = createAttributeValueElement(conditionElementDT0.
                    getAttributeValueElementDTO(), doc);
            conditionElement.appendChild(attributeValueElement);

        } else if(conditionElementDT0.getAttributeDesignator() != null) {
            AttributeDesignatorDTO attributeDesignatorDTO = conditionElementDT0.getAttributeDesignator();
            conditionElement.appendChild(createAttributeDesignatorElement(attributeDesignatorDTO, doc));

        } else if(conditionElementDT0.getFunctionFunctionId() != null) {
            Element functionElement = doc.createElement(PolicyConstants.FUNCTION_ELEMENT);
            functionElement.setAttribute(PolicyConstants.FUNCTION_ID,
                    conditionElementDT0.getFunctionFunctionId());
            conditionElement.appendChild(functionElement);
        } else if(conditionElementDT0.getVariableId() != null){
            Element variableReferenceElement = doc.createElement(PolicyConstants.
                    VARIABLE_REFERENCE);
            variableReferenceElement.setAttribute(PolicyConstants.VARIABLE_ID,
                    conditionElementDT0.getVariableId());
            conditionElement.appendChild(variableReferenceElement);
        }

        return conditionElement;

    }

    /**
     * This creates XML representation of apply element using ApplyElementDTO object
     *
     * @param applyElementDTO ApplyElementDTO
     * @param doc Document
     * @return DOM element
     * @throws PolicyBuilderException throws
     */
    public static Element createApplyElement(ApplyElementDTO applyElementDTO,
                                                    Document doc) throws PolicyBuilderException {

        Element applyElement = doc.createElement(PolicyConstants.APPLY_ELEMENT);

        if(applyElementDTO.getFunctionId() != null && applyElementDTO.getFunctionId().trim().length() > 0){
            applyElement.setAttribute(PolicyConstants.FUNCTION_ID,
                    applyElementDTO.getFunctionId());
        }

        if(applyElementDTO.getFunctionFunctionId() != null && applyElementDTO.
                getFunctionFunctionId().trim().length() > 0){
            FunctionElementDTO functionElementDTO = new FunctionElementDTO();
            functionElementDTO.setFunctionId(applyElementDTO.getFunctionFunctionId());
            Element functionElement = createFunctionElement(functionElementDTO, doc);
            applyElement.appendChild(functionElement);
        }

        List<ApplyElementDTO> applyElementDTOs = applyElementDTO.getApplyElements();

        if(applyElementDTOs != null && applyElementDTOs.size() > 0) {

            for(ApplyElementDTO elementDTO : applyElementDTOs) {
                Element subApplyElement = createApplyElement(elementDTO, doc);
                applyElement.appendChild(subApplyElement);
            }
        }

        List<AttributeValueElementDTO> attributeValueElementDTOs = applyElementDTO.
                getAttributeValueElementDTOs();
        if(attributeValueElementDTOs != null && attributeValueElementDTOs.size() > 0) {

            for(AttributeValueElementDTO attributeValueElementDTO : attributeValueElementDTOs) {
                Element attributeValueElement = createAttributeValueElement(attributeValueElementDTO,
                        doc);

                applyElement.appendChild(attributeValueElement);
            }
        }

        List<AttributeDesignatorDTO> attributeDesignatorDTOs = applyElementDTO.getAttributeDesignators();
        if(attributeDesignatorDTOs != null && attributeDesignatorDTOs.size() > 0) {

            for(AttributeDesignatorDTO attributeDesignatorDTO : attributeDesignatorDTOs) {
                Element attributeDesignatorElement =
                        createAttributeDesignatorElement(attributeDesignatorDTO, doc);
                applyElement.appendChild(attributeDesignatorElement);
            }
        }

        List<AttributeSelectorDTO> attributeSelectorDTOs = applyElementDTO.getAttributeSelectors();
        if(attributeSelectorDTOs != null && attributeSelectorDTOs.size() > 0) {

            for(AttributeSelectorDTO attributeSelectorDTO : attributeSelectorDTOs) {
                Element attributeSelectorElement = createAttributeSelectorElement(attributeSelectorDTO,
                        doc);
                applyElement.appendChild(attributeSelectorElement);
            }
        }
        return applyElement;
    }
}
