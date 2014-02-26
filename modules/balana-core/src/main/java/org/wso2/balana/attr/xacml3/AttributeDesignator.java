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

package org.wso2.balana.attr.xacml3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.*;
import org.wso2.balana.attr.AbstractDesignator;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.MissingAttributeDetail;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.StatusDetail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class AttributeDesignator extends AbstractDesignator {

    // required attributes
    private URI type;
    private URI id;

    // optional attribute
    private String issuer;

    // must resolution find something
    private boolean mustBePresent;

    private URI category;

    // the logger we'll use for all messages
    private static Log logger = LogFactory.getLog(AttributeDesignator.class);


    /**
     * Creates a new <code>AttributeDesignator</code> without the optional issuer.
     *
     * @param type          the data type resolved by this designator
     * @param id            the attribute id looked for by this designator
     * @param mustBePresent whether resolution must find a value
     * @param category
     */
    public AttributeDesignator(URI type, URI id, boolean mustBePresent, URI category) {
        this(type, id, mustBePresent, null, category);
    }

    /**
     * Creates a new <code>AttributeDesignator</code> with the optional issuer.
     *
     * @param type          the data type resolved by this designator
     * @param id            the attribute id looked for by this designator
     * @param mustBePresent whether resolution must find a value
     * @param issuer        the issuer of the values to search for or null if no issuer is specified
     * @param category
     * @throws IllegalArgumentException if the input target isn't a valid value
     */
    public AttributeDesignator(URI type, URI id, boolean mustBePresent, String issuer,
                               URI category) throws IllegalArgumentException {
        this.type = type;
        this.id = id;
        this.mustBePresent = mustBePresent;
        this.issuer = issuer;
        this.category = category;
    }


    /**
     * Creates a new <code>AttributeDesignator</code> based on the DOM root of the XML data.
     *
     * @param root  the DOM root of the AttributeDesignatorType XML type
     * @return the designator
     * @throws ParsingException if the AttributeDesignatorType was invalid
     */
    public static AttributeDesignator getInstance(Node root) throws ParsingException {

        URI type = null;
        URI id = null;
        String issuer = null;
        URI category = null;
        boolean mustBePresent = false;

        // First check to be sure the node passed is indeed a AttributeDesignator node.
        String tagName = DOMHelper.getLocalName(root);
        if (!tagName.equals("AttributeDesignator")) {
            throw new ParsingException("AttributeDesignator cannot be constructed using " + "type: "
                    + DOMHelper.getLocalName(root));
        }

        NamedNodeMap attrs = root.getAttributes();

        try {
            id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Required AttributeId missing in " + "AttributeDesignator", e);
        }

        try {
            category = new URI(attrs.getNamedItem("Category").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Required Category missing in " + "AttributeDesignator", e);
        }

        try {
            String nodeValue = attrs.getNamedItem("MustBePresent").getNodeValue();
            if ("true".equals(nodeValue)) {
                mustBePresent = true;
            }
        } catch (Exception e) {
            throw new ParsingException("Required MustBePresent missing in " + "AttributeDesignator", e);
        }

        try {
            type = new URI(attrs.getNamedItem("DataType").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Required DataType missing in " + "AttributeDesignator", e);
        }

        try {
            Node node = attrs.getNamedItem("Issuer");
            if (node != null){
                issuer = node.getNodeValue();
            }
        } catch (Exception e) {
            throw new ParsingException(
                    "Error parsing AttributeDesignator " + "optional attributes", e);
        }

        return new AttributeDesignator(type, id, mustBePresent, issuer, category);
    }


    /**
     * Returns the type of attribute that is resolved by this designator. While an AD will always
     * return a bag, this method will always return the type that is stored in the bag.
     *
     * @return the attribute type
     */
    public URI getType() {
        return type;
    }

    /**
     * Returns the AttributeId of the values resolved by this designator.
     *
     * @return identifier for the values to resolve
     */
    public URI getId() {
        return id;
    }

    /**
     * Returns the category for this designator. 
     *
     * @return the category
     */
    public URI getCategory() {
        return category;
    }

    /**
     * Returns the issuer of the values resolved by this designator if specified.
     *
     * @return the attribute issuer or null if unspecified
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Returns whether or not a value is required to be resolved by this designator.
     *
     * @return true if a value is required, false otherwise
     */
    public boolean mustBePresent() {
        return mustBePresent;
    }

    /**
     * Always returns true, since a designator always returns a bag of attribute values.
     *
     * @return true
     */
    public boolean returnsBag() {
        return true;
    }

    /**
     * Always returns true, since a designator always returns a bag of attribute values.
     *
     * @return true
     * @deprecated As of 2.0, you should use the <code>returnsBag</code> method from the
     *             super-interface <code>Expression</code>.
     */
    public boolean evaluatesToBag() {
        return true;
    }

    /**
     * Always returns an empty list since designators never have children.
     *
     * @return an empty <code>List</code>
     */
    public List getChildren() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Evaluates the pre-assigned meta-data against the given context, trying to find some matching
     * values.
     *
     * @param context the representation of the request
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult evaluate(EvaluationCtx context) {
        EvaluationResult result = null;

        // look in  attribute values
        result = context.getAttribute(type, id, issuer, category);

        // if the lookup was indeterminate, then we return immediately
        if (result.indeterminate()){
            return result;
        }
        BagAttribute bag = (BagAttribute) (result.getAttributeValue());

        if (bag.isEmpty()) {
            // if it's empty, this may be an error
            if (mustBePresent) {
                if (logger.isDebugEnabled()) {
                    logger.debug("AttributeDesignator failed to resolve a "
                            + "value for a required attribute: " + id.toString());
                }

                ArrayList<String> code = new ArrayList<String>();
                code.add(Status.STATUS_MISSING_ATTRIBUTE);

                ArrayList<MissingAttributeDetail> missingAttributes = new ArrayList<MissingAttributeDetail>();
                MissingAttributeDetail missingAttribute = new MissingAttributeDetail(id, type,
                                        category, issuer, null, XACMLConstants.XACML_VERSION_3_0);
                missingAttributes.add(missingAttribute);
                StatusDetail detail = new StatusDetail(missingAttributes);

                String message = "Couldn't find AttributeDesignator attribute";

                // Note that there is a bug in the XACML spec. You can't
                // specify an identifier without specifying acceptable
                // values. Until this is fixed, this code will only
                // return the status code, and not any hints about what
                // was missing

                /*
                * List attrs = new ArrayList(); attrs.add(new Attribute(id, ((issuer == null) ?
                * null : issuer.toString()), null, null)); StatusDetail detail = new
                * StatusDetail(attrs);
                */

                return new EvaluationResult(new Status(code, message, detail));
            }
        }

        // if we got here the bag wasn't empty, or mustBePresent was false,
        // so we just return the result
        return result;
    }

    /**
     * Encodes this <code>AttributeDesignator</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<AttributeDesignator");

        builder.append(" AttributeId=\"").append(id.toString()).append("\"");
        builder.append(" DataType=\"").append(type.toString()).append("\"");
        builder.append(" Category=\"").append(category.toString()).append("\"");

        if (issuer != null) {
            builder.append(" Issuer=\"").append(issuer).append("\"");
        }
        
        if (mustBePresent) {
            builder.append(" MustBePresent=\"true\"");
        } else {
            builder.append(" MustBePresent=\"false\"");
        }
        
        builder.append("/>\n");
    }

}
