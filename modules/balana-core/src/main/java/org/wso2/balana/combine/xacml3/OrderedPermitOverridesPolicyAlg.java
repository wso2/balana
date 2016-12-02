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
package org.wso2.balana.combine.xacml3;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This is the new (XACML 3.0) standard Ordered Permit Overrides policy combining algorithm.
 * It allows a single evaluation of Permit to take precedence over any number of deny,
 * not applicable or indeterminate results. Note that this uses the regular Permit Overrides
 * implementation since it is also ordered.
 */
public class OrderedPermitOverridesPolicyAlg extends PermitOverridesPolicyAlg {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:"
            + "ordered-permit-overrides";

    // a URI form of the identifier
    private static URI identifierURI;
    // exception if the URI was invalid, which should never be a problem
    private static RuntimeException earlyException;

    static {
        try {
            identifierURI = new URI(algId);
        } catch (URISyntaxException se) {
            earlyException = new IllegalArgumentException();
            earlyException.initCause(se);
        }
    }

    /**
     * Standard constructor.
     */
    public OrderedPermitOverridesPolicyAlg() {
        super(identifierURI);

        if (earlyException != null) {
            throw earlyException;
        }
    }

}