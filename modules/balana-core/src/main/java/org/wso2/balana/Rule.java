/*
 * @(#)Rule.java
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

package org.wso2.balana;

import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.xacml2.Result;
import org.wso2.balana.xacml3.Advice;
import org.wso2.balana.xacml3.AdviceExpression;
import org.wso2.balana.attr.BooleanAttribute;

import org.wso2.balana.cond.Apply;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.VariableManager;

import org.wso2.balana.ctx.Status;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.xacml3.ObligationExpression;

/**
 * Represents the RuleType XACML type. This has a target for matching, and encapsulates the
 * condition and all sub-operations that make up the heart of most policies.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class Rule implements PolicyTreeElement {

    // the attributes associated with this Rule
    private URI idAttr;
    private int effectAttr;
    // any obligations held by this Rule
    private Set<AbstractObligation> obligationExpressions;

    // any obligations held by this Rule
    private Set<AdviceExpression> adviceExpressions;
    // the elements in the rule, each of which is optional
    private String description = null;
    private AbstractTarget target = null;
    private Condition condition = null;
    private int xacmlVersion;

    /**
     * Creates a new <code>Rule</code> object for XACML 1.x and 2.0.
     * 
     * @param id the rule's identifier
     * @param effect the effect to return if the rule applies (either Pemit or Deny) as specified in
     *            <code>Result</code>
     * @param description a textual description, or null
     * @param target the rule's target, or null if the target is to be inherited from the
     *            encompassing policy
     * @param condition the rule's condition, or null if there is none
     * @param obligationExpressions  the rule's ObligationExpressions
     * @param adviceExpressions   the rule's AdviceExpressions
     * @param xacmlVersion xacml version
     */
    public Rule(URI id, int effect, String description, AbstractTarget target, Condition condition,
                Set<AbstractObligation> obligationExpressions, Set<AdviceExpression> adviceExpressions,
                                                                                int xacmlVersion) {
        idAttr = id;
        effectAttr = effect;
        this.description = description;
        this.target = target;
        this.condition = condition;
        this.adviceExpressions = adviceExpressions;
        this.obligationExpressions = obligationExpressions;
        this.xacmlVersion = xacmlVersion;
    }

    /**
     * Creates a new <code>Rule</code> object for XACML 1.x only.
     * 
     * @deprecated As of 2.0 you should use the Constructor that accepts the new
     *             <code>Condition</code> class.
     * 
     * @param id the rule's identifier
     * @param effect the effect to return if the rule applies (either Pemit or Deny) as specified in
     *            <code>Result</code>
     * @param description a textual description, or null
     * @param target the rule's target, or null if the target is to be inherited from the
     *            encompassing policy
     * @param condition the rule's condition, or null if there is none
     * @param xacmlVersion  xacml version
     */
    public Rule(URI id, int effect, String description, AbstractTarget target, Apply condition,
                                                                                int xacmlVersion) {
        idAttr = id;
        effectAttr = effect;
        this.description = description;
        this.target = target;
        this.condition = new Condition(condition.getFunction(), condition.getChildren());
        this.xacmlVersion = xacmlVersion;
    }


    /**
     * Creates a new <code>Rule</code> object for XACML 1.x only.
     *
     * @deprecated As of 2.0 you should use the Constructor that accepts the new
     *             <code>Condition</code> class.
     *
     * @param id the rule's identifier
     * @param effect the effect to return if the rule applies (either Pemit or Deny) as specified in
     *            <code>Result</code>
     * @param description a textual description, or null
     * @param target the rule's target, or null if the target is to be inherited from the
     *            encompassing policy
     * @param condition the rule's condition, or null if there is none
     */
    public Rule(URI id, int effect, String description, AbstractTarget target, Condition condition) {
        idAttr = id;
        effectAttr = effect;
        this.description = description;
        this.target = target;
        this.condition = new Condition(condition.getFunction(), condition.getChildren());
    }
    /**
     * Returns a new instance of the <code>Rule</code> class based on a DOM node. The node must be
     * the root of an XML RuleType.
     * 
     * @deprecated As of 2.0 you should avoid using this method and should instead use the version
     *             that takes a <code>PolicyMetaData</code> instance. This method will only work for
     *             XACML 1.x policies.
     * 
     * @param root the DOM root of a RuleType XML type
     * @param xpathVersion the XPath version to use in any selectors or XPath functions, or null if
     *            this is unspecified (ie, not supplied in the defaults section of the policy)
     * 
     * @throws ParsingException if the RuleType is invalid
     */
    public static Rule getInstance(Node root, String xpathVersion) throws ParsingException {
        return getInstance(root, new PolicyMetaData(XACMLConstants.XACML_1_0_IDENTIFIER,
                xpathVersion), null);
    }

    /**
     * Returns a new instance of the <code>Rule</code> class based on a DOM node. The node must be
     * the root of an XML RuleType.
     * 
     * @param root the DOM root of a RuleType XML type
     * @param metaData the meta-data associated with this Rule's policy
     * @param manager the <code>VariableManager</code> used to connect
     *            <code>VariableReference</code>s to their cooresponding
     *            <code>VariableDefinition<code>s
     * 
     * @throws ParsingException if the RuleType is invalid
     */
    public static Rule getInstance(Node root, PolicyMetaData metaData, VariableManager manager)
            throws ParsingException {

        URI id = null;
        int effect = 0;
        String description = null;
        AbstractTarget target = null;
        Condition condition = null;
        Set<AbstractObligation> obligationExpressions = new HashSet<AbstractObligation>();
        Set<AdviceExpression> adviceExpressions = new HashSet<AdviceExpression>();

        // first, get the attributes
        NamedNodeMap attrs = root.getAttributes();

        try {
            // get the two required attrs...
            id = new URI(attrs.getNamedItem("RuleId").getNodeValue());
        } catch (URISyntaxException use) {
            throw new ParsingException("Error parsing required attribute " + "RuleId", use);
        }

        String str = attrs.getNamedItem("Effect").getNodeValue();
        if (str.equals("Permit")) {
            effect = Result.DECISION_PERMIT;
        } else if (str.equals("Deny")) {
            effect = Result.DECISION_DENY;
        } else {
            throw new ParsingException("Invalid Effect: " + effect);
        }

        // next, get the elements
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String cname = DOMHelper.getLocalName(child);

            if (cname.equals("Description")) {
                if(child.getFirstChild() != null){
                    description = child.getFirstChild().getNodeValue();
                }
            } else if (cname.equals("Target")) {
                target = TargetFactory.getFactory().getTarget(child, metaData);
            } else if (cname.equals("Condition")) {
                condition = Condition.getInstance(child, metaData, manager);
            } else if("ObligationExpressions".equals(cname)){
                NodeList nodes = child.getChildNodes();
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node node = nodes.item(j);
                    if ("ObligationExpression".equals(DOMHelper.getLocalName(node))){
                        obligationExpressions.add(ObligationFactory.getFactory().
                                getObligation(node, metaData));
                    }
                }
            } else if("AdviceExpressions".equals(cname)){
                NodeList nodes = child.getChildNodes();
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node node = nodes.item(j);
                    if ("AdviceExpression".equals(DOMHelper.getLocalName(node)))
                        adviceExpressions.add(AdviceExpression.getInstance(node, metaData));
                }
            }
        }

        return new Rule(id, effect, description, target, condition, obligationExpressions,
                                                    adviceExpressions, metaData.getXACMLVersion());
    }

    /**
     * Returns the effect that this <code>Rule</code> will return from the evaluate method (Permit
     * or Deny) if the request applies.
     * 
     * @return a decision effect, as defined in <code>Result</code>
     */
    public int getEffect() {
        return effectAttr;
    }

    /**
     * Returns the id of this <code>Rule</code>
     * 
     * @return the rule id
     */
    public URI getId() {
        return idAttr;
    }

    /**
     * Returns the given description of this <code>Rule</code> or null if there is no description
     * 
     * @return the description or null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the target for this <code>Rule</code> or null if there is no target
     * 
     * @return the rule's target
     */
    public AbstractTarget getTarget() {
        return target;
    }

    /**
     * Since a rule is always a leaf in a policy tree because it can have no children, this always
     * returns an empty <code>List</code>.
     * 
     * @return a <code>List</code> with no elements
     */
    public List getChildren() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns the condition for this <code>Rule</code> or null if there is no condition
     * 
     * @return the rule's condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Given the input context sees whether or not the request matches this <code>Rule</code>'s
     * <code>Target</code>. Note that unlike the matching done by the <code>evaluate</code> method,
     * if the <code>Target</code> is missing than this will return Indeterminate. This lets you
     * write your own custom matching routines for rules but lets evaluation proceed normally.
     * 
     * @param context the representation of the request
     * 
     * @return the result of trying to match this rule and the request
     */
    public MatchResult match(EvaluationCtx context) {
        if (target == null) {
            ArrayList code = new ArrayList();
            code.add(Status.STATUS_PROCESSING_ERROR);
            Status status = new Status(code, "no target available for " + "matching a rule");

            return new MatchResult(MatchResult.INDETERMINATE, status);
        }

        return target.match(context);
    }

    /**
     * Evaluates the rule against the supplied context. This will check that the target matches, and
     * then try to evaluate the condition. If the target and condition apply, then the rule's effect
     * is returned in the result.
     * <p>
     * Note that rules are not required to have targets. If no target is specified, then the rule
     * inherits its parent's target. In the event that this <code>Rule</code> has no
     * <code>Target</code> then the match is assumed to be true, since evaluating a policy tree to
     * this level required the parent's target to match.
     * 
     * @param context the representation of the request we're evaluating
     * 
     * @return the result of the evaluation
     */
    public AbstractResult evaluate(EvaluationCtx context) {

        // If the Target is null then it's supposed to inherit from the
        // parent policy, so we skip the matching step assuming we wouldn't
        // be here unless the parent matched
        MatchResult match = null;
        
        if (target != null) {

            match = target.match(context);
            int result = match.getResult();

            // if the target didn't match, then this Rule doesn't apply
            if (result == MatchResult.NO_MATCH){
                return ResultFactory.getFactory().getResult(Result.DECISION_NOT_APPLICABLE, context);
            }

            // if the target was indeterminate, we can't go on
            if (result == MatchResult.INDETERMINATE){

                // defines extended indeterminate results with XACML 3.0
                if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0){
                    if(effectAttr == AbstractResult.DECISION_PERMIT){
                        return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE_PERMIT,
                                match.getStatus(), context);
                    } else {
                        return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE_DENY,
                                match.getStatus(), context);
                    }
                }

                return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE,
                        match.getStatus(), context);
            }
        }

        // if there's no condition, then we just return the effect
        if (condition == null){
            // if any obligations or advices are defined, evaluates them and return
            return  ResultFactory.getFactory().getResult(effectAttr, processObligations(context),
                                                        processAdvices(context), context);
        }

        // otherwise we evaluate the condition
        EvaluationResult result = condition.evaluate(context);

        if (result.indeterminate()) {

            // defines extended indeterminate results with XACML 3.0
            if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0){
                if(effectAttr == AbstractResult.DECISION_PERMIT){
                    return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE_PERMIT,
                            result.getStatus(), context);
                } else {
                    return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE_DENY,
                           result.getStatus(), context);
                }
            }

            // if it was INDETERMINATE, then that's what we return
            return ResultFactory.getFactory().getResult(Result.DECISION_INDETERMINATE,
                                                                       result.getStatus(), context);
        } else {
            // otherwise we return the effect on true, and NA on false
            BooleanAttribute bool = (BooleanAttribute) (result.getAttributeValue());

            if (bool.getValue()) {
                // if any obligations or advices are defined, evaluates them and return
                return  ResultFactory.getFactory().getResult(effectAttr, processObligations(context),
                                                            processAdvices(context), context);
            } else {
                return ResultFactory.getFactory().getResult(Result.DECISION_NOT_APPLICABLE, context);
            }
        }
    }

    /**
     * helper method to evaluate the obligations expressions
     *
     * @param evaluationCtx context of a single policy evaluation
     * @return list of <code>ObligationResult</code> or null
     */
    private List<ObligationResult> processObligations(EvaluationCtx evaluationCtx){

        if(obligationExpressions != null && obligationExpressions.size() > 0){
            List<ObligationResult>  results = new ArrayList<ObligationResult>();
            for(AbstractObligation obligationExpression : obligationExpressions){
                if(obligationExpression.getFulfillOn() == effectAttr) {
                    results.add(obligationExpression.evaluate(evaluationCtx));

                }
            }
            return results;
        }
        return null;
    }

    /**
     * helper method to evaluate the  advice expressions
     *
     * @param evaluationCtx context of a single policy evaluation
     * @return set of <code>Advice</code> or null
     */
    private List<Advice> processAdvices(EvaluationCtx evaluationCtx){
        if(adviceExpressions != null && adviceExpressions.size() > 0){
            List<Advice>  advices = new ArrayList<Advice>();
            for(AdviceExpression adviceExpression : adviceExpressions){
                if(adviceExpression.getAppliesTo() == effectAttr) {
                    advices.add(adviceExpression.evaluate(evaluationCtx));
                }
            }
            return advices;
        }
        return null;
    }

    public String encode() {
        return null; // TODO.
    }

    public void encode(StringBuilder builder) {

        builder.append("<Rule RuleId=\"" + idAttr + "\"" + " Effect=\"" +
                AbstractResult.DECISIONS[effectAttr] + "\"  >\n");


        if (description != null){
            builder.append("<Description>").append(description).append("</Description>\n");
        }

        if(target != null){
            target.encode(builder);
        }

        if(condition != null){
            condition.encode(builder);
        }

        if(obligationExpressions != null && obligationExpressions.size() > 0){

            if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0){
                builder.append("<Obligations>\n");
            } else {
                builder.append("<ObligationExpressions>\n");
            }

            for(AbstractObligation expression : obligationExpressions){
                expression.encode(builder);
            }

            if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0){
                builder.append("</Obligations>\n");
            } else {
                builder.append("</ObligationExpressions>\n");
            }
        }

        if(adviceExpressions != null && adviceExpressions.size() > 0){
            builder.append("<AdviceExpressions>");
            for(AdviceExpression expression : adviceExpressions){
                expression.encode(builder);
            }
            builder.append("</AdviceExpressions>\n");
        }

        builder.append("</Rule>\n");
    }
}
