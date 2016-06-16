/*
 * @(#)SelectorModule.java
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

package org.wso2.balana.finder.impl;


import org.w3c.dom.Document;
import org.wso2.balana.*;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;

import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.BagAttribute;

import org.wso2.balana.cond.EvaluationResult;

import org.wso2.balana.ctx.Status;

import org.wso2.balana.finder.AttributeFinderModule;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.utils.Utils;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

/**
 * This module implements the basic behavior of the AttributeSelectorType, looking for attribute
 * values in the physical request document using the given XPath expression. This is implemented as
 * a separate module (instead of being implemented directly in <code>AttributeSelector</code> so
 * that programmers can remove this functionality if they want (it's optional in the spec), so they
 * can replace this code with more efficient, specific code as needed, and so they can easily swap
 * in different XPath libraries.
 * <p>
 * Note that if no matches are found, this module will return an empty bag (unless some error
 * occurred). The <code>AttributeSelector</code> is still deciding what to return to the policy
 * based on the MustBePresent attribute.
 * <p>
 * This module uses the Xalan XPath implementation, and supports only version 1.0 of XPath. It is a
 * fully functional, correct implementation of XACML's AttributeSelector functionality, but is not
 * designed for environments that make significant use of XPath queries. Developers for any such
 * environment should consider implementing their own module.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class SelectorModule extends AttributeFinderModule {

    /**
     * Returns true since this module supports retrieving attributes based on the data provided in
     * an AttributeSelectorType.
     * 
     * @return true
     */
    public boolean isSelectorSupported() {
        return true;
    }


    @Override
    public EvaluationResult findAttribute(String contextPath, URI attributeType,
                  String contextSelector, Node root, EvaluationCtx context, String xpathVersion) {

        Node contextNode = null;
        NamespaceContext namespaceContext = null;

        if(root == null){
            // root == null means there is not content element defined with the attributes element
            // therefore complete request is evaluated.
            // get the DOM root of the request document
            contextNode = context.getRequestRoot();
        } else if(contextSelector != null) {
            // root != null  means content element is there.  we can find the context node by
            // evaluating the contextSelector

            // 1st assume context node as the root
            contextNode = root;

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            //see if the request root is in a namespace
            String namespace = null;
            if(contextNode != null){
                namespace = contextNode.getNamespaceURI();
            }
            // name spaces are used, so we need to lookup the correct
            // prefix to use in the search string
            NamedNodeMap namedNodeMap = contextNode.getAttributes();

            Map<String, String> nsMap = new HashMap<String, String>();

            for (int i = 0; i < namedNodeMap.getLength(); i++) {
                Node n = namedNodeMap.item(i);
                // we found the matching namespace, so get the prefix
                // and then break out
                String prefix = DOMHelper.getLocalName(n);
                String nodeValue= n.getNodeValue();
                nsMap.put(prefix, nodeValue);
            }

            // if there is not any namespace is defined for content element, default XACML request
            //  name space would be there.
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(namespace)){
                nsMap.put("xacml", namespace);
            }

            namespaceContext = new DefaultNamespaceContext(nsMap);
            xpath.setNamespaceContext(namespaceContext);

            try{
                XPathExpression expression = xpath.compile(contextSelector);
                NodeList result = (NodeList) expression.evaluate(contextNode, XPathConstants.NODESET);                
                if(result == null || result.getLength() == 0){
                    throw new Exception("No node is found from context selector id evaluation");    
                } else if(result.getLength() != 1){
                    throw new Exception("More than one node is found from context selector id evaluation");
                }
                contextNode = result.item(0);
                if(contextNode != null){
                    // make the node appear to be a direct child of the Document
                    try{
                        DocumentBuilderFactory dbf = Utils.getSecuredDocumentBuilderFactory();
                        DocumentBuilder builder = dbf.newDocumentBuilder();
                        dbf.setNamespaceAware(true);
                        Document docRoot = builder.newDocument();
                        Node topRoot = docRoot.importNode(contextNode, true);
                        docRoot.appendChild(topRoot);
                        contextNode = docRoot.getDocumentElement();
                    } catch (Exception e){
                        //
                    }
                }
            } catch (Exception e) {
                List<String> codes = new ArrayList<String>();
                codes.add(Status.STATUS_SYNTAX_ERROR);
                Status status = new Status(codes, e.getMessage());
                return new EvaluationResult(status);
            }
        } else {
            contextNode = root;
        }


        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        if(namespaceContext == null){

            //see if the request root is in a namespace
            String namespace = null;
            if(contextNode != null){
                namespace = contextNode.getNamespaceURI();
            }
            // name spaces are used, so we need to lookup the correct
            // prefix to use in the search string
            NamedNodeMap namedNodeMap = contextNode.getAttributes();

            Map<String, String> nsMap = new HashMap<String, String>();

            for (int i = 0; i < namedNodeMap.getLength(); i++) {
                Node n = namedNodeMap.item(i);
                // we found the matching namespace, so get the prefix
                // and then break out
                String prefix = DOMHelper.getLocalName(n);
                String nodeValue= n.getNodeValue();
                nsMap.put(prefix, nodeValue);
            }
            
            // if there is not any namespace is defined for content element, default XACML request
            //  name space would be there.
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(namespace)){
                nsMap.put("xacml", namespace);
            }

            namespaceContext = new DefaultNamespaceContext(nsMap);
        }

        xpath.setNamespaceContext(namespaceContext);

        NodeList matches;
        
        try {
            XPathExpression expression = xpath.compile(contextPath);
            matches = (NodeList) expression.evaluate(contextNode, XPathConstants.NODESET);
            if(matches == null || matches.getLength() < 1){
                throw new Exception("No node is found from xpath evaluation");                 
            }
        } catch (XPathExpressionException e) {
            List<String> codes = new ArrayList<String>();
            codes.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(codes, e.getMessage());
            return new EvaluationResult(status);
        } catch (Exception e) {
            List<String> codes = new ArrayList<String>();
            codes.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(codes, e.getMessage());
            return new EvaluationResult(status);
        }

        if (matches.getLength() == 0) {
            // we didn't find anything, so we return an empty bag
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
        }

        // there was at least one match, so try to generate the values
        try {
            ArrayList<AttributeValue> list = new ArrayList<AttributeValue>();
            AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();

            for (int i = 0; i < matches.getLength(); i++) {
                String text = null;
                Node node = matches.item(i);
                short nodeType = node.getNodeType();

                // see if this is straight text, or a node with data under
                // it and then get the values accordingly
                if ((nodeType == Node.CDATA_SECTION_NODE) || (nodeType == Node.COMMENT_NODE)
                        || (nodeType == Node.TEXT_NODE) || (nodeType == Node.ATTRIBUTE_NODE)) {
                    // there is no child to this node
                    text = node.getNodeValue();
                } else {
                    // the data is in a child node
                    text = node.getFirstChild().getNodeValue();
                }

                list.add(attrFactory.createValue(attributeType, text));
            }

            return new EvaluationResult(new BagAttribute(attributeType, list));
            
        } catch (ParsingException pe) {
            ArrayList<String> code = new ArrayList<String>();
            code.add(Status.STATUS_PROCESSING_ERROR);
            return new EvaluationResult(new Status(code, pe.getMessage()));
        } catch (UnknownIdentifierException uie) {
            ArrayList<String> code = new ArrayList<String>();
            code.add(Status.STATUS_PROCESSING_ERROR);
            return new EvaluationResult(new Status(code, "Unknown attribute type : " + attributeType));
        }
    }
}
