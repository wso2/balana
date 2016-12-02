/*
 * @(#)Status.java
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

import org.wso2.balana.DOMHelper;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the status data that is included in a ResultType. By default, the status is OK.
 *
 * @author Seth Proctor
 * @since 1.0
 */
public class Status {

    /**
     * Standard identifier for the OK status
     */
    public static final String STATUS_OK = "urn:oasis:names:tc:xacml:1.0:status:ok";

    /**
     * Standard identifier for the MissingAttribute status
     */
    public static final String STATUS_MISSING_ATTRIBUTE = "urn:oasis:names:tc:xacml:1.0:status:missing-attribute";

    /**
     * Standard identifier for the SyntaxError status
     */
    public static final String STATUS_SYNTAX_ERROR = "urn:oasis:names:tc:xacml:1.0:status:syntax-error";

    /**
     * Standard identifier for the ProcessingError status
     */
    public static final String STATUS_PROCESSING_ERROR = "urn:oasis:names:tc:xacml:1.0:status:processing-error";
    // a single OK object we'll use most of the time
    private static Status okStatus;

    // initialize the OK Status object
    static {
        List<String> code = new ArrayList<String>();
        code.add(STATUS_OK);
        okStatus = new Status(code);
    }

    // the status code
    private List<String> code;
    // the message
    private String message;
    // the detail
    private StatusDetail detail;

    ;

    /**
     * Constructor that takes only the status code.
     *
     * @param code a <code>List</code> of <code>String</code> codes, typically just one code, but
     *             this may contain any number of minor codes after the first item in the list, which
     *             is the major code
     */
    public Status(List<String> code) {
        this(code, null, null);
    }

    /**
     * Constructor that takes both the status code and a message to include with the status.
     *
     * @param code    a <code>List</code> of <code>String</code> codes, typically just one code, but
     *                this may contain any number of minor codes after the first item in the list, which
     *                is the major code
     * @param message a message to include with the code
     */
    public Status(List code, String message) {
        this(code, message, null);
    }

    /**
     * Constructor that takes the status code, an optional message, and some detail to include with
     * the status. Note that the specification explicitly says that a status code of OK, SyntaxError
     * or ProcessingError may not appear with status detail, so an exception is thrown if one of
     * these status codes is used and detail is included.
     *
     * @param code    a <code>List</code> of <code>String</code> codes, typically just one code, but
     *                this may contain any number of minor codes after the first item in the list, which
     *                is the major code
     * @param message a message to include with the code, or null if there should be no message
     * @param detail  the status detail to include, or null if there is no detail
     * @throws IllegalArgumentException if detail is included for a status code that doesn't allow
     *                                  detail
     */
    public Status(List<String> code, String message, StatusDetail detail) throws IllegalArgumentException {
        // if the code is ok, syntax error or processing error, there
        // must not be any detail included
        if (detail != null) {
            String c = (String) (code.iterator().next());
            if (c.equals(STATUS_OK) || c.equals(STATUS_SYNTAX_ERROR)
                    || c.equals(STATUS_PROCESSING_ERROR))
                throw new IllegalArgumentException("status detail cannot be " + "included with "
                        + c);
        }

        this.code = Collections.unmodifiableList(new ArrayList<String>(code));
        this.message = message;
        this.detail = detail;
    }

    /**
     * Gets a <code>Status</code> instance that has the OK status and no other information. This is
     * the default status data for all responses except Indeterminate ones.
     *
     * @return an instance with <code>STATUS_OK</code>
     */
    public static Status getOkInstance() {
        return okStatus;
    }

    /**
     * Creates a new instance of <code>Status</code> based on the given DOM root node. A
     * <code>ParsingException</code> is thrown if the DOM root doesn't represent a valid StatusType.
     *
     * @param root the DOM root of a StatusType
     * @return a new <code>Status</code>
     * @throws ParsingException if the node is invalid
     */
    public static Status getInstance(Node root) throws ParsingException {

        List<String> code = null;
        String message = null;
        StatusDetail detail = null;

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String name = DOMHelper.getLocalName(node);

            if (name.equals("StatusCode")) {
                code = parseStatusCode(node);
            } else if (name.equals("StatusMessage")) {
                message = node.getFirstChild().getNodeValue();
            } else if (name.equals("StatusDetail")) {
                detail = StatusDetail.getInstance(node);
            }
        }

        if (code == null) {
            throw new ParsingException("Missing required element StatusCode in StatusType");
        }

        return new Status(code, message, detail);
    }

    /**
     * Private helper that parses the status code
     *
     * @param root the DOM root of a StatusCodeType
     * @return a List for status
     */
    private static List<String> parseStatusCode(Node root) {
        // get the top-level code
        String val = root.getAttributes().getNamedItem("Value").getNodeValue();
        List<String> code = new ArrayList<String>();
        code.add(val);

        // now get the list of all sub-codes, and work through them
        NodeList list = ((Element) root).getElementsByTagName("StatusCode");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            code.add(node.getAttributes().getNamedItem("Value").getNodeValue());
        }

        return code;
    }

    /**
     * Returns the status code.
     *
     * @return the status code
     */
    public List<String> getCode() {
        return code;
    }

    /**
     * Returns the status message or null if there is none.
     *
     * @return the status message or null
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the status detail or null if there is none.
     *
     * @return a <code>StatusDetail</code> or null
     */
    public StatusDetail getDetail() {
        return detail;
    }

    /**
     * Encodes this <code>Status</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>Status</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<Status>");

        encodeStatusCode(code.iterator(), builder);

        if (message != null) {
            builder.append("<StatusMessage>").append(message).append("</StatusMessage>");
        }

        if (detail != null) {
            builder.append(detail.getEncoded());
        }
        builder.append("</Status>");
    }

    /**
     * Encodes the object in XML
     *
     * @param iterator
     * @param builder
     */
    private void encodeStatusCode(Iterator iterator, StringBuilder builder) {

        String code = (String) (iterator.next());

        if (iterator.hasNext()) {
            builder.append("<StatusCode Value=\"").append(code).append("\">");
            encodeStatusCode(iterator, builder);
            builder.append("</StatusCode>");
        } else {
            builder.append("<StatusCode Value=\"").append(code).append("\"/>");
        }
    }

}
