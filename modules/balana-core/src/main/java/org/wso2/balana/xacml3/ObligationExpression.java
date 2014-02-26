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
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.xacml2.Result;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents ObligationExpressionType in the XACML 3.0 policy schema
 */
public class ObligationExpression extends AbstractObligation {

    /**
     * <code>List</code> of <code>AttributeAssignmentExpression</code> that contains in
     * <code>ObligationExpression</code>
     *
     */
    private List<AttributeAssignmentExpression> expressions;


    /**
     * Constructor that creates a new <code>ObligationExpression</code> based on
     * the given elements.
     *
     * @param fulfillOn  effect that will cause this obligation to be included in a response 
     * @param expressions  <code>List</code> of <code>AttributeAssignmentExpression</code>
     * @param obligationId Identifier that uniquely identify ObligationExpression element
     */
    public ObligationExpression(int fulfillOn, List<AttributeAssignmentExpression> expressions,
                                URI obligationId) {
        this.fulfillOn = fulfillOn;
        this.expressions = expressions;
        this.obligationId = obligationId;
    }

    /**
     *  creates a <code>ObligationExpression</code> based on its DOM node.
     *
     * @param root root the node to parse for the ObligationExpression
     * @param metaData  meta-date associated with the policy
     * @return  a new <code>ObligationExpression</code> constructed by parsing
     * @throws ParsingException if the DOM node is invalid
     */
    public static ObligationExpression getInstance(Node root, PolicyMetaData metaData)
            throws ParsingException {

        List<AttributeAssignmentExpression> expressions =
                new ArrayList<AttributeAssignmentExpression>();
        URI obligationId;
        int fulfillOn;
        String effect;

        // First check that we're really parsing an Attribute
        if (!DOMHelper.getLocalName(root).equals("ObligationExpression")) {
            throw new ParsingException("ObligationExpression object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NamedNodeMap nodeAttributes = root.getAttributes();

        try {
            obligationId = new URI(nodeAttributes.getNamedItem("ObligationId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required ObligationId in " +
                    "ObligationExpressionType", e);
        }

        try {
            effect = nodeAttributes.getNamedItem("FulfillOn").getNodeValue();
        } catch (Exception e) {
            throw new ParsingException("Error parsing required FulfillOn in " +
                    "ObligationExpressionType", e);
        }

        if("Permit".equals(effect)){
            fulfillOn = Result.DECISION_PERMIT;
        } else if("Deny".equals(effect)){
            fulfillOn = Result.DECISION_DENY;
        } else {
            throw new ParsingException("Invalid FulfillOn : " + effect);
        }

        NodeList children = root.getChildNodes();

        for(int i = 0; i < children.getLength(); i ++){
            Node child = children.item(i);
            if("AttributeAssignmentExpression".equals(DOMHelper.getLocalName(child))){
                expressions.add(AttributeAssignmentExpression.getInstance(child, metaData));
            }
        }

        return new ObligationExpression(fulfillOn, expressions, obligationId);

    }

    @Override
    public ObligationResult evaluate(EvaluationCtx ctx) {
        List<AttributeAssignment> assignments = new ArrayList<AttributeAssignment>();
        for(AttributeAssignmentExpression expression : expressions){
            Set<AttributeAssignment> assignmentSet = expression.evaluate(ctx);
            if(assignmentSet != null && assignmentSet.size() > 0){
                assignments.addAll(assignmentSet);    
            }
        }
        return new Obligation(assignments, obligationId);
    }

    /**
     * Encodes this <code>ObligationExpression</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<ObligationExpression ObligationId=\"").append(obligationId.toString()).
                append("\" FulfillOn=\"").append(AbstractResult.DECISIONS[fulfillOn]).append("\">\n");
        for (AttributeAssignmentExpression assignment : expressions) {
            assignment.encode(builder);
        }
        builder.append("</ObligationExpression>");
    }

}
