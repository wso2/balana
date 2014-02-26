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

package org.wso2.balana.xacml2;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.xacml2.Result;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents ObligationType in the XACML 2.0 policy schema. In XACML 2.0, this element represent both
 * in policy and result. Therefore this has extends the <code>AbstractObligation</code> and implements
 * the  <code>ObligationResult</code>
 */
public class Obligation extends AbstractObligation implements ObligationResult {

    /**
     *  A <code>List</code> of  <code>Attribute</code>
     */
    private List<Attribute> assignments;


    /**
     * Constructor that takes all the data associated with an obligation. The attribute assignment
     * list contains <code>Attribute</code> objects, but only the fields used by the
     * AttributeAssignmentType are used.
     *
     * @param obligationId the obligation's id
     * @param fulfillOn the effect denoting when to fulfill this obligation
     * @param assignments a <code>List</code> of <code>Attribute</code>s
     */
    public Obligation(URI obligationId, int fulfillOn, List<Attribute> assignments) {
        this.obligationId = obligationId;
        this.fulfillOn = fulfillOn;
        this.assignments = Collections.unmodifiableList(new ArrayList<Attribute>(assignments));
    }

    /**
     * Creates an instance of <code>Obligation</code> based on the DOM root node.
     *
     * @param root the DOM root of the ObligationType XML type
     *
     * @return an instance of an obligation
     *
     * @throws ParsingException if the structure isn't valid
     */
    public static Obligation getInstance(Node root) throws ParsingException {
        
        URI id;
        int fulfillOn;
        String effect;
        List<Attribute> assignments = new ArrayList<Attribute>();
        
        AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
        NamedNodeMap attrs = root.getAttributes();

        try {
            id = new URI(attrs.getNamedItem("ObligationId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute ObligationId", e);
        }

        try {
            effect = attrs.getNamedItem("FulfillOn").getNodeValue();
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute FulfillOn", e);
        }

        if (effect.equals("Permit")) {
            fulfillOn = Result.DECISION_PERMIT;
        } else if (effect.equals("Deny")) {
            fulfillOn = Result.DECISION_DENY;
        } else {
            throw new ParsingException("Invalid Effect type: " + effect);
        }

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("AttributeAssignment")) {
                try {
                    URI attrId = new URI(node.getAttributes().getNamedItem("AttributeId")
                            .getNodeValue());
                    AttributeValue attrValue = attrFactory.createValue(node);
                    assignments.add(new Attribute(attrId, null, null, attrValue,
                                                                XACMLConstants.XACML_VERSION_2_0));
                } catch (URISyntaxException use) {
                    throw new ParsingException("Error parsing URI", use);
                } catch (UnknownIdentifierException uie) {
                    throw new ParsingException(uie.getMessage(), uie);
                } catch (Exception e) {
                    throw new ParsingException("Error parsing attribute assignments", e);
                }
            }
        }

        return new Obligation(id, fulfillOn, assignments);
    }


    @Override
    public ObligationResult evaluate(EvaluationCtx ctx) {
        return new Obligation(obligationId, fulfillOn, assignments);
    }

    /**
     * Returns the attribute assignment data in this obligation. The <code>List</code> contains
     * objects of type <code>Attribute</code> with only the correct attribute fields being used.
     *
     * @return the assignments
     */
    public List<Attribute> getAssignments() {
        return assignments;
    }

    /**
     * Encodes this <code>Obligation</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>Obligation</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<Obligation ObligationId=\"").append(obligationId.toString()).
                append("\" FulfillOn=\"").append(Result.DECISIONS[fulfillOn]).append("\">\n");
        for (Attribute assignment : assignments) {
            builder.append("<AttributeAssignment AttributeId=\"").
                    append(assignment.getId().toString()).append("\" DataType=\"").
                    append(assignment.getType().toString()).append("\">").
                    append(assignment.getValue().encode()).append("</AttributeAssignment>\n");
        }
        builder.append("</Obligation>");
    }
}
