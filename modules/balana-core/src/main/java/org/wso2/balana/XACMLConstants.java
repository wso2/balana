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


package org.wso2.balana;

/**
 * XACML related constants are defined here..
 */
public class XACMLConstants {

    /**
     * The standard URI for listing a resource's id
     */
    public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";

    /**
     * The standard URI for listing a resource's scope  in XACML 1.0 
     */
    public static final String RESOURCE_SCOPE_1_0 = "urn:oasis:names:tc:xacml:1.0:resource:scope";

    /**
     * The standard URI for listing a resource's scope in multiple resource profile
     */
    public static final String RESOURCE_SCOPE_2_0 = "urn:oasis:names:tc:xacml:2.0:resource:scope";

    /**
     * Resource scope of Immediate (only the given resource)
     */
    public static final int SCOPE_IMMEDIATE = 0;

    /**
     * Resource scope of Children (the given resource and its direct children)
     */
    public static final int SCOPE_CHILDREN = 1;

    /**
     * Resource scope of Descendants (the given resource and all descendants at any depth or
     * distance)
     */
    public static final int SCOPE_DESCENDANTS = 2;

    public static final String MULTIPLE_CONTENT_SELECTOR = "urn:oasis:names:tc:xacml:3.0:profile:" +
                                                                        "multiple:content-selector";

    public static final String CONTENT_SELECTOR = "urn:oasis:names:tc:xacml:3.0:content-selector";

    public final static String ATTRIBUTES_ELEMENT =  "Attributes";

    public final static String MULTI_REQUESTS =  "MultiRequests";

    public final static String REQUEST_DEFAULTS =  "RequestDefaults";

    public final static String ATTRIBUTE_ELEMENT =  "Attribute";

    public final static String ATTRIBUTES_CATEGORY =  "Category";

    public final static String ATTRIBUTES_ID =  "id";

    public final static String RETURN_POLICY_LIST =  "ReturnPolicyIdList";

    public final static String COMBINE_DECISION =  "CombinedDecision";
    
    public final static String ATTRIBUTES_CONTENT =  "Content";

    public final static String RESOURCE_CONTENT =  "ResourceContent";
    
    public static final String RESOURCE_CATEGORY = "urn:oasis:names:tc:xacml:3.0:attribute-category:resource";

    public static final String SUBJECT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";

    public static final String ACTION_CATEGORY = "urn:oasis:names:tc:xacml:3.0:attribute-category:action";

    public static final String ENT_CATEGORY = "urn:oasis:names:tc:xacml:3.0:attribute-category:environment";

    public static final String REQUEST_CONTEXT_1_0_IDENTIFIER = "urn:oasis:names:tc:xacml:1.0:context";

    public static final String REQUEST_CONTEXT_2_0_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:context:schema:os";

    public static final String REQUEST_CONTEXT_3_0_IDENTIFIER = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";

    public static final String ANY  = "Any";

    /**
     * XACML 1.0 identifier
     */
    public static final String XACML_1_0_IDENTIFIER = "urn:oasis:names:tc:xacml:1.0:policy";

    /**
     * XACML 2.0 identifier
     */
    public static final String XACML_2_0_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:policy:schema:os";

    /**
     * XACML 3.0 identifier
     */
    public static final String XACML_3_0_IDENTIFIER = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";

    /**
     * Version identifier for XACML 1.0
     */
    public static final int XACML_VERSION_1_0 = 0;

    /**
     * Version identifier for XACML 1.1 (which isn't a formal release so has no namespace string,
     * but still exists as a separate specification)
     */
    public static final int XACML_VERSION_1_1 = 1;

    /**
     * Version identifier for XACML 1.2
     */
    public static final int XACML_VERSION_2_0 = 2;

    /**
     * Version identifier for XACML 3.0
     */
    public static final int XACML_VERSION_3_0 = 3;
    
}
