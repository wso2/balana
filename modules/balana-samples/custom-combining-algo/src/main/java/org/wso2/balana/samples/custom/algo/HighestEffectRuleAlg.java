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

package org.wso2.balana.samples.custom.algo;

import org.wso2.balana.Rule;
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.xacml2.Result;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * This is the custom sample rule combining algorithm. It looks through the set of
 * rules and evaluates all rules. Then highest number of resulted effect (permit or deny)
 * would be the result of  the combining algorithm.  If there is no permit or deny results
 * or same number of permit or deny results, final result would be Deny
 */
public class HighestEffectRuleAlg  extends RuleCombiningAlgorithm {


    /**
     * identifier for algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:"
            + "highest-effect";

    private static URI identifierURI;

    static {
        try {
            identifierURI = new URI(algId);
        } catch (URISyntaxException se) {
            // just ignore
        }
    }
    public HighestEffectRuleAlg() {
        super(identifierURI);
    }

    @Override
    public AbstractResult combine(EvaluationCtx context, List parameters, List ruleElements) {

        int noOfDenyRules = 0;
        int noOfPermitRules = 0;

        for (Object ruleElement : ruleElements) {
            
            Rule rule = ((RuleCombinerElement) (ruleElement)).getRule();
            AbstractResult result = rule.evaluate(context);

            int value = result.getDecision();
            if (value == Result.DECISION_DENY) {
                noOfDenyRules++;
            } else if (value == Result.DECISION_PERMIT) {
                noOfPermitRules++;
            }
        }

        if(noOfPermitRules > noOfDenyRules){
            return ResultFactory.getFactory().getResult(Result.DECISION_PERMIT, context);            
        } else {
            return ResultFactory.getFactory().getResult(Result.DECISION_DENY, context);     
        }
    }
}
