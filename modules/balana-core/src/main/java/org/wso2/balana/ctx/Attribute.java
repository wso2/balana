/*
 * @(#)Attribute.java
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

package org.wso2.balana.ctx;

import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;

import java.io.PrintStream;
import java.io.OutputStream;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the AttributeType XML type found in the context schema.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class Attribute {

    /**
     * attribute identifier
     */
    private URI id;

    /**
     * attribute data type,  this is not used in XACML3
     */
    private URI type;

    /**
     * whether to include this attribute in the result. This is useful to correlate requests
     * with their responses in case of multiple requests.
     * optional one defined only in XACML3
     */
    private boolean includeInResult;

    /**
     * issuer of the attribute.   optional one
     */
    private String issuer = null;

    /**
     * issue instance of the attribute. this is not used in XACML3
     */
    private DateTimeAttribute issueInstant = null;

    /**
     * a <code>List</code> of <code>AttributeValue</code>  
     */
    private List<AttributeValue> attributeValues;

    private int xacmlVersion;

    /**
     * Creates a new <code>Attribute</code> of the type specified in the given
     * <code>AttributeValue</code>.for XACML 3 with one  <code>AttributeValue</code>
     *
     * @param id the id of the attribute
     * @param issuer the attribute's issuer or null if there is none
     * @param issueInstant the moment when the attribute was issued, or null if it's unspecified
     * @param value the actual value associated with the attribute meta-data
     * @param includeInResult  whether to include this attribute in the result.
     * @param version XACML version
     */
    public Attribute(URI id, String issuer, DateTimeAttribute issueInstant, AttributeValue value,
                      boolean includeInResult, int version) {
        this(id, value.getType(), issuer, issueInstant, Arrays.asList(value), includeInResult,version);
    }

    /**
     * Creates a new <code>Attribute</code>  for XACML 2 and XACML 1.X with one <code>AttributeValue</code>
     *
     * @param id the id of the attribute
     * @param issuer the attribute's issuer or null if there is none
     * @param issueInstant the moment when the attribute was issued, or null if it's unspecified
     * @param value actual <code>List</code> of <code>AttributeValue</code>  associated with
     * @param version XACML version
     */
    public Attribute(URI id, String issuer, DateTimeAttribute issueInstant, AttributeValue value,
                                                                                    int version) {

        this(id, value.getType(), issuer, issueInstant, Arrays.asList(value), false, version);
    }

    /**
     * Creates a new <code>Attribute</code>
     * 
     * @param id the id of the attribute
     * @param type the type of the attribute
     * @param issuer the attribute's issuer or null if there is none
     * @param issueInstant the moment when the attribute was issued, or null if it's unspecified
     * @param attributeValues actual <code>List</code> of <code>AttributeValue</code>  associated with
     * @param includeInResult whether to include this attribute in the result.
     * @param xacmlVersion xacml version
     */
    public Attribute(URI id, URI type, String issuer, DateTimeAttribute issueInstant,
            List<AttributeValue> attributeValues, boolean includeInResult, int xacmlVersion) {
        this.id = id;
        this.type = type;
        this.issuer = issuer;
        this.issueInstant = issueInstant;
        this.attributeValues = attributeValues;
        this.includeInResult = includeInResult;
        this.xacmlVersion = xacmlVersion;
    }


    /**
     * Creates an instance of an <code>Attribute</code> based on the root DOM node of the XML data.
     *
     * @param root the DOM root of the AttributeType XML type
     * @param version XACML version
     * @return the attribute
     * @throws ParsingException throws ParsingException if the data is invalid
     */
    public static Attribute getInstance(Node root, int version) throws ParsingException {
        URI id = null;
        URI type = null;
        String issuer = null;
        DateTimeAttribute issueInstant = null;
        List<AttributeValue> values = new ArrayList<AttributeValue>();
        boolean includeInResult = false ;


        AttributeFactory attributeFactory =  Balana.getInstance().getAttributeFactory();

        // First check that we're really parsing an Attribute
        if (!DOMHelper.getLocalName(root).equals("Attribute")) {
            throw new ParsingException("Attribute object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NamedNodeMap attrs = root.getAttributes();

        try {
            id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "AttributeId in AttributeType", e);
        }

        if(!(version == XACMLConstants.XACML_VERSION_3_0)){
            try {
                type = new URI(attrs.getNamedItem("DataType").getNodeValue());
            } catch (Exception e) {
                throw new ParsingException("Error parsing required attribute "
                        + "DataType in AttributeType", e);
            }
        }

        if(version == XACMLConstants.XACML_VERSION_3_0){
            try {
                String includeInResultString = attrs.getNamedItem("IncludeInResult").getNodeValue();
                if("true".equals(includeInResultString)){
                    includeInResult = true;
                }
            } catch (Exception e) {
                throw new ParsingException("Error parsing required attribute "
                        + "IncludeInResult in AttributeType", e);
            }            
        }

        try {
            Node issuerNode = attrs.getNamedItem("Issuer");
            if (issuerNode != null)
                issuer = issuerNode.getNodeValue();
            if(!(version == XACMLConstants.XACML_VERSION_3_0)){
                Node instantNode = attrs.getNamedItem("IssueInstant");
                if (instantNode != null){
                    issueInstant = DateTimeAttribute.getInstance(instantNode.getNodeValue());
                }
            }
        } catch (Exception e) {
            // shouldn't happen, but just in case...
            throw new ParsingException("Error parsing optional AttributeType" + " attribute", e);
        }

        // now we get the attribute value
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("AttributeValue")) {
                if(version == XACMLConstants.XACML_VERSION_3_0){
                    NamedNodeMap dataTypeAttribute = node.getAttributes();
                    try {
                        type = new URI(dataTypeAttribute.getNamedItem("DataType").getNodeValue());
                    } catch (Exception e) {
                        throw new ParsingException("Error parsing required attribute "
                                + "DataType in AttributeType", e);
                    }
                }

                try {                    
                    values.add(attributeFactory.createValue(node, type));
                } catch (UnknownIdentifierException uie) {
                    throw new ParsingException(uie.getMessage(), uie);
                }
            }
        }

        // make sure we got a value
        if (values.size() < 1){
            throw new ParsingException("Attribute must contain a value");
        }
        return new Attribute(id, type, issuer, issueInstant, values, includeInResult, version);
    }

    /**
     * Returns the id of this attribute
     * 
     * @return the attribute id
     */
    public URI getId() {
        return id;
    }

    /**
     * Returns the data type of this attribute
     * 
     * @return the attribute's data type
     */
    public URI getType() {
        return type;
    }

    /**
     * Returns the issuer of this attribute, or null if no issuer was named
     * 
     * @return the issuer or null
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Returns the moment at which the attribute was issued, or null if no issue time was provided
     * 
     * @return the time of issuance or null
     */
    public DateTimeAttribute getIssueInstant() {
        return issueInstant;
    }

    /**
     * Returns whether attribute must be present in response or not
     *
     * @return  true/false
     */
    public boolean isIncludeInResult() {
        return includeInResult;
    }

    /**
     * <code>List</code> of <code>AttributeValue</code>  of this attribute,
     * or null if no value was included
     * 
     * @return the attribute' s value or null
     */
    public List<AttributeValue> getValues() {
        return attributeValues;
    }


    /**
     * a <code>AttributeValue</code>  of this attribute,
     * or null if no value was included
     *
     * @return the attribute' s value or null
     */
    public AttributeValue getValue() {

        if(attributeValues != null){
            return attributeValues.get(0);
        }
        return null;
    }


    /**
     * Encodes this <code>Attribute</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }


    /**
     * Encodes this <code>Attribute</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<Attribute AttributeId=\"").append(id.toString()).append("\"");

        if((xacmlVersion == XACMLConstants.XACML_VERSION_3_0)){
            builder.append(" IncludeInResult=\"").append(includeInResult).append("\"");
        } else {
            builder.append(" DataType=\"").append(type.toString()).append("\"");
            if (issueInstant != null){
                builder.append(" IssueInstant=\"").append(issueInstant.encode()).append("\"");
            }
        }

        if (issuer != null) {
            builder.append(" Issuer=\"").append(issuer).append("\"");
        }

        builder.append(">\n");

        if(attributeValues != null && attributeValues.size() > 0){
            for(AttributeValue value : attributeValues){
                value.encode(builder);
            }
        }

        builder.append("</Attribute>\n");
    }

}
