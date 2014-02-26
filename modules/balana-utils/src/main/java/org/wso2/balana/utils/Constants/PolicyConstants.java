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

package org.wso2.balana.utils.Constants;

/**
 *
 */
public class PolicyConstants {

    public static final String ATTRIBUTE_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:example:attribute:";

    public static final String POLICY_ELEMENT = "Policy";

    public static final String POLICY_SET_ELEMENT = "PolicySet";

    public static final String POLICY_ID_REFERENCE_ELEMENT = "PolicyIdReference";

    public static final String POLICY_SET_ID_REFERENCE_ELEMENT = "PolicySetIdReference";

    public static final String APPLY_ELEMENT = "Apply";

    public static final String MATCH_ELEMENT = "Match";

    public static final String SUBJECT_ELEMENT = "Subject";

    public static final String ACTION_ELEMENT = "Action";

    public static final String RESOURCE_ELEMENT = "Resource";

    public static final String ENVIRONMENT_ELEMENT = "Environment";

    public static final String POLICY_ID = "PolicyId";

    public static final String POLICY_SET_ID = "PolicySetId";

    public static final String RULE_ALGORITHM = "RuleCombiningAlgId";

    public static final String POLICY_ALGORITHM = "PolicyCombiningAlgId";

    public static final String POLICY_VERSION = "Version";

    public static final String DESCRIPTION_ELEMENT = "Description";

    public static final String TARGET_ELEMENT = "Target";

    public static final String ANY_OF_ELEMENT = "AnyOf";

    public static final String ALL_OF_ELEMENT = "AllOf";

    public static final String RULE_ELEMENT = "Rule";

    public static final String CONDITION_ELEMENT = "Condition";

    public static final String FUNCTION_ELEMENT = "Function";

    public static final String ATTRIBUTE_SELECTOR = "AttributeSelector";

    public static final String ATTRIBUTE_VALUE = "AttributeValue";

    public static final String FUNCTION = "Function";

    public static final String VARIABLE_REFERENCE = "VariableReference";

    public static final String ATTRIBUTE_DESIGNATOR = "AttributeDesignator";

    public static final String ATTRIBUTE_ID = "AttributeId";

    public static final String CATEGORY = "Category";

    public static final String ATTRIBUTE = "Attribute";

    public static final String ATTRIBUTES = "Attributes";

    public static final String INCLUDE_RESULT = "IncludeInResult";

    public static final String DATA_TYPE = "DataType";

    public static final String ISSUER = "Issuer";

    public static final String MUST_BE_PRESENT = "MustBePresent";

    public static final String REQUEST_CONTEXT_PATH = "RequestContextPath";

    public static final String MATCH_ID = "MatchId";

    public static final String RULE_ID = "RuleId";

    public static final String RULE_EFFECT = "Effect";

    public static final String RULE_DESCRIPTION = "Description";

    public static final String FUNCTION_ID = "FunctionId";

    public static final String VARIABLE_ID = "VariableId";

    public static final String OBLIGATION_EXPRESSIONS = "ObligationExpressions";

    public static final String OBLIGATION_EXPRESSION = "ObligationExpression";

    public static final String OBLIGATION_ID = "ObligationId";

    public static final String OBLIGATION_EFFECT = "FulfillOn";

    public static final String ADVICE_EXPRESSIONS = "AdviceExpressions";

    public static final String ADVICE_EXPRESSION = "AdviceExpression";

    public static final String ADVICE_ID = "AdviceId";

    public static final String ADVICE_EFFECT = "AppliesTo";

    public static final String ATTRIBUTE_ASSIGNMENT = "AttributeAssignmentExpression";

    public static final String STRING_DATA_TYPE = "http://www.w3.org/2001/XMLSchema#string";

    public static final String SUBJECT_ID_DEFAULT = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";

    public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";

    public static final String ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";

    public static final String ENVIRONMENT_ID = "urn:oasis:names:tc:xacml:1.0:environment:environment-id";

    public static final String ATTRIBUTE_SEPARATOR = ",";

    public static final String RESOURCE_CATEGORY_URI = "urn:oasis:names:tc:xacml:3.0:" +
            "attribute-category:resource";

    public static final String SUBJECT_CATEGORY_URI = "urn:oasis:names:tc:xacml:1.0:" +
            "subject-category:access-subject";

    public static final String ACTION_CATEGORY_URI = "urn:oasis:names:tc:xacml:3.0:" +
            "attribute-category:action";

    public static final String ENVIRONMENT_CATEGORY_URI = "urn:oasis:names:tc:xacml:3.0:" +
            "attribute-category:environment";

