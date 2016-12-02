/*
 * @(#)StringAttribute.java
 *
 * Copyright 2003-2005 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.attr;

import java.net.URI;

import org.w3c.dom.Node;

/**
 * Representation of an xs:string value. This class supports parsing xs:string values. All objects
 * of this class are immutable and all methods of the class are thread-safe.
 * <p>
 * Note that there was some confusion in the XACML specification about whether this datatype should
 * be able to handle XML elements (ie, whether &lt;AttributeValue
 * DataType="...string"&gt;&lt;foo/&gt; &lt;/AttributeValue&gt; is valid). This has been clarified
 * to provide the correct requirement that a string may not contain mixed content (ie, the example
 * provided here is invalid). If you need to specify something like this with the string datatype,
 * then you must escape the <code>&lt;</code> and <code>&gt;</code> characters.
 *
 * @author Marco Barreno
 * @author Seth Proctor
 * @author Steve Hanna
 * @since 1.0
 */
public class StringAttribute extends AttributeValue {
    /**
     * Official name of this type
     */
    public static final String identifier = "http://www.w3.org/2001/XMLSchema#string";

    /**
     * URI version of name for this type
     * <p>
     * This field is initialized by a static initializer so that we can catch any exceptions thrown
     * by URI(String) and transform them into a RuntimeException, since this should never happen but
     * should be reported properly if it ever does.
     */
    private static URI identifierURI;

    /**
     * RuntimeException that wraps an Exception thrown during the creation of identifierURI, null if
     * none.
     */
    private static RuntimeException earlyException;

    /**
     * Static initializer that initializes the identifierURI class field so that we can catch any
     * exceptions thrown by URI(String) and transform them into a RuntimeException. Such exceptions
     * should never happen but should be reported properly if they ever do.
     */
    static {
        try {
            identifierURI = new URI(identifier);
        } catch (Exception e) {
            earlyException = new IllegalArgumentException();
            earlyException.initCause(e);
        }
    }

    ;

    /**
     * The actual String value that this object represents.
     */
    private String value;

    /**
     * Creates a new <code>StringAttribute</code> that represents the String value supplied.
     *
     * @param value the <code>String</code> value to be represented
     */
    public StringAttribute(String value) {
        super(identifierURI);

        // Shouldn't happen, but just in case...
        if (earlyException != null)
            throw earlyException;

        if (value == null)
            this.value = "";
        else
            this.value = value;
    }

    /**
     * Returns a new <code>StringAttribute</code> that represents the xs:string at a particular DOM
     * node.
     *
     * @param root the <code>Node</code> that contains the desired value
     * @return a new <code>StringAttribute</code> representing the appropriate value (null if there
     * is a parsing error)
     */
    public static StringAttribute getInstance(Node root) {
        Node node = root.getFirstChild();

        // Strings are allowed to have an empty AttributeValue element and are
        // just treated as empty strings...we have to handle this case
        if (node == null)
            return new StringAttribute("");

        // get the type of the node
        short type = node.getNodeType();

        // now see if we have (effectively) a simple string value
        if ((type == Node.TEXT_NODE) || (type == Node.CDATA_SECTION_NODE)
                || (type == Node.COMMENT_NODE)) {
            return getInstance(node.getNodeValue());
        }

        // there is some confusion in the specifications about what should
        // happen at this point, but the strict reading of the XMLSchema
        // specification suggests that this should be an error
        return null;
    }

    /**
     * Returns a new <code>StringAttribute</code> that represents the xs:string value indicated by
     * the <code>String</code> provided.
     *
     * @param value a string representing the desired value
     * @return a new <code>StringAttribute</code> representing the appropriate value
     */
    public static StringAttribute getInstance(String value) {
        return new StringAttribute(value);
    }

    /**
     * Returns the <code>String</code> value represented by this object.
     *
     * @return the <code>String</code> value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns true if the input is an instance of this class and if its value equals the value
     * contained in this class.
     *
     * @param o the object to compare
     * @return true if this object and the input represent the same value
     */
    public boolean equals(Object o) {
        if (!(o instanceof StringAttribute))
            return false;

        StringAttribute other = (StringAttribute) o;

        return value.equals(other.value);
    }

    /**
     * Returns the hashcode value used to index and compare this object with others of the same
     * type. Typically this is the hashcode of the backing data object.
     *
     * @return the object's hashcode value
     */
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Converts to a String representation.
     *
     * @return the String representation
     */
    public String toString() {
        return "StringAttribute: \"" + value + "\"";
    }

    /**
     *
     */
    public String encode() {
        return value;
    }

}
