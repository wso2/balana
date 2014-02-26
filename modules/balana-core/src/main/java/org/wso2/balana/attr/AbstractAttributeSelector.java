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

package org.wso2.balana.attr;

import org.wso2.balana.cond.Evaluatable;

import java.net.URI;

/**
 * Supports the standard selector functionality in XACML 3.0 version, which uses XPath expressions to resolve
 * values from the Request or elsewhere. This absrtact implementation of
 */
public abstract class AbstractAttributeSelector implements Evaluatable {

    /**
     * the data type returned by this selector
     */
    protected URI type;

    /**
     * must resolution find something
     */
    protected boolean mustBePresent;

    /**
     * the xpath version we've been told to use
     */
    protected String xpathVersion;

    /**
     * Returns the data type of the attribute values that this selector will resolve
     *
     * @return the data type of the values found by this selector
     */
    public URI getType() {
        return type;
    }

    /**
     * Returns whether or not a value is required to be resolved by this selector.
     *
     * @return true if a value is required, false otherwise
     */
    public boolean isMustBePresent() {
        return mustBePresent;
    }

    /**
     * Returns the XPath version this selector is supposed to use. This is typically provided by the
     * defaults section of the policy containing this selector.
     *
     * @return the XPath version
     */
    public String getXPathVersion() {
        return xpathVersion;
    }
}
