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

import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.xacml3.Advice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This is the new (XACML 3.0) is the standard Permit Overrides policy combining algorithm. It allows a single evaluation
 * of Permit to take precedence over any number of deny, not applicable or indeterminate results.
 * Note that since this implementation does an ordered evaluation, this class also supports the
 * Ordered Permit Overrides algorithm.
 */
public class PermitOverridesPolicyAlg extends PolicyCombiningAlgorithm{
    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:"
            + "permit-overrides";

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
    public PermitOverridesPolicyAlg() {
        super(identifierURI);

        if (earlyException != null)
            throw earlyException;
    }

    /**
     * Protected constructor used by the ordered version of this algorithm.
     *
     * @param identifier the algorithm's identifier
     */
    protected PermitOverridesPolicyAlg(URI identifier) {
        super(identifier);
    }

    /**
     * Applies the combining rule to the set of policies based on the evaluation context.
     *
     * @param context the context from the request
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *            <code>CombinerParameter<code>s
     * @param policyElements the policies to combine
     *
     * @return the result of running the combining algorithm
     */
    public AbstractResult combine(EvaluationCtx context, List parameters, List policyElements) {

        boolean atLeastOneErrorD = false;
        boolean atLeastOneErrorP = false;
        boolean atLeastOneErrorDP = false;
        boolean atLeastOneDeny = false;
        AbstractResult firstIndeterminateResultD = null;
        AbstractResult firstIndeterminateResultP = null;
        AbstractResult firstIndeterminateResultDP = null;        
        List<ObligationResult> denyObligations = new ArrayList<ObligationResult>();
        List<Advice> denyAdvices = new ArrayList<Advice>();

        Iterator it = policyElements.iterator();

        while (it.hasNext()) {
            AbstractPolicy policy = ((PolicyCombinerElement) (it.next())).getPolicy();

            // make sure that the policy matches the context
            MatchResult match = policy.match(context);

            if (match.getResult() == MatchResult.INDETERMINATE) {
//                atLeastOneError = true;
//                                                                      // TODO
//                // keep track of the first error, regardless of cause
//                if (firstIndeterminateStatus == null){
//                    firstIndeterminateStatus = match.getStatus();
//                }
            } else if (match.getResult() == MatchResult.MATCH) {
                // now we evaluate the policy
                AbstractResult result = policy.evaluate(context);
                int value = result.getDecision();

                if (value == AbstractResult.DECISION_PERMIT){
                    return result;
                }

                if(value == AbstractResult.DECISION_NOT_APPLICABLE){
                    continue;
                }

                // keep track of whether we had at least one rule that
                // actually pertained to the request
                if (value == AbstractResult.DECISION_DENY){

                    atLeastOneDeny = true;
                    denyAdvices.addAll(result.getAdvices());
                    denyObligations.addAll(result.getObligations());

                } else {

                    // if it was INDETERMINATE, check extended results
                    if (value == AbstractResult.DECISION_INDETERMINATE_DENY){
                        atLeastOneErrorD = true;
                        // there are no rules about what to do if multiple cases
                        // cause errors, so we'll just return the first one
                        if(firstIndeterminateResultD == null){
                            firstIndeterminateResultD = result;
                        }
                    } else if (value== AbstractResult.DECISION_INDETERMINATE_PERMIT){
                        atLeastOneErrorP = true;
                        // there are no rules about what to do if multiple cases
                        // cause errors, so we'll just return the first one
                        if(firstIndeterminateResultP == null){
                            firstIndeterminateResultP = result;
                        }
                    } else if(value == AbstractResult.DECISION_INDETERMINATE_DENY_OR_PERMIT){
                        atLeastOneErrorDP = true;
                        // there are no rules about what to do if multiple cases
                        // cause errors, so we'll just return the first one
                        if(firstIndeterminateResultDP == null){
                            firstIndeterminateResultDP = result;
                        }
                    }
                }
            }
        }

        if(atLeastOneErrorDP){
            return firstIndeterminateResultDP;
        }

        if (atLeastOneErrorP && (atLeastOneErrorD || atLeastOneDeny)){

            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE_DENY_OR_PERMIT,
                                                   firstIndeterminateResultP.getStatus(), context);
        }

        if(atLeastOneErrorP){
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE_PERMIT,
                                                   firstIndeterminateResultP.getStatus(), context);
        }

        if (atLeastOneDeny) {
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_DENY,
                                                        denyObligations, denyAdvices, context);
        }
        // if we hit this point, then none of the rules actually applied
        // to us, so we return NOT_APPLICABLE
        return ResultFactory.getFactory().getResult(AbstractResult.DECISION_NOT_APPLICABLE, context);
    }

}
