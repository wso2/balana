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

import java.net.URI;

/**
 * Represents ObligationType in the XACML 2.0 and  ObligationExpressionType in the XACML 2.0
 * policy schema. AbstractObligation class has been written to provide a unique interface for
 * both XACML 2.0 and XACML 3.0
 * 
 */
public abstract class AbstractObligation {

    /**
     * Identifier that uniquely identify the Obligation or ObligationExpression element
     */
    protected URI obligationId;

    /**
     * effect that will cause this obligation to be included in a response 
     */
    protected int fulfillOn;

    /**
     * Evaluates obligation and creates the results
     *
     * @param ctx  <code>EvaluationCtx</code>
     * @return  <code>ObligationResult</code>
     */
    public abstract ObligationResult evaluate(EvaluationCtx ctx);

    /**
     * Returns effect that will cause this obligation to be included in a response
     *
     * @return the fulfillOn effect
     */
    public int getFulfillOn(){
        return fulfillOn;
    }

    /**
     * Returns the id of this obligation
     *
     * @return the id
     */
    public URI getId() {
        return obligationId;
    }


    /**
     * Encodes this <code>ObligationResult</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public abstract void encode(StringBuilder builder);
}
