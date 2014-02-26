/*
 * @(#)DenyOverridesPolicyAlg.java
 *
 * Copyright 2003-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */

package org.wso2.balana.combine.xacml2;

import org.wso2.balana.*;

import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.xacml2.Result;
import org.wso2.balana.xacml3.Advice;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

/**
 * This is the standard Deny Overrides policy combining algorithm. It allows a single evaluation of
 * Deny to take precedence over any number of permit, not applicable or indeterminate results. Note
 * that since this implementation does an ordered evaluation, this class also supports the Ordered
 * Deny Overrides algorithm.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class DenyOverridesPolicyAlg extends PolicyCombiningAlgorithm {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:"
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
    public DenyOverridesPolicyAlg() {
        super(identifierURI);

        if (earlyException != null)
            throw earlyException;
    }

    /**
     * Protected constructor used by the ordered version of this algorithm.
     * 
     * @param identifier the algorithm's identifier
     */
    protected DenyOverridesPolicyAlg(URI identifier) {
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
        boolean atLeastOnePermit = false;
        List<ObligationResult> permitObligations = new ArrayList<ObligationResult>();
        List<Advice> permitAdvices= new ArrayList<Advice>();
        Iterator it = policyElements.iterator();

        while (it.hasNext()) {
            AbstractPolicy policy = ((PolicyCombinerElement) (it.next())).getPolicy();
            // make sure that the policy matches the context
            MatchResult match = policy.match(context);

            if (match.getResult() == MatchResult.INDETERMINATE){ //TODO  do we really want this?
                return ResultFactory.getFactory().getResult(AbstractResult.DECISION_DENY, context);
            }

            if (match.getResult() == MatchResult.MATCH) {
                // evaluate the policy
                AbstractResult result = policy.evaluate(context);
                int effect = result.getDecision();

                // unlike in the RuleCombining version of this alg, we always
                // return DENY if any Policy returns DENY or INDETERMINATE
                if (effect == AbstractResult.DECISION_DENY){
                    return result;
                }
                
                if (effect == AbstractResult.DECISION_INDETERMINATE ||
                    effect == AbstractResult.DECISION_INDETERMINATE_DENY ||
                    effect == AbstractResult.DECISION_INDETERMINATE_PERMIT ||
                    effect == AbstractResult.DECISION_INDETERMINATE_DENY_OR_PERMIT) {
                    
                    return ResultFactory.getFactory().getResult(Result.DECISION_DENY, context);
                }
                // remember if at least one Policy said PERMIT
                if (effect == Result.DECISION_PERMIT) {
                    atLeastOnePermit = true;
                    permitAdvices.addAll(result.getAdvices());
                    permitObligations.addAll(result.getObligations());
                }
            }
        }

        // if we got a PERMIT, return it, otherwise it's NOT_APPLICABLE
        if (atLeastOnePermit) {
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_PERMIT,
                                                        permitObligations, permitAdvices, context);
        } else {
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_NOT_APPLICABLE, context); 
        }
    }

}