    public static final class Request {

        public static final String RETURN_POLICY_LIST = "ReturnPolicyIdList";

        public static final String COMBINED_DECISION = "CombinedDecision";

        public static final String REQUEST_ELEMENT = "Request";

        public static final String REQ_RES_CONTEXT_XACML3 = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";
    }

    public static final class Functions {

        public static final String FUNCTION_GREATER_EQUAL_AND_LESS_EQUAL = "greater-than-or-equal-and-less-than-or-equal";

        public static final String FUNCTION_GREATER_AND_LESS_EQUAL= "greater-than-and-less-than-or-equal";

        public static final String FUNCTION_GREATER_EQUAL_AND_LESS = "greater-than-or-equal-and-less-than";

        public static final String FUNCTION_GREATER_AND_LESS = "greater-than-and-less-than";

        public static final String FUNCTION_GREATER = "greater-than";

        public static final String FUNCTION_GREATER_EQUAL = "greater-than-or-equal";

        public static final String FUNCTION_LESS = "less-than";

        public static final String FUNCTION_LESS_EQUAL = "less-than-or-equal";

        public static final String FUNCTION_AT_LEAST_ONE = "at-least-one-member-of";

        public static final String FUNCTION_AT_LEAST_ONE_MATCH_REGEXP = "at-least-one-matching-regexp-member-of";

        public static final String FUNCTION_IS_IN = "is-in";

        public static final String FUNCTION_IS_IN_MATCH_REGEXP = "is-in-matching-regexp";

        public static final String FUNCTION_SET_EQUALS = "set-equals";

        public static final String FUNCTION_SET_EQUALS_MATCH_REGEXP = "set-equals-matching-regexp";

        public static final String FUNCTION_EQUAL = "equal";

        public static final String FUNCTION_EQUAL_IGNORE_CASE = "equal-ignore-case";

        public static final String FUNCTION_EQUAL_MATCH_REGEXP = "regexp-match";

        public static final String[] targetFunctions =
                                        new String[] {FUNCTION_EQUAL,
                                                FUNCTION_EQUAL_IGNORE_CASE,
                                                FUNCTION_EQUAL_MATCH_REGEXP};

        public static final String[] simpleRuleFunctions = new String[] {
                FUNCTION_IS_IN, FUNCTION_IS_IN_MATCH_REGEXP};

        public static final String[] simpleBagRuleFunctions = new String[] {
                FUNCTION_AT_LEAST_ONE, FUNCTION_AT_LEAST_ONE_MATCH_REGEXP,
                FUNCTION_SET_EQUALS, FUNCTION_SET_EQUALS_MATCH_REGEXP};

        public static final String[] advanceRuleFunctions = new String[] {
                FUNCTION_GREATER_EQUAL_AND_LESS_EQUAL,
                FUNCTION_GREATER_AND_LESS_EQUAL, FUNCTION_GREATER_EQUAL_AND_LESS, FUNCTION_LESS,
                FUNCTION_GREATER_AND_LESS, FUNCTION_GREATER, FUNCTION_GREATER_EQUAL, FUNCTION_LESS_EQUAL};

        public static final String[] functions = new String[] {
                FUNCTION_EQUAL, FUNCTION_EQUAL_MATCH_REGEXP,
                FUNCTION_GREATER_EQUAL_AND_LESS_EQUAL,
                FUNCTION_IS_IN, FUNCTION_IS_IN_MATCH_REGEXP,
                FUNCTION_AT_LEAST_ONE, FUNCTION_AT_LEAST_ONE_MATCH_REGEXP,
                FUNCTION_SET_EQUALS, FUNCTION_SET_EQUALS_MATCH_REGEXP,
                FUNCTION_GREATER_AND_LESS_EQUAL, FUNCTION_GREATER_EQUAL_AND_LESS, FUNCTION_LESS,
                FUNCTION_GREATER_AND_LESS, FUNCTION_GREATER, FUNCTION_GREATER_EQUAL, FUNCTION_LESS_EQUAL};
    }
    

    public static final class PreFunctions {

        public static final String PRE_FUNCTION_NOT = "not";
        public static final String PRE_FUNCTION = "is";

        public static final String[] preFunctions = new String[] {PRE_FUNCTION, PRE_FUNCTION_NOT};
    }

    public static final class DataType {

        public static final String DAY_TIME_DURATION  = "http://www.w3.org/2001/XMLSchema#dayTimeDuration";

