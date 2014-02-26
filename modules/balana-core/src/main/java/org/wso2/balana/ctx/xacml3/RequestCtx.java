/*
 * @(#)RequestCtx.java
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

package org.wso2.balana.ctx.xacml3;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.ctx.*;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.xacml3.MultiRequests;
import org.wso2.balana.xacml3.RequestDefaults;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a XACML3 request made to the PDP. This is the class that contains all the data used to start
 * a policy evaluation.
 */
public class RequestCtx extends AbstractRequestCtx {

    /**
     * define boolean value whether to send back the applicable policies to PEP or not
     */
    private boolean returnPolicyIdList;

    /**
     * uses for when multiple decisions is enabled in PDP. This is defined whether to combine
     * multiple decisions or not
     */
    private boolean combinedDecision;

    /**
     * lists multiple request contexts by references to the <Attributes> elements
     */
    private MultiRequests multiRequests;

    /**
     * contains default values for the request, such as XPath version.
     */
    private RequestDefaults defaults;

    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param attributesSet a <code>Set</code> of <code>Attributes</code>s
     * @param documentRoot  the root node of the DOM tree for this request 
     * @throws IllegalArgumentException if the inputs are not well formed
     */
    public RequestCtx(Set<Attributes> attributesSet, Node documentRoot) {
        this(documentRoot, attributesSet, false, false, null, null);
    }

    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param documentRoot       the root node of the DOM tree for this request
     * @param attributesSet      a <code>Set</code> of <code>Attributes</code>s
     * @param returnPolicyIdList a <code>boolean</code> value whether to send back policy list of not
     * @param combinedDecision   a <code>boolean</code> value whether to combine decisions or not
     * @param multiRequests      a <code>MultiRequests</code> for the  MultiRequests element in request
     * @param defaults           a <code>RequestDefaults</code>  for the  RequestDefaults element in request
     * @throws IllegalArgumentException if the inputs are not well formed
     */
    public RequestCtx(Node documentRoot, Set<Attributes> attributesSet, boolean returnPolicyIdList,
                      boolean combinedDecision, MultiRequests multiRequests,
                      RequestDefaults defaults) throws IllegalArgumentException {


        this.xacmlVersion = XACMLConstants.XACML_VERSION_3_0;
        this.documentRoot = documentRoot;
        this.attributesSet = attributesSet;
        this.returnPolicyIdList = returnPolicyIdList;
        this.combinedDecision = combinedDecision;
        this.multiRequests = multiRequests;
        this.defaults = defaults;
    }

    /**
     * Create a new <code>RequestCtx</code> by parsing a node. This node should be created by
     * schema-verified parsing of an <code>XML</code> document.
     *
     * @param root the node to parse for the <code>RequestCtx</code>
     * @return a new <code>RequestCtx</code> constructed by parsing
     * @throws org.wso2.balana.ParsingException
     *          if the DOM node is invalid
     */
    public static RequestCtx getInstance(Node root) throws ParsingException {

        Set<Attributes> attributesElements;
        boolean returnPolicyIdList = false;
        boolean combinedDecision = false;
        MultiRequests multiRequests = null;
        RequestDefaults defaults = null;

        // First check to be sure the node passed is indeed a Request node.
        String tagName = DOMHelper.getLocalName(root);
        if (!tagName.equals("Request")) {
            throw new ParsingException("Request cannot be constructed using " + "type: "
                    + DOMHelper.getLocalName(root));
        }

        NamedNodeMap attrs = root.getAttributes();
        try {
            String attributeValue = attrs.getNamedItem(XACMLConstants.RETURN_POLICY_LIST).
                    getNodeValue();
            if ("true".equals(attributeValue)) {
                returnPolicyIdList = true;
            }
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "ReturnPolicyIdList in RequestType", e);
        }

        try {
            String attributeValue = attrs.getNamedItem(XACMLConstants.COMBINE_DECISION).
                    getNodeValue();
            if ("true".equals(attributeValue)) {
                combinedDecision = true;
            }
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "CombinedDecision in RequestType", e);
        }

        attributesElements = new HashSet<Attributes>();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String tag = DOMHelper.getLocalName(node);
            if (tag.equals(XACMLConstants.ATTRIBUTES_ELEMENT)) {
                Attributes attributes = Attributes.getInstance(node);
                attributesElements.add(attributes);
            }

            if (tag.equals(XACMLConstants.MULTI_REQUESTS)) {
                if (multiRequests != null) {
                    throw new ParsingException("Too many MultiRequests elements are defined.");
                }
                multiRequests = MultiRequests.getInstance(node);
            }

            if (tag.equals(XACMLConstants.REQUEST_DEFAULTS)) {
                if (multiRequests != null) {
                    throw new ParsingException("Too many RequestDefaults elements are defined.");
                }
                defaults = RequestDefaults.getInstance(node);
            }
        }

        if (attributesElements.isEmpty()) {
            throw new ParsingException("Request must contain at least one AttributesType");
        }

        return new RequestCtx(root, attributesElements, returnPolicyIdList, combinedDecision,
                multiRequests, defaults);
    }

    /**
     * Returns a <code>boolean</code> value whether to combine decisions or not
     *
     * @return true of false
     */
    public boolean isCombinedDecision() {
        return combinedDecision;
    }

    /**
     * Returns a <code>boolean</code> value whether to send back policy list of not
     *
     * @return true or false
     */
    public boolean isReturnPolicyIdList() {
        return returnPolicyIdList;
    }

    /**
     * Returns a <code>MultiRequests</code> that encapsulates MultiRequests element in request
     *
     * @return MultiRequests element in request
     */
    public MultiRequests getMultiRequests() {
        return multiRequests;
    }

    /**
     * Returns a <code>RequestDefaults</code> that encapsulates RequestDefaults element in request
     *
     * @return RequestDefaults element in request
     */
    public RequestDefaults getDefaults() {
        return defaults;
    }

    /**
     * Encodes this  <code>AbstractRequestCtx</code>  into its XML representation and writes this encoding to the given
     * <code>OutputStream</code> with indentation.
     *
     * @param output a stream into which the XML-encoded data is written
     * @param indenter an object that creates indentation strings
     */
    public void encode(OutputStream output, Indenter indenter) {

        String indent = indenter.makeString();
        PrintStream out = new PrintStream(output);

        out.println(indent + "<Request xmlns=\"" + XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER +
                "\" ReturnPolicyIdList=\"" + returnPolicyIdList + "\" CombinedDecision=\"" + 
                combinedDecision +   "\" >");

        indenter.in();

        for(Attributes attributes : attributesSet){
            out.println(attributes.encode());
        }

        if(defaults != null){
            defaults.encode(output, indenter);
        }

        if(multiRequests != null){
           // multiRequests 
        }

        indenter.out();

        out.println(indent + "</Request>");
    }

    /**
     * Encodes this  <code>AbstractRequestCtx</code>  into its XML representation and writes this encoding to the given
     * <code>OutputStream</code>. No indentation is used.
     *
     * @param output a stream into which the XML-encoded data is written
     */
    public void encode(OutputStream output) {
        encode(output, new Indenter(0));
    }
}