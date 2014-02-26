/*
 * @(#)CombinerParameter.java
 *
 * Copyright 2005 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.combine;

import org.wso2.balana.Balana;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;

import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.attr.AttributeValue;

import java.io.OutputStream;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * Represents a single named parameter to a combining algorithm. Parameters are only used by XACML
 * 2.0 and later policies.
 * 
 * @since 2.0
 * @author Seth Proctor
 */
public class CombinerParameter {

    // the name of this parameter
    private String name;

    // the value of this parameter
    private AttributeValue value;

    /**
     * Creates a new CombinerParameter.
     * 
     * @param name the parameter's name
     * @param value the parameter's value
     */
    public CombinerParameter(String name, AttributeValue value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns a new instance of the <code>CombinerParameter</code> class based on a DOM node. The
     * node must be the root of an XML CombinerParameterType.
     * 
     * @param root the DOM root of a CombinerParameterType XML type
     * 
     * @throws ParsingException if the CombinerParameterType is invalid
     * @return an instance of <code>CombinerParameter</code>
     */
    public static CombinerParameter getInstance(Node root) throws ParsingException {
        // get the name, which is a required attribute
        String name = root.getAttributes().getNamedItem("ParameterName").getNodeValue();

        // get the attribute value, the only child of this element
        AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();
        AttributeValue value = null;

        try {
            value = attrFactory.createValue(root.getFirstChild());
        } catch (UnknownIdentifierException uie) {
            throw new ParsingException(uie.getMessage(), uie);
        }

        return new CombinerParameter(name, value);
    }

    /**
     * Returns the name of this parameter.
     * 
     * @return the name of this parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value provided by this parameter.
     * 
     * @return the value provided by this parameter
     */
    public AttributeValue getValue() {
        return value;
    }

    /**
     * Encodes this <code>CombinerParameter</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<CombinerParameter ParameterName=\"").append(getName()).append("\">\n");

        getValue().encode(builder);

        builder.append("</CombinerParameter>\n");
    }

}
