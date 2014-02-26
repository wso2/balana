/*
 * @(#)PermitOverridesPolicyAlg.java
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
import org.wso2.balana.ctx.Status;
import org.wso2.balana.xacml3.Advice;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

/**
 * This is the standard Permit Overrides policy combining algorithm. It allows a single evaluation
 * of Permit to take precedence over any number of deny, not applicable or indeterminate results.
 * Note that since this implementation does an ordered evaluation, this class also supports the
 * Ordered Permit Overrides algorithm.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class PermitOverridesPolicyAlg extends PolicyCombiningAlgorithm {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:"
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
        boolean atLeastOneError = false;
        boolean atLeastOneDeny = false;
        List<ObligationResult> denyObligations = new ArrayList<ObligationResult>();
        List<Advice> denyAdvices = new ArrayList<Advice>();
        Status firstIndeterminateStatus = null;
        Iterator it = policyElements.iterator();

        while (it.hasNext()) {
            AbstractPolicy policy = ((PolicyCombinerElement) (it.next())).getPolicy();

            // make sure that the policy matches the context
            MatchResult match = policy.match(context);

            if (match.getResult() == MatchResult.INDETERMINATE) {
                atLeastOneError = true;

                // keep track of the first error, regardless of cause
                if (firstIndeterminateStatus == null){
                    firstIndeterminateStatus = match.getStatus();
                }
            } else if (match.getResult() == MatchResult.MATCH) {
                // now we evaluate the policy
                AbstractResult result = policy.evaluate(context);
                int effect = result.getDecision();

                // this is a little different from DenyOverrides...

                if (effect == Result.DECISION_PERMIT)
                    return result;

                if (effect == Result.DECISION_DENY) {
                    atLeastOneDeny = true;
                    denyAdvices.addAll(result.getAdvices());
                    denyObligations.addAll(result.getObligations());
                } else if (effect == AbstractResult.DECISION_INDETERMINATE ||
                    effect == AbstractResult.DECISION_INDETERMINATE_DENY ||
                    effect == AbstractResult.DECISION_INDETERMINATE_PERMIT ||
                    effect == AbstractResult.DECISION_INDETERMINATE_DENY_OR_PERMIT) {
                    
                    atLeastOneError = true;
                    // keep track of the first error, regardless of cause
                    if (firstIndeterminateStatus == null)
                        firstIndeterminateStatus = result.getStatus();
                }
            }
        }

        // if we got a DENY, return it
        if (atLeastOneDeny){
            return ResultFactory.getFactory().getResult(Result.DECISION_DENY, denyObligations,
                                                                            denyAdvices, context);
        }
        // if we got an INDETERMINATE, return it
        if (atLeastOneError){
            return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE,
                    firstIndeterminateStatus, context);
        }

        // if we got here, then nothing applied to us
        //return new Result(Result.DECISION_NOT_APPLICABLE, context.getResourceId().encode());
        return ResultFactory.getFactory().getResult(Result.DECISION_NOT_APPLICABLE, context);
    }

}
