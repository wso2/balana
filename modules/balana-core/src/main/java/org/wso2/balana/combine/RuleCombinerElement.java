/*
 * @(#)RuleCombinerElement.java
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
import org.wso2.balana.Rule;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Iterator;
import java.util.List;

/**
 * Specific version of <code>CombinerElement</code> used for rule combining.
 * 
 * @since 2.0
 * @author Seth Proctor
 */
public class RuleCombinerElement extends CombinerElement {

    /**
     * Constructor that only takes a <code>Rule</code. No parameters are associated with this
     * <code>Rule</code> when combining.
     * 
     * @param rule a <code>Rule</code> to use in combining
     */
    public RuleCombinerElement(Rule rule) {
        super(rule);
    }

    /**
     * Constructor that takes both the <code>Rule</code> to combine and its associated combiner
     * parameters.
     * 
     * @param rule a <code>Rule</code> to use in combining
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *            <code>CombinerParameter<code>s provided for general
     *                   use (for all pre-2.0 policies this must be empty)
     */
    public RuleCombinerElement(Rule rule, List parameters) {
        super(rule, parameters);
    }

    /**
     * Returns the <code>Rule</code> in this element.
     * 
     * @return the element's <code>Rule</code>
     */
    public Rule getRule() {
        return (Rule) (getElement());
    }

    /**
     * Encodes this <code>RuleCombinerElement</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {
        Iterator it = getParameters().iterator();

        if (it.hasNext()) {
            builder.append("<RuleCombinerParameters RuleIdRef=\"").append(getRule().getId()).append("\">\n");
            while (it.hasNext()) {
                CombinerParameter param = (CombinerParameter) (it.next());
                param.encode(builder);
            }

            builder.append("</RuleCombinerParameters>\n");
        }

        getRule().encode(builder);
    }

}