        public static final String YEAR_MONTH_DURATION  = "http://www.w3.org/2001/XMLSchema#yearMonthDuration";

        public static final String STRING = "http://www.w3.org/2001/XMLSchema#string";

        public static final String TIME = "http://www.w3.org/2001/XMLSchema#time";

        public static final String IP_ADDRESS = "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress";

        public static final String DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";

        public static final String DATE = "http://www.w3.org/2001/XMLSchema#date";

        public static final String DOUBLE = "http://www.w3.org/2001/XMLSchema#double";

        public static final String INT = "http://www.w3.org/2001/XMLSchema#integer";

        public static final String BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";

        public static final String ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

        public static final String HEX = "http://www.w3.org/2001/XMLSchema#hexBinary";

        public static final String BASE64 = "http://www.w3.org/2001/XMLSchema#base64Binary";

        public static final String DNS = "urn:oasis:names:tc:xacml:2.0:data-type:dnsName";

        public static final String RFC = "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";

        public static final String XPATH = "urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression";

        public static final String X500 = "urn:oasis:names:tc:xacml:1.0:data-type:x500Name";


        public static final String[] dataTypes = new String[]{ BOOLEAN, ANY_URI, HEX, BASE64, DNS,
                DAY_TIME_DURATION, YEAR_MONTH_DURATION, STRING, TIME, IP_ADDRESS, DATE_TIME,
                DATE, DOUBLE, INT, RFC, XPATH, X500};

    }


    public static final class RuleCombiningAlog {

        public static final String DENY_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides";

        public static final String PERMIT_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides";

        public static final String FIRST_APPLICABLE_ID = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";

        public static final String ORDER_PERMIT_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides";

        public static final String ORDER_DENY_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides";

        public static final String DENY_UNLESS_PERMIT_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit";

        public static final String PERMIT_UNLESS_DENY_ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny";

        public static final String[] algorithms = new String[]{
                DENY_OVERRIDE_ID, PERMIT_OVERRIDE_ID, FIRST_APPLICABLE_ID, ORDER_PERMIT_OVERRIDE_ID,
                ORDER_DENY_OVERRIDE_ID, DENY_UNLESS_PERMIT_ID, PERMIT_UNLESS_DENY_ID};
    }

    public static final class PolicyCombiningAlog {

        public static final String DENY_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides";

        public static final String PERMIT_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides";

        public static final String FIRST_APPLICABLE_ID = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";

        public static final String ORDER_PERMIT_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-overrides";

        public static final String ORDER_DENY_OVERRIDE_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-deny-overrides";

        public static final String DENY_UNLESS_PERMIT_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit";

        public static final String PERMIT_UNLESS_DENY_ID = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny";

        public static final String ONLY_ONE_APPLICABLE_ID = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable";

        public static final String[] algorithms = new String[]{
                DENY_OVERRIDE_ID, PERMIT_OVERRIDE_ID, FIRST_APPLICABLE_ID, ORDER_PERMIT_OVERRIDE_ID,
                ORDER_DENY_OVERRIDE_ID, DENY_UNLESS_PERMIT_ID, PERMIT_UNLESS_DENY_ID, ONLY_ONE_APPLICABLE_ID};
    }


    public static final class XACMLData {

        public static final String XACML3_POLICY_NAMESPACE = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";

        public static final String FUNCTION_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-equal";

        public static final String FUNCTION_ONE_AND_ONLY = "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";

        public static final String FUNCTION_IS_IN = "urn:oasis:names:tc:xacml:1.0:function:string-is-in";

        public static final String FUNCTION_REGEXP = "urn:oasis:names:tc:xacml:1.0:function:string-regexp-match";

        public static final String FUNCTION_AT_LEAST = "urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of";

        public static final String FUNCTION_UNION = "urn:oasis:names:tc:xacml:1.0:function:string-union";

        public static final String FUNCTION_SUBSET = "urn:oasis:names:tc:xacml:1.0:function:string-subset";

        public static final String FUNCTION_SET_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-set-equals";

        public static final String FUNCTION_ANY_OF = "urn:oasis:names:tc:xacml:1.0:function:any-of";

        public static final String FUNCTION_AND = "urn:oasis:names:tc:xacml:1.0:function:and";

        public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";

        public static final String FUNCTION_BAG = "urn:oasis:names:tc:xacml:1.0:function:string-bag";
    }

    public static final class RuleEffect {

        public static final String PERMIT = "Permit";

        public static final String DENY = "Deny";

        public static final String[] effect = new String[]{PERMIT, DENY};
    }

}
