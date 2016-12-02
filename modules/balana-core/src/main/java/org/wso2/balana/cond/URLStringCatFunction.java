/*
 * @(#)URLStringCatFunction.java
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.cond;

import org.wso2.balana.ctx.EvaluationCtx;

import org.wso2.balana.attr.AnyURIAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;

import org.wso2.balana.ctx.Status;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the XACML 2.0 url-string-concatenate function.
 *
 * @author Seth Proctor
 * @since 2.0
 */
public class URLStringCatFunction extends FunctionBase {

    /**
     * Standard identifier for the url-string-concatenate function.
     */
    public static final String NAME_URI_STRING_CONCATENATE = FUNCTION_NS_2
            + "uri-string-concatenate";

    /**
     * Creates an instance of this function.
     */
    public URLStringCatFunction() {
        super(NAME_URI_STRING_CONCATENATE, 0, AnyURIAttribute.identifier, false);
    }

    /**
     * Checks the inputs of this function.
     *
     * @param inputs a <code>List></code> of <code>Evaluatable</code>s
     * @throws IllegalArgumentException if the inputs won't work
     */
    public void checkInputs(List inputs) throws IllegalArgumentException {
        // scan the list to make sure nothing returns a bag
        Iterator it = inputs.iterator();
        while (it.hasNext()) {
            if (((Expression) (it.next())).returnsBag())
                throw new IllegalArgumentException(NAME_URI_STRING_CONCATENATE
                        + " doesn't accept bags");
        }

        // nothing is a bag, so check using the no-bag method
        checkInputsNoBag(inputs);
    }

    /**
     * Checks the inputs of this function assuming no parameters are bags.
     *
     * @param inputs a <code>List></code> of <code>Evaluatable</code>s
     * @throws IllegalArgumentException if the inputs won't work
     */
    public void checkInputsNoBag(List inputs) throws IllegalArgumentException {
        // make sure it's long enough
        if (inputs.size() < 2)
            throw new IllegalArgumentException("not enough args to " + NAME_URI_STRING_CONCATENATE);

        // check that the parameters are of the correct types...
        Iterator it = inputs.iterator();

        // ...the first argument must be a URI...
        if (!((Expression) (it.next())).getType().toString().equals(AnyURIAttribute.identifier))
            throw new IllegalArgumentException("illegal parameter");

        // ...and all following arguments must be strings
        while (it.hasNext()) {
            if (!((Expression) (it.next())).getType().toString().equals(StringAttribute.identifier))
                throw new IllegalArgumentException("illegal parameter");
        }
    }

    /**
     * Evaluates the function given the input data. This function expects an
     * <code>AnyURIAttribute</code> followed by one or more <code>StringAttribute</code>s, and
     * returns an <code>AnyURIAttribute</code>.
     *
     * @param inputs  the input agrument list
     * @param context the representation of the request
     * @return the result of evaluation
     */
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {
        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null)
            return result;

        // the first argument is always a URI
        String str = ((AnyURIAttribute) (argValues[0])).getValue().toString();

        // the remaining arguments are strings
        StringBuffer buffer = new StringBuffer(str);
        for (int i = 1; i < argValues.length; i++) {
            buffer.append(((StringAttribute) (argValues[i])).getValue());
        }

        // finally, try to convert the string back to a URI
        try {
            return new EvaluationResult(new AnyURIAttribute(new URI(str)));
        } catch (URISyntaxException use) {
            List code = new ArrayList();
            code.add(Status.STATUS_PROCESSING_ERROR);
            String message = NAME_URI_STRING_CONCATENATE + " didn't produce" + " a valid URI: "
                    + str;

            return new EvaluationResult(new Status(code, message));
        }
    }

}
