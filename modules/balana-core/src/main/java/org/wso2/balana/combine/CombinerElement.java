/*
 * @(#)CombinerElement.java
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

import org.wso2.balana.Indenter;
import org.wso2.balana.PolicyTreeElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.OutputStream;

/**
 * Represents one input (a Rule, Policy, PolicySet, or reference) to a combining algorithm and
 * combiner parameters associated with that input.
 * 
 * @since 2.0
 * @author Seth Proctor
 */
public abstract class CombinerElement {

    // the element to be combined
    private PolicyTreeElement element;

    // the parameters used with this element
    private List parameters;

    /**
     * Constructor that only takes an element. No parameters are associated with this element when
     * combining.
     * 
     * @param element a <code>PolicyTreeElement</code> to use in combining
     */
    public CombinerElement(PolicyTreeElement element) {
        this(element, null);
    }

    /**
     * Constructor that takes both the element to combine and its associated combiner parameters.
     * 
     * @param element a <code>PolicyTreeElement</code> to use in combining
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *            <code>CombinerParameter<code>s provided for general
     *                   use (for all pre-2.0 policies this must be empty)
     */
    public CombinerElement(PolicyTreeElement element, List parameters) {
        this.element = element;

        if (parameters == null)
            this.parameters = Collections.unmodifiableList(new ArrayList());
        else
            this.parameters = Collections.unmodifiableList(new ArrayList(parameters));
    }

    /**
     * Returns the <code>PolicyTreeElement</code> in this element.
     * 
     * @return the <code>PolicyTreeElement</code>
     */
    public PolicyTreeElement getElement() {
        return element;
    }

    /**
     * Returns the <code>CombinerParameter</code>s associated with this element.
     * 
     * @return a <code>List</code> of <code>CombinerParameter</code>s
     */
    public List getParameters() {
        return parameters;
    }

    /**
     * Encodes this <code>CombinerElement</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public abstract void encode(StringBuilder builder);

}
