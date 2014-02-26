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

package org.wso2.balana.xacml3;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.xacml2.Result;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents the AdviceExpressionType XML type in XACML. Advice are introduced with XACML 3   
 */
public class AdviceExpression {

    /**
     * The value of the advice identifier may be interpreted by the PEP.
     */
    private URI adviceId;

    /**
     * The effect for which this advice must be provided to the PEP.
     */
    private int appliesTo;

    /**
     * Advice arguments in the form of expressions; <code>AttributeAssignmentExpression</code>
     */
    private List<AttributeAssignmentExpression> attributeAssignmentExpressions;


    /**
     * Constructor that takes all the data associated with an AdviceExpression
     * .
     * @param adviceId the advice's id
     * @param appliesTo the effect for which this advice must be provided
     * @param attributeAssignmentExpressions a <code>List</code> of <code>AttributeAssignmentExpression</code>s
     */
    public AdviceExpression(URI adviceId, int appliesTo,
                            List<AttributeAssignmentExpression> attributeAssignmentExpressions) {
        this.adviceId = adviceId;
        this.appliesTo = appliesTo;
        this.attributeAssignmentExpressions = attributeAssignmentExpressions;
    }

    /**
     *  Creates an instance of <code>AdviceExpression</code> based on the DOM root node.
     *
     * @param root  the DOM root of the AdviceExpressionType XML type
     * @param metaData policy meta data
     * @return an instance of an <code>AdviceExpression</code>
     * @throws ParsingException  if the structure isn't valid
     */
    public static AdviceExpression getInstance(Node root, PolicyMetaData metaData) throws ParsingException {

        URI adviceId;
        int appliesTo;
        String effect;
        List<AttributeAssignmentExpression> expressions = new ArrayList<AttributeAssignmentExpression>();
        NamedNodeMap attrs = root.getAttributes();

        try {
            adviceId = new URI(attrs.getNamedItem("AdviceId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute AdviceId " +
                    "in AdviceExpressionType", e);
        }

        try {
            effect = attrs.getNamedItem("AppliesTo").getNodeValue();
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute AppliesTo " +
                    "in AdviceExpressionType", e);
        }

        if (effect.equals("Permit")) {
            appliesTo = AbstractResult.DECISION_PERMIT;
        } else if (effect.equals("Deny")) {
            appliesTo = AbstractResult.DECISION_DENY;
        } else {
            throw new ParsingException("Invalid Effect type: " + effect);
        }

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("AttributeAssignmentExpression")) {
                try {
                    AttributeAssignmentExpression expression = AttributeAssignmentExpression.
                            getInstance(node, metaData);
                    expressions.add(expression);
                } catch (Exception e) {
                    throw new ParsingException("Error parsing attribute assignments " +
                            "in AdviceExpressionType", e);
                }
            }
        }

        return new AdviceExpression(adviceId, appliesTo, expressions);
    }

    /**
     * returns whether this is applied for permit or deny
     *
     * @return permit/deny
     */
    public int getAppliesTo() {
        return appliesTo;
    }

    /**
     * returns advice id
     *
     * @return advice id
     */
    public URI getAdviceId() {
        return adviceId;
    }

    /**
     * return  evaluation result of the advice expression
     *
     * @param ctx  <code>EvaluationCtx</code>
     * @return  result as <code>Advice</code> Object
     */
    public Advice evaluate(EvaluationCtx ctx) {
        List<AttributeAssignment> assignments = new ArrayList<AttributeAssignment>();
        for(AttributeAssignmentExpression expression : attributeAssignmentExpressions){
            Set<AttributeAssignment> assignmentSet = expression.evaluate(ctx);
            if(assignmentSet != null && assignmentSet.size() > 0){
                assignments.addAll(assignmentSet);
            }
        }
        return new Advice(adviceId, assignments);
    }


    /**
     * Encodes this <code>ObligationExpression</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<AdviceExpression AdviceId=\"" + adviceId + "\" AppliesTo=\""
                + AbstractResult.DECISIONS[appliesTo] + "\">");
        for (AttributeAssignmentExpression assignment : attributeAssignmentExpressions) {
            assignment.encode(builder);
        }
        builder.append("</AdviceExpression>");
    }
}
