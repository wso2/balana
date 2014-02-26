/*
 * @(#)PDP.java
 *
 * Copyright 2003-2004 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.wso2.balana.combine.CombinerElement;
import org.wso2.balana.ctx.*;

import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx;
import org.wso2.balana.xacml3.MultipleCtxResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.*;

/**
 * This is the core class for the XACML engine, providing the starting point for request evaluation.
 * To build an XACML policy engine, you start by instantiating this object.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class PDP {

    /**
     * the encapsulate the <code>PDP</code> related configurations
     */
    private PDPConfig pdpConfig;

    /**
     * the single policy finder that will be used to resolve policies
     */
	private PolicyFinder policyFinder;

    /**
     * the logger we'll use for all messages
     */
	private static Log logger = LogFactory.getLog(PDP.class);

	/**
	 * Constructs a new <code>PDP</code> object with the given configuration information.
	 * 
	 * @param pdpConfig user configuration data defining how to find policies, resolve external
	 *            attributes, etc.
	 */
	public PDP(PDPConfig pdpConfig) {

		if (logger.isDebugEnabled()) {
			logger.debug("creating a PDP");
		}

        this.pdpConfig = pdpConfig;
        
		policyFinder = pdpConfig.getPolicyFinder();
		policyFinder.init();
	}

    /**
     * Attempts to evaluate the request against the policies known to this PDP. This is really the
     * core method of the entire XACML specification, and for most people will provide what you
     * want. If you need any special handling, you should look at the version of this method that
     * takes an <code>EvaluationCtx</code>.
     * <p>
     * Note that if the request is somehow invalid (it was missing a required attribute, it was
     * using an unsupported scope, etc), then the result will be a decision of INDETERMINATE.
     *
     * @param request the request to evaluate
     *
     * @return a response paired to the request
     */
    public String evaluate(String request) {

        AbstractRequestCtx  requestCtx;
        ResponseCtx responseCtx;

        try {
            requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.replaceAll(">\\s+<", "><"));
            responseCtx = evaluate(requestCtx);
        } catch (ParsingException e) {
            String error = "Invalid request  : " + e.getMessage();
            logger.error(error);
            // there was something wrong with the request, so we return
            // Indeterminate with a status of syntax error...though this
            // may change if a more appropriate status type exists
            ArrayList<String> code = new ArrayList<String>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(code, error);
            //As invalid request, by default XACML 3.0 response is created. 
            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, status));
        }

        return responseCtx.encode();
    }


	/**
	 * Attempts to evaluate the request against the policies known to this PDP. This is really the
	 * core method of the entire XACML specification, and for most people will provide what you
	 * want. If you need any special handling, you should look at the version of this method that
	 * takes an <code>EvaluationCtx</code>.
	 * <p>
	 * Note that if the request is somehow invalid (it was missing a required attribute, it was
	 * using an unsupported scope, etc), then the result will be a decision of INDETERMINATE.
	 * 
	 * @param request the request to evaluate
	 * 
	 * @return a response paired to the request
	 */
	public ResponseCtx evaluate(AbstractRequestCtx request) {

        EvaluationCtx evalContext = null;
		try {
            evalContext = EvaluationCtxFactory.getFactory().getEvaluationCtx(request, pdpConfig);
			return evaluate(evalContext);
		} catch (ParsingException e) {
			logger.error("Invalid request  : " + e.getMessage());
			// there was something wrong with the request, so we return
			// Indeterminate with a status of syntax error...though this
			// may change if a more appropriate status type exists
			ArrayList<String> code = new ArrayList<String>();
			code.add(Status.STATUS_SYNTAX_ERROR);
			Status status = new Status(code, e.getMessage());
			return new ResponseCtx(ResultFactory.getFactory().
                getResult(AbstractResult.DECISION_INDETERMINATE, status, request.getXacmlVersion()));

		}
    }

	/**
	 * Uses the given <code>EvaluationCtx</code> against the available policies to determine a
	 * response. If you are starting with a standard XACML Request, then you should use the version
	 * of this method that takes a <code>RequestCtx</code>. This method should be used only if you
	 * have a real need to directly construct an evaluation context (or if you need to use an
	 * <code>EvaluationCtx</code> implementation other than <code>XACML3EvaluationCtx</code> and
     * <code>XACML2EvaluationCtx</code>).
	 * 
	 * @param context representation of the request and the context used for evaluation
	 * 
	 * @return a response based on the contents of the context
	 */
	public ResponseCtx evaluate(EvaluationCtx context) {

        // check whether this PDP configure to support multiple decision profile
        if(pdpConfig.isMultipleRequestHandle()){

            Set<EvaluationCtx> evaluationCtxSet;
            MultipleCtxResult multipleCtxResult = context.getMultipleEvaluationCtx();
            if(multipleCtxResult.isIndeterminate()){
                return new ResponseCtx(ResultFactory.getFactory().
                        getResult(AbstractResult.DECISION_INDETERMINATE,multipleCtxResult.getStatus(), context));
            } else {
                evaluationCtxSet = multipleCtxResult.getEvaluationCtxSet();                
                HashSet<AbstractResult> results = new HashSet<AbstractResult>();
                for(EvaluationCtx ctx : evaluationCtxSet){
                    // do the evaluation, for all evaluate context
                    AbstractResult result = evaluateContext(ctx);
                    // add the result
                    results.add(result);
                }
                // XACML 3.0.version
                return new ResponseCtx(results, XACMLConstants.XACML_VERSION_3_0);
            }
        } else {
            // this is special case that specific to XACML3 request

            if(context instanceof XACML3EvaluationCtx && ((XACML3EvaluationCtx)context).
                                                                            isMultipleAttributes()){
                ArrayList<String> code = new ArrayList<String>();
                code.add(Status.STATUS_SYNTAX_ERROR);
                Status status = new Status(code, "PDP does not supports multiple decision profile. " +
                        "Multiple AttributesType elements with the same Category can be existed");
                return new ResponseCtx(ResultFactory.getFactory().
                        getResult(AbstractResult.DECISION_INDETERMINATE,
                        status, context));
            } else if(context instanceof XACML3EvaluationCtx && ((RequestCtx)context.
                    getRequestCtx()).isCombinedDecision()){
                List<String> code = new ArrayList<String>();
                code.add(Status.STATUS_PROCESSING_ERROR);
                Status status = new Status(code, "PDP does not supports multiple decision profile. " +
                        "Multiple decision is not existed to combine them");
                return new ResponseCtx(ResultFactory.getFactory().
                        getResult(AbstractResult.DECISION_INDETERMINATE,
                        status, context));
            } else {
                return new ResponseCtx(evaluateContext(context));
            }
        }

	}

	/**
	 * A private helper routine that resolves a policy for the given context, and then tries to
	 * evaluate based on the policy
     *
     * @param context  context
     * @return a response
     */
	private AbstractResult evaluateContext(EvaluationCtx context) {
		// first off, try to find a policy
		PolicyFinderResult finderResult = policyFinder.findPolicy(context);

		// see if there weren't any applicable policies
		if (finderResult.notApplicable()){
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_NOT_APPLICABLE, context);
        }
		// see if there were any errors in trying to get a policy
		if (finderResult.indeterminate()){
            return ResultFactory.getFactory().getResult(AbstractResult.DECISION_INDETERMINATE,
                    finderResult.getStatus(), context);
        }

		// we found a valid policy,

        // list all found policies if XACML 3.0
        if(context instanceof XACML3EvaluationCtx && ((RequestCtx)context.getRequestCtx()).
                                                                            isReturnPolicyIdList()){
            Set<PolicyReference> references = new HashSet<PolicyReference>();
            processPolicyReferences(finderResult.getPolicy(), references);
            ((XACML3EvaluationCtx) context).setPolicyReferences(references);
        }

        // so we can do the evaluation
		return finderResult.getPolicy().evaluate(context);
	}

	/**
	 * A utility method that wraps the functionality of the other evaluate method with input and
	 * output streams. This is useful if you've got a PDP that is taking inputs from some stream and
	 * is returning responses through the same stream system. If the Request is invalid, then this
	 * will always return a decision of INDETERMINATE.
	 * 
	 * @deprecated As of 1.2 this method should not be used. Instead, you should do your own stream
	 *             handling, and then use one of the other <code>evaluate</code> methods. The
	 *             problem with this method is that it often doesn't handle stream termination
	 *             correctly (eg, with sockets).
	 * 
	 * @param input a stream that contains an XML RequestType
	 * 
	 * @return a stream that contains an XML ResponseType
	 */
	public OutputStream evaluate(InputStream input) {
		AbstractRequestCtx request = null;
		ResponseCtx response = null;

		try {
			request = RequestCtxFactory.getFactory().getRequestCtx(input);
		} catch (Exception pe) {
			// the request wasn't formed correctly
			ArrayList<String> code = new ArrayList<String>();
			code.add(Status.STATUS_SYNTAX_ERROR);
			Status status = new Status(code, "invalid request: " + pe.getMessage());
            // can not determine XACML version at here. therefore return assume as XACML 3
            response = new ResponseCtx(ResultFactory.getFactory().
                getResult(AbstractResult.DECISION_INDETERMINATE, status, XACMLConstants.XACML_VERSION_3_0));
		}

		// if we didn't have a problem above, then we should go ahead
		// with the evaluation
		if (response == null){
			response = evaluate(request);
        }

		ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(response.encode().getBytes());
        } catch (IOException e) {
            logger.error("Error creating output stream of XACML response", e);    
        }

        return out;
	}

    /**
     *
     * @param policy
     * @param references
     */
    private void processPolicyReferences(AbstractPolicy policy, Set<PolicyReference> references){

        if(policy instanceof Policy){
            references.add(new PolicyReference(policy.getId(),
                                                PolicyReference.POLICY_REFERENCE, null, null));
        } else if(policy instanceof PolicySet){
            List<CombinerElement> elements = policy.getChildElements();
            if(elements != null && elements.size() > 0){
                for(CombinerElement element : elements){
                    PolicyTreeElement treeElement = element.getElement();
                    if(treeElement instanceof AbstractPolicy){
                        processPolicyReferences(policy, references);
                    } else {
                        references.add(new PolicyReference(policy.getId(),
                                            PolicyReference.POLICYSET_REFERENCE, null, null));
                    }
                }
            }
        }
    }

}
