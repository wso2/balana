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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the standard Deny unless permit rule combining algorithm. This algorithm is intended for
 * those cases where a deny decision should have priority over a permit decision, and an "Indeterminate" or  
 * "NotApplicable" must never be the result. It is particularly useful at the top level
 * in a policy structure to ensure that a PDP will always return a definite "Permit" or "Deny" result.
 */
public class PermitUnlessDenyRuleAlg extends RuleCombiningAlgorithm{

    /**
     * The standard URI used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:" +
            "permit-unless-deny";

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
    public PermitUnlessDenyRuleAlg() {
        super(identifierURI);

        if (earlyException != null){
            throw earlyException;
        }
    }

    /**
     * Constructor that takes the algorithm's identifier.
     *
     * @param identifier the algorithm's identifier
     */
    public PermitUnlessDenyRuleAlg(URI identifier) {
        super(identifier);
    }


    @Override
    public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements) {

        List<ObligationResult> permitObligations = new ArrayList<ObligationResult>();
        List<Advice> permitAdvices= new ArrayList<Advice>();

        for (Object ruleElement : ruleElements) {
            Rule rule = ((RuleCombinerElement) (ruleElement)).getRule();
            AbstractResult result = rule.evaluate(context);
            int value = result.getDecision();

            // if there was a value of DENY, then regardless of what else
            // we've seen, we always return DENY
            if (value == AbstractResult.DECISION_DENY) {
                return result;
            } else if(value == AbstractResult.DECISION_PERMIT){
                permitObligations.addAll(result.getObligations());
                permitAdvices.addAll(result.getAdvices());
            }
        }

        // if there is not any value of DENY. The return PERMIT
        return ResultFactory.getFactory().getResult(AbstractResult.DECISION_PERMIT,
                                                    permitObligations, permitAdvices, context);
    }
}
