/*
 * @(#)MapFunction.java	1.4 01/30/03
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

package org.wso2.balana.cond;

import org.wso2.balana.DOMHelper;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;

import org.wso2.balana.attr.BagAttribute;

import org.wso2.balana.ctx.Status;

import java.net.URI;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the higher order bag function map.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
class MapFunction implements Function {

    /**
     * The name of this function
     */
    public static final String NAME_MAP = FunctionBase.FUNCTION_NS + "map";

    // the return type for this instance
    private URI returnType;

    // the stuff used to make sure that we have a valid identifier or a
    // known error, just like in the attribute classes
    private static URI identifier;
    private static RuntimeException earlyException;

    // try to initialize the identifier
    static {
        try {
            identifier = new URI(NAME_MAP);
        } catch (Exception e) {
            earlyException = new IllegalArgumentException();
            earlyException.initCause(e);
        }
    };

    /**
     * Creates a new instance of a <code>MapFunction</code>.
     * 
     * @param returnType the type returned by this function
     */
    public MapFunction(URI returnType) {
        this.returnType = returnType;
    }

    /**
     * Returns a <code>Set</code> containing all the function identifiers supported by this class.
     * 
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set getSupportedIdentifiers() {
        Set set = new HashSet();

        set.add(NAME_MAP);

        return set;
    }

    /**
     * Creates a new instance of the map function using the data found in the DOM node provided.
     * This is called by a proxy when the factory is asked to create one of these functions.
     * 
     * @param root the DOM node of the apply tag containing this function
     * 
     * @return a <code>MapFunction</code> instance
     * 
     * @throws ParsingException if the DOM data was incorrect
     */
    public static MapFunction getInstance(Node root) throws ParsingException {
        URI returnType = null;

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (DOMHelper.getLocalName(node).equals("Function")) {
                String funcName = node.getAttributes().getNamedItem("FunctionId").getNodeValue();
                FunctionFactory factory = FunctionFactory.getGeneralInstance();
                try {
                    Function function = factory.createFunction(funcName);
                    returnType = function.getReturnType();
                    break;
                } catch (FunctionTypeException fte) {
                    // try to get this as an abstract function
                    try {
                        Function function = factory.createAbstractFunction(funcName, root);
                        returnType = function.getReturnType();
                        break;
                    } catch (Exception e) {
                        // any exception here is an error
                        throw new ParsingException("invalid abstract map", e);
                    }
                } catch (Exception e) {
                    // any exception that's not function type is an error
                    throw new ParsingException("couldn't parse map body", e);
                }
            }
        }

        // see if we found the return type
        if (returnType == null)
            throw new ParsingException("couldn't find the return type");

        return new MapFunction(returnType);
    }

    /**
     * Returns the full identifier of this function, as known by the factories.
     * 
     * @return the function's identifier
     */
    public URI getIdentifier() {
        // strictly speaking, this should never happen
        if (earlyException != null)
            throw earlyException;

        return identifier;
    }

    /**
     * Returns the same value as <code>getReturnType</code>. This is here to support the
     * <code>Expression</code> interface.
     * 
     * @return the return type
     */
    public URI getType() {
        return getReturnType();
    }

    /**
     * Returns the attribute type returned by this function.
     * 
     * @return the return type
     */
    public URI getReturnType() {
        return returnType;
    }

    /**
     * Returns <code>true</code>, since the map function always returns a bag
     * 
     * @return true
     */
    public boolean returnsBag() {
        return true;
    }

    /**
     * Helper function to create a processing error message.
     */
    private static EvaluationResult makeProcessingError(String message) {
        ArrayList code = new ArrayList();
        code.add(Status.STATUS_PROCESSING_ERROR);
        return new EvaluationResult(new Status(code, message));
    }

    /**
     * Evaluates the function given the input data. Map expects a <code>Function</code> followed by
     * a <code>BagAttribute</code>.
     * 
     * @param inputs the input agrument list
     * @param context the representation of the request
     * 
     * @return the result of evaluation
     */
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {

        // get the inputs, which we expect to be correct
        Iterator iterator = inputs.iterator();
        Function function = null;

        Expression xpr = (Expression) (iterator.next());
        if (xpr instanceof Function) {
            function = (Function) xpr;
        } else {
            function = (Function) (((VariableReference) xpr).getReferencedDefinition()
                    .getExpression());
        }

        Evaluatable eval = (Evaluatable) (iterator.next());
        EvaluationResult result = eval.evaluate(context);

        // in a higher-order case, if anything is INDETERMINATE, then
        // we stop right away
        if (result.indeterminate())
            return result;

        BagAttribute bag = (BagAttribute) (result.getAttributeValue());

        // param: function, bag
        // return: bag
        // for each value in the bag evaluate the given function with
        // the value and put the function result in a new bag that
        // is ultimately returned

        Iterator it = bag.iterator();
        List<AttributeValue> outputs = new ArrayList<AttributeValue>();

        while (it.hasNext()) {
            List params = new ArrayList();
            params.add(it.next());
            result = function.evaluate(params, context);

            if (result.indeterminate())
                return result;

            outputs.add(result.getAttributeValue());
        }

        return new EvaluationResult(new BagAttribute(returnType, outputs));
    }

    /**
     * Checks that the input list is valid for evaluation.
     * 
     * @param inputs a <code>List</code> of inputs
     * 
     * @throws IllegalArgumentException if the inputs cannot be evaluated
     */
    public void checkInputs(List inputs) throws IllegalArgumentException {
        Object[] list = inputs.toArray();

        // check that we've got the right number of arguments
        if (list.length != 2)
            throw new IllegalArgumentException("map requires two inputs");

        // now check that we've got the right types for map
        Function function = null;

        if (list[0] instanceof Function) {
            function = (Function) (list[0]);
        } else if (list[0] instanceof VariableReference) {
            Expression xpr = ((VariableReference) (list[0])).getReferencedDefinition()
                    .getExpression();
            if (xpr instanceof Function)
                function = (Function) xpr;
        }

        if (function == null)
            throw new IllegalArgumentException("first argument to map must " + "be a Function");
        Evaluatable eval = (Evaluatable) (list[1]);
        if (!eval.returnsBag())
            throw new IllegalArgumentException("second argument to map must " + "be a bag");

        // finally, check that the type in the bag is right for the function
        List input = new ArrayList();
        input.add(list[1]);
        function.checkInputsNoBag(input);
    }

    /**
     * Always throws <code>IllegalArgumentException</code> since map needs to work on a bag
     * 
     * @param inputs a <code>List</code> of inputs
     * 
     * @throws IllegalArgumentException always
     */
    public void checkInputsNoBag(List inputs) throws IllegalArgumentException {
        throw new IllegalArgumentException("map requires a bag");
    }

    /**
     * Encodes this <code>MapFunction</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>MapFunction</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {
        builder.append("<Function FunctionId=\"" + NAME_MAP + "\"/>\n");
    }

}
