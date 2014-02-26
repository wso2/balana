/*
 * @(#)ResponseCtx.java
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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.ctx.xacml2.Result;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents the response to a request made to the XACML PDP.
 * 
 * @since 1.0
 * @author Seth Proctor
 * @author Marco Barreno
 */
public class ResponseCtx {

    // The set of Result objects returned by the PDP
    private Set<AbstractResult> results = new HashSet<AbstractResult>();

    // XACML version
    private int version;

    /**
     * Constructor that creates a new <code>ResponseCtx</code> with only a single
     * <code>Result</code> (a common case).
     * 
     * @param result the single result in the response
     */
    public ResponseCtx(AbstractResult result) {
        this.version = result.getVersion();
        results.add(result);
    }

    /**
     * Constructor that creates a new <code>ResponseCtx</code> with a <code>Set</code> of
     * <code>Result</code>s. The <code>Set</code> must be non-empty.
     * 
     * @param results a <code>Set</code> of <code>Result</code> objects
     * @param version XACML version
     */
    public ResponseCtx(Set<AbstractResult> results, int version) {
        this.version = version;
        this.results = Collections.unmodifiableSet(new HashSet<AbstractResult>(results));
    }

    /**
     * Creates a new instance of <code>ResponseCtx</code> based on the given
     * DOM root node. A <code>ParsingException</code> is thrown if the DOM
     * root doesn't represent a valid ResponseType.
     *
     * @param root the DOM root of a ResponseType
     * @return a new <code>ResponseCtx</code>
     * @throws ParsingException if the node is invalid
     */
    public static ResponseCtx getInstance(Node root) throws ParsingException {
        String requestCtxNs = root.getNamespaceURI();

        if(requestCtxNs != null){
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(requestCtxNs.trim())){
                return getInstance(root, XACMLConstants.XACML_VERSION_3_0);
            } else if(XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(requestCtxNs.trim()) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(requestCtxNs.trim())) {
                return getInstance(root, XACMLConstants.XACML_VERSION_2_0);
            } else {
                throw new ParsingException("Invalid namespace in XACML response");
            }
        } else {
            //No Namespace defined in XACML request and Assume as XACML 3.0
            return getInstance(root, XACMLConstants.XACML_VERSION_3_0);
        }
    }

    /**
     * Creates a new instance of <code>ResponseCtx</code> based on the given
     * DOM root node. A <code>ParsingException</code> is thrown if the DOM
     * root doesn't represent a valid ResponseType.
     *
     * @param root the DOM root of a ResponseType
     * @param version XACML version
     * @return a new <code>ResponseCtx</code>
     * @throws ParsingException if the node is invalid
     */
    public static ResponseCtx getInstance(Node root, int version) throws ParsingException {
        Set<AbstractResult> results = new HashSet<AbstractResult>();

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("Result")) {
                if(version == XACMLConstants.XACML_VERSION_3_0){
                    results.add(org.wso2.balana.ctx.xacml3.Result.getInstance(node));
                } else {
                    results.add(Result.getInstance(node));
                }
            }
        }

        if (results.size() == 0){
            throw new ParsingException("must have at least one Result");
        }
        return new ResponseCtx(results, version);
    }    

    /**
     * Get the set of <code>Result</code>s from this response.
     * 
     * @return a <code>Set</code> of results
     */
    public Set<AbstractResult> getResults() {
        return results;
    }


    /**
     * Encodes this <code>ResponseCtx</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>ResponseCtx</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<Response");

        if(version == XACMLConstants.XACML_VERSION_3_0){
            builder.append(" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"");
        }

        builder.append(">");

        // Go through all results
        Iterator it = results.iterator();
        while (it.hasNext()) {
            AbstractResult result = (AbstractResult) (it.next());
            result.encode(builder);
        }
        // Finish the XML for a response
        builder.append("</Response>");

    }

}
