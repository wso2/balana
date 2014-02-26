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

import org.wso2.balana.ctx.EvaluationCtx;

import java.io.OutputStream;

/**
 * Represents the TargetType XML type in XACML. This defined as abstract, because there can be
 * more than one implementation of TargetType. As an example, TargetType is considerably defer in
 * XACML 2.0 and XACML 3.0 Therefore two implementations are used.
 *
 * The target is used to quickly identify whether the parent element (a policy set, policy, or rule)
 * is applicable to a given request.
 */
public abstract class AbstractTarget {

    /**
     * Determines whether this <code>AbstractTarget</code> matches the input request (whether it is
     * applicable).
     *
     * @param context the representation of the request
     *
     * @return the result of trying to match the target and the request
     */
    public abstract MatchResult match(EvaluationCtx context);

    /**
     * Encodes this <code>AbstractTarget</code> into its XML form
     *
     * @return <code>String</code>
     */
    public abstract String encode();

    /**
     * Encodes this <code>AbstractTarget</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public abstract void encode(StringBuilder builder);

}
