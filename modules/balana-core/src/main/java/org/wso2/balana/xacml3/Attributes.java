/*
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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the AttributesType XML type found in the context schema.
 * TODO But here, not supporting the xml:id  Identifier. Just use it as String  attributes
 */
public class Attributes {

    /**
     * category of the Attributes element whether it is subject, action and etc
     */
    private URI category;

    /**
     *  content of the Attributes element that can be a XML data
     */
    private Node content;

    /**
     *  a <code>Set</code> of <code>Attribute</code> that contains in <code>Attributes</code> 
     */
    private Set<Attribute> attributes;

    /**
     * id of the Attribute element
     */
    private String id;

    /**
     * Constructor that creates a new <code>Attributes</code> based on
     * the given elements.
     * @param category category of the Attributes element whether it is subject, action and etc
     * @param attributes  a <code>Set</code> of <code>Attribute</code>
     * that contains in <code>Attributes</code>
     */
    public Attributes(URI category,Set<Attribute> attributes) {
        this(category, null, attributes, null);
    }

    /**
     * Constructor that creates a new <code>Attributes</code> based on
     * the given elements.
     * @param category category of the Attributes element whether it is subject, action and etc
     * @param content content of the Attributes element that can be a XML data
     * @param attributes  a <code>Set</code> of <code>Attribute</code>
     * that contains in <code>Attributes</code> 
     * @param id   id of the Attribute element
     */
    public Attributes(URI category, Node content, Set<Attribute> attributes, String id) {
        this.category = category;
        this.content = content;
        this.attributes = attributes;
        this.id = id;
    }

    /**
     *
     * @param root
     * @return
     * @throws ParsingException
     */
    public static Attributes getInstance(Node root) throws ParsingException {
        URI category ;
        Node content = null;
        String id = null;
        Set<Attribute> attributes = new HashSet<Attribute>();

        // First check that we're really parsing an Attribute
        if (!DOMHelper.getLocalName(root).equals(XACMLConstants.ATTRIBUTES_ELEMENT)) {
            throw new ParsingException("Attributes object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NamedNodeMap attrs = root.getAttributes();

        try {
            category = new URI(attrs.getNamedItem(XACMLConstants.ATTRIBUTES_CATEGORY).getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "AttributeId in AttributesType", e);
        }

        try {
            Node idNode = attrs.getNamedItem(XACMLConstants.ATTRIBUTES_ID);
            if(idNode != null){
                id = idNode.getNodeValue();
            }
        } catch (Exception e) {
            throw new ParsingException("Error parsing optional attributes in " +
                            "AttributesType", e);
        }

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals(XACMLConstants.ATTRIBUTES_CONTENT)) {
                // only one value can be in an Attribute
                if (content != null){
                    throw new ParsingException("Too many content elements are defined.");
                }
                // now get the value
                content = node.getFirstChild();
            } else if(DOMHelper.getLocalName(node).equals(XACMLConstants.ATTRIBUTE_ELEMENT)) {
                attributes.add(Attribute.getInstance(node, XACMLConstants.XACML_VERSION_3_0));
            }
        }

        if(content != null){
            // make the node appear to be a direct child of the Document
            try{
                DocumentBuilderFactory dbf = Utils.getSecuredDocumentBuilderFactory();
                DocumentBuilder builder = dbf.newDocumentBuilder();
                dbf.setNamespaceAware(true);
                Document docRoot = builder.newDocument();
                Node topRoot = docRoot.importNode(content, true);
                docRoot.appendChild(topRoot);
                content = docRoot.getDocumentElement();
            } catch (Exception e){
                //
            }
        }

        return new Attributes(category, content, attributes , id);
    }

    /**
     * Returns the category of this attributes
     *
     * @return the attribute 's category as <code>URI</code>
     */
    public URI getCategory() {
        return category;
    }

    /**
     * Returns the content of this attributes, or null if no content was included
     *
     * @return the attribute 's content as <code>Node</code> or null
     */
    public Node getContent() {
        return content;
    }

    /**
     * Returns list of attribute that contains in the attributes element
     *
     * @return  list of <code>Attribute</code>
     */
    public Set<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns the id of this attributes, or null if it was not included
     *
     * @return  the attribute 's id as <code>String</code> or null 
     */
    public String getId() {
        return id;
    }

    /**
     * Encodes this <code>Attributes</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>Attributes</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<Attributes Category=\"").append(category.toString()).append("\">");

        for(Attribute attribute : attributes){
            attribute.encode(builder);
        }
        if (content != null) {
        // TODO
        }

        builder.append("</Attributes>");
    }

//    /**
//     * Encodes only the included attribute into its XML representation and writes this encoding to the given
//     * <code>OutputStream</code> with indentation.
//     *
//     * @param output a stream into which the XML-encoded data is written
//     * @param indenter an object that creates indentation strings
//     */
//    public void encodeIncludeAttribute(OutputStream output, Indenter indenter) {
//
//        String indent = indenter.makeString();
//        PrintStream out = new PrintStream(output);
//        boolean atLestOneAttribute = false;
//
//        for(Attribute attribute : attributes){
//            if(attribute.isIncludeInResult()){
//                if(!atLestOneAttribute){
//                    // if there is one included attribute, encode the attributes element
//                    out.println(indent + "<Attributes Category=\"" + category.toString() + "\">");
//                    indenter.in();
//                }
//                atLestOneAttribute = true;
//                attribute.encode(output, indenter);
//            }
//        }
//
//        if(atLestOneAttribute){
//            indenter.out();
//            indenter.in();
//            if (content != null) {
//            // TODO
//            }
//
//            out.println(indent + "</Attributes>");
//        }
//    }



//    /**
//     * Encodes this attribute into its XML representation and writes this encoding to the given
//     * <code>OutputStream</code> with indentation.
//     *
//     * @param output a stream into which the XML-encoded data is written
//     * @param indenter an object that creates indentation strings
//     */
//    public void encodeWithIncludedAttributes(OutputStream output, Indenter indenter) {
//        // setup the formatting & outstream stuff
//        String indent = indenter.makeString();
//        PrintStream out = new PrintStream(output);
//
//        out.println(indent + "<Attributes Category=\"" + category.toString() + "\">");
//
//        indenter.in();
//
//        for(Attribute attribute : attributes){
//            if(attribute.isIncludeInResult()){
//                attribute.encode(output, indenter);
//            }
//        }
//
//        indenter.out();
//
//        indenter.in();
//        if (content != null) {
//        // TODO
//        }
//
//        out.println(indent + "</Attributes>");
//    }

}
