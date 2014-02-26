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
import org.wso2.balana.DOMHelper;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;

/**
 * Represents AttributeAssignmentType in the XACML 3.0 and 2.0 policy schema
 * This is used for including arguments in obligations and advices 
 */
public class AttributeAssignment extends AttributeValue {

    /**
     * attribute id of the AttributeAssignment  element
     */
    private URI attributeId;

    /**
     * category of the AttributeAssignment  element whether it is subject, action and etc
     */
    private URI category;

    /**
     *  issuer of the AttributeAssignment
     */
    private String issuer;

    /**
     * content as String
     */
    private String content;

    /**
     * Constructor that creates a new <code>AttributeAssignment</code> based on the given elements.
     * @param attributeId   attribute id of the AttributeAssignment  element
     * @param dataType attributes datatype
     * @param category category of the AttributeAssignment  element whether it is subject, action and etc
     * @param content Content as String
     * @param issuer issuer of the AttributeAssignment
     */
    public AttributeAssignment(URI attributeId, URI dataType, URI category, String content,
                                                                                String issuer) {
        super(dataType);
        this.attributeId = attributeId;
        this.category = category;
        this.issuer = issuer;
        this.content = content;
    }


    /**
     * TODO   remove this method if possible
     * creates a <code>AttributeAssignment</code> based on its DOM node.
     *
     * @param root root the node to parse for the AttributeAssignment
     * @return a new <code>AttributeAssignment</code> constructed by parsing
     * @throws ParsingException if the DOM node is invalid
     */
    public static AttributeAssignment getInstance(Node root) throws ParsingException {

        URI attributeId;
        URI category = null;
        URI type;
        String issuer = null;
        String content = null;

        if (!DOMHelper.getLocalName(root).equals("AttributeAssignment")) {
            throw new ParsingException("AttributeAssignment object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NamedNodeMap nodeAttributes = root.getAttributes();

        try {
            attributeId = new URI(nodeAttributes.getNamedItem("AttributeId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required AttributeId in " +
                    "AttributeAssignmentType", e);
        }

        try {
            type = new URI(nodeAttributes.getNamedItem("DataType").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required AttributeId in " +
                    "AttributeAssignmentType", e);
        }

        try {
            Node categoryNode = nodeAttributes.getNamedItem("Category");
            if(categoryNode != null){
                category = new URI(categoryNode.getNodeValue());
            }

            Node issuerNode = nodeAttributes.getNamedItem("Issuer");
            if(issuerNode != null){
                issuer = issuerNode.getNodeValue();
            }
            content = root.getTextContent();
        } catch (Exception e) {
            throw new ParsingException("Error parsing optional attributes in " +
                    "AttributeAssignmentType", e);
        }

        return new AttributeAssignment(attributeId, type, category, content, issuer);
    }

    public URI getAttributeId() {
        return attributeId;
    }

    public URI getCategory() {
        return category;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getContent() {
        return content;
    }

    /**
     * Encodes this <code>AttributeAssignment</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<AttributeAssignment  AttributeId=\"").append(attributeId).append("\"");

        builder.append(" DataType=\"").append(getType()).append("\"");

        if(category != null){
            builder.append(" Category=\"").append(category).append("\"");
        }

        if(issuer != null){
            builder.append("\" Issuer=\"").append(issuer).append("\"");
        }

        builder.append(">\n");

        if(content != null){
            builder.append(content);
        }

        builder.append("</AttributeAssignment>\n");
    }

    @Override
    /**
     * Encodes this <code>AttributeAssignment</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }
}
