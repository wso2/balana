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
package org.wso2.balana.ctx;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents the MissingAttributeDetailType in context schema. this contains the  information
 * about attributes required for policy evaluation that were missing from the request context.
 */
public class MissingAttributeDetail {

    /**
     * attribute identifier
     */
    private URI id;

    /**
     * attribute data type,  this is not used in XACML3
     */
    private URI type;

    /**
     * category of the Attributes element whether it is subject, action and etc
     */
    private URI category;    

    /**
     * issuer of the attribute.   optional one
     */
    private String issuer = null;

    /**
     * a <code>List</code> of <code>AttributeValue</code>
     */
    private List<AttributeValue> attributeValues;

    /**
     * XACML version
     */
    private int xacmlVersion;

    /**
     * Creates a new <code>MissingAttributeDetail</code>
     *
     * @param id the id of the attribute
     * @param type the type of the attribute
     * @param category category of the attributes elements whether it is subject, action and etc
     * @param issuer the attribute's issuer or null if there is none
     * @param attributeValues actual <code>List</code> of <code>AttributeValue</code>
     * @param xacmlVersion xacml version
     */
    public MissingAttributeDetail(URI id, URI type, URI category, String issuer,
                                  List<AttributeValue> attributeValues, int xacmlVersion) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.issuer = issuer;
        this.attributeValues = attributeValues;
        this.xacmlVersion = xacmlVersion;
    }

    /**
     * Creates a new <code>MissingAttributeDetail</code>
     *
     * @param id the id of the attribute
     * @param type the type of the attribute
     * @param category category of the attributes elements whether it is subject, action and etc
     * @param attributeValues  actual <code>List</code> of <code>AttributeValue</code>
     * @param xacmlVersion xacml version
     */
    public MissingAttributeDetail(URI id, URI type, URI category,
                                   List<AttributeValue> attributeValues, int xacmlVersion) {
        this(id, type, category, null, attributeValues, xacmlVersion);

    }

    /**
     * Creates a new <code>MissingAttributeDetail</code>
     *
     * @param id the id of the attribute
     * @param type the type of the attribute
     * @param category category of the attributes elements whether it is subject, action and etc
     * @param xacmlVersion xacml version
     */
    public MissingAttributeDetail(URI id, URI type, URI category, int xacmlVersion) {
        this(id, type, category, null, null, xacmlVersion);
    }
    
    /**
     * Creates an instance of an <code>MissingAttributeDetail</code> based on the root
     * DOM node of the XML data.
     *
     * @param root the DOM root of the AttributeType XML type
     * @param metaData policy meta data
     * @return  a <code>MissingAttributeDetail</code>  object       
     * @throws ParsingException throws ParsingException if the data is invalid
     */
    public static MissingAttributeDetail getInstance(Node root, PolicyMetaData metaData)
                                                                        throws ParsingException {
        URI id = null;
        URI type = null;
        URI category = null;
        String issuer = null;
        List<AttributeValue> values = new ArrayList<AttributeValue>();
        int version  = metaData.getXACMLVersion();

        AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();

        // First check that we're really parsing an Attribute
        if (!DOMHelper.getLocalName(root).equals("MissingAttributeDetail")) {
            throw new ParsingException("MissingAttributeDetailType object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NamedNodeMap attrs = root.getAttributes();

        try {
            id = new URI(attrs.getNamedItem("AttributeId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "AttributeId in MissingAttributeDetailType", e);
        }


        try {
            type = new URI(attrs.getNamedItem("DataType").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "DataType in MissingAttributeDetailType", e);
        }


        if(version == XACMLConstants.XACML_VERSION_3_0){
            try {
                category =  new URI(attrs.getNamedItem("IncludeInResult").getNodeValue());
            } catch (Exception e) {
                throw new ParsingException("Error parsing required attribute "
                        + "Category in MissingAttributeDetailType", e);
            }
        }

        try {
            Node issuerNode = attrs.getNamedItem("Issuer");
            if (issuerNode != null){
                issuer = issuerNode.getNodeValue();
            }
        } catch (Exception e) {
            // shouldn't happen, but just in case...
            throw new ParsingException("Error parsing optional attributes in MissingAttributeDetailType", e);
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
                                + "DataType in MissingAttributeDetailType", e);
                    }
                }

                try {
                    values.add(attrFactory.createValue(node, type));
                } catch (UnknownIdentifierException uie) {
                    throw new ParsingException("Unknown AttributeValue", uie);
                }
            }
        }

        return new MissingAttributeDetail(id, type, category, issuer, values, version);
    }

    /**
     * Returns the encoded String from MissingAttributeDetail
     *
     * @return String
     * @throws ParsingException if there are any issues, when parsing object in to Sting 
     */
    public String getEncoded() throws ParsingException {

        if(id == null){
            throw new ParsingException("Required AttributeId attribute is Null");
        }

        if(type == null){
            throw new ParsingException("Required DataType attribute is Null");
        }

        if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0 && category == null){
            throw new ParsingException("Required Category attribute is Null");
        }
        
        String encoded = "<MissingAttributeDetail AttributeId=\"" + id + "\" DataType=\"" + type + "\"";

        if(xacmlVersion == XACMLConstants.XACML_VERSION_3_0){
            encoded += " Category=\"" + category + "\"";
        }

        if(issuer != null){
            encoded += " Issuer=\"" + issuer + "\"";
        }

        encoded += " >";

        if(attributeValues != null && attributeValues.size() > 0){
            for(AttributeValue value : attributeValues){
                encoded += (value.encodeWithTags(true) + "\n");
            }
        }

        encoded += "</MissingAttributeDetail>";

        return encoded;
    }

}
