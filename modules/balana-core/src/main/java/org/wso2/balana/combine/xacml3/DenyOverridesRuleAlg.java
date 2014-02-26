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

import org.wso2.balana.ObligationResult;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.xacml3.Advice;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This is the new (XACML 3.0) standard Deny Overrides rule combining algorithm.
 * It allows a single evaluation of Deny to take precedence over any number of permit, not applicable 
 * or indeterminate results. Note that since this implementation does an ordered evaluation,
 * this class also supports the Ordered  Deny Overrides algorithm.
 */
public class DenyOverridesRuleAlg extends RuleCombiningAlgorithm {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:"
            + "deny-overrides";

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
    public DenyOverridesRuleAlg() {
        super(identifierURI);

        if (earlyException != null){
            throw earlyException;
        }
    }

    /**
     * Protected constructor used by the ordered version of this algorithm.
     *
     * @param identifier the algorithm's identifier
     */
    protected DenyOverridesRuleAlg(URI identifier) {
        super(identifier);
    }

    /**
     * Applies the combining rule to the set of rules based on the evaluation context.
     *
     * @param context the context from the request
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *            <code>CombinerParameter<code>s
     * @param ruleElements the rules to combine
     *
     * @return the result of running the combining algorithm
     */
    public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements) {

        boolean atLeastOneErrorD = false;
        boolean atLeastOneErrorP = false;
        boolean atLeastOnePermit = false;
        AbstractResult firstIndeterminateResultD = null;
        AbstractResult firstIndeterminateResultP = null;
        List<ObligationResult> permitObligations = new ArrayList<ObligationResult>();
        List<Advice> permitAdvices = new ArrayList<Advice>();
        Iterator it = ruleElements.iterator();

        while (it.hasNext()) {
            Rule rule = ((RuleCombinerElement) (it.next())).getRule();
            AbstractResult result = rule.evaluate(context);
            int value = result.getDecision();

            // if there was a value of DENY, then regardless of what else
            // we've seen, we always return DENY
            if (value == AbstractResult.DECISION_DENY){  
                return result;
            }

            if(value == AbstractResult.DECISION_NOT_APPLICABLE){
                continue;
            }

            // keep track of whether we had at least one rule that
            // actually pertained to the request
            if (value == AbstractResult.DECISION_PERMIT){

                atLeastOnePermit = true;
                permitAdvices.addAll(result.getAdvices());
                permitObligations.addAll(result.getObligations());
                
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
                }
            }
        }

        if (atLeastOneErrorD && (atLeastOneErrorP || atLeastOnePermit)){

            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE_DENY_OR_PERMIT,
                                                   firstIndeterminateResultD.getStatus(), context);
        }

        if(atLeastOneErrorD){
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE_DENY,
                                                   firstIndeterminateResultD.getStatus(), context);
        }

        if (atLeastOnePermit) {
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_PERMIT,
                                                        permitObligations, permitAdvices, context);
        }

        if (atLeastOneErrorP){
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE_PERMIT,
                                                   firstIndeterminateResultP.getStatus(), context);
        }

        // if we hit this point, then none of the rules actually applied
        // to us, so we return NOT_APPLICABLE
        return ResultFactory.getFactory().getResult(AbstractResult.DECISION_NOT_APPLICABLE, context);
    }

}