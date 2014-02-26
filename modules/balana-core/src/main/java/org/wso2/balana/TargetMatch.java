/*
 * @(#)TargetMatch.java
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

package org.wso2.balana;

import org.wso2.balana.attr.*;

import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.FunctionFactory;
import org.wso2.balana.cond.FunctionTypeException;

import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the SubjectMatch, ResourceMatch, ActionMatch, or EnvironmentMatch (in XACML 2.0 and
 * later) XML types in XACML, depending on the value of the type field. This is the part of the
 * Target that actually evaluates whether the specified attribute values in the Target match the
 * corresponding attribute values in the request context.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class TargetMatch {

    /**
     * An integer value indicating that this class represents a SubjectMatch
     */
    public static final int SUBJECT = 0;

    /**
     * An integer value indicating that this class represents a ResourceMatch
     */
    public static final int RESOURCE = 1;

    /**
     * An integer value indicating that this class represents an ActionMatch
     */
    public static final int ACTION = 2;

    /**
     * An integer value indicating that this class represents an EnvironmentMatch
     */
    public static final int ENVIRONMENT = 3;

    /**
     * Mapping from the 4 match types to their string representations
     */
    public static final String[] NAMES = { "Subject", "Resource", "Action", "Environment"};

    // the type of this target match
    // if there is no type, value is zero
    private int type;

    // the function used for matching
    private Function function;

    // the designator or selector
    private Evaluatable eval;

    // the value
    private AttributeValue attrValue;

    /**
     * Constructor that creates a <code>TargetMatch</code> from components.
     * 
     * @param type an integer indicating whether this class represents a SubjectMatch,
     *            ResourceMatch, or ActionMatch
     * @param function the <code>Function</code> that represents the MatchId
     * @param eval the <code>AttributeDesignator</code> or <code>AttributeSelector</code> to be used
     *            to select attributes from the request context
     * @param attrValue the <code>AttributeValue</code> to compare against
     * 
     * @throws IllegalArgumentException if the input type isn't a valid value
     */
    public TargetMatch(int type, Function function, Evaluatable eval, AttributeValue attrValue)
            throws IllegalArgumentException {

        // check if input type is a valid value
        if ((type != SUBJECT) && (type != RESOURCE) && (type != ACTION) && (type != ENVIRONMENT))
            throw new IllegalArgumentException("Unknown TargetMatch type");

        this.type = type;
        this.function = function;
        this.eval = eval;
        this.attrValue = attrValue;
    }


    /**
     * Constructor that creates a <code>TargetMatch</code> from components.
     *
     * @param function the <code>Function</code> that represents the MatchId
     * @param eval the <code>AttributeDesignator</code> or <code>AttributeSelector</code> to be used
     *            to select attributes from the request context
     * @param attrValue the <code>AttributeValue</code> to compare against
     *
     * @throws IllegalArgumentException if the input type isn't a valid value
     */
    public TargetMatch(Function function, Evaluatable eval, AttributeValue attrValue)
            throws IllegalArgumentException {

        this.function = function;
        this.eval = eval;
        this.attrValue = attrValue;
    }



    /**
     * Creates a <code>TargetMatch</code> by parsing a node, using the input prefix to determine
     * whether this is a SubjectMatch, ResourceMatch, or ActionMatch.
     * 
     * @deprecated As of 2.0 you should avoid using this method and should instead use the version
     *             that takes a <code>PolicyMetaData</code> instance. This method will only work for
     *             XACML 1.x policies.
     * 
     * @param root the node to parse for the <code>TargetMatch</code>
     * @param prefix a String indicating what type of <code>TargetMatch</code> to instantiate
     *            (Subject, Resource, or Action)
     * @param xpathVersion the XPath version to use in any selectors, or null if this is unspecified
     *            (ie, not supplied in the defaults section of the policy)
     * 
     * @return a new <code>TargetMatch</code> constructed by parsing
     * 
     * @throws org.wso2.balana.ParsingException if there was an error during parsing
     * @throws IllegalArgumentException if the input prefix isn't a valid value
     */
    public static TargetMatch getInstance(Node root, String prefix, String xpathVersion)
            throws ParsingException, IllegalArgumentException {
        int i = 0;
        while ((i < NAMES.length) && (!NAMES[i].equals(prefix)))
            i++;

        if (i == NAMES.length)
            throw new IllegalArgumentException("Unknown TargetMatch type");

        return getInstance(root, i, new PolicyMetaData(XACMLConstants.XACML_1_0_IDENTIFIER,
                xpathVersion));
    }


    public static TargetMatch getInstance(Node root, PolicyMetaData metaData) throws ParsingException {        
        return getInstance(root, 0, metaData);
    }

    /**
     * Creates a <code>TargetMatch</code> by parsing a node, using the input prefix to determine
     * whether this is a SubjectMatch, ResourceMatch, or ActionMatch.
     * 
     * @param root the node to parse for the <code>TargetMatch</code>
     * @param matchType the type of <code>TargetMatch</code> as specified by the SUBJECT, RESOURCE,
     *            ACTION, or ENVIRONMENT fields
     * @param metaData the policy's meta-data
     * 
     * @return a new <code>TargetMatch</code> constructed by parsing
     * 
     * @throws ParsingException if there was an error during parsing
     */
    public static TargetMatch getInstance(Node root, int matchType, PolicyMetaData metaData)
            throws ParsingException {

        Function function;
        Evaluatable eval = null;
        AttributeValue attrValue = null;

        AttributeFactory attrFactory = Balana.getInstance().getAttributeFactory();

        // get the function type, making sure that it's really a correct
        // Target function
        String funcName = root.getAttributes().getNamedItem("MatchId").getNodeValue();
        FunctionFactory factory = FunctionFactory.getTargetInstance();
        try {
            URI funcId = new URI(funcName);
            function = factory.createFunction(funcId);
        } catch (URISyntaxException use) {
            throw new ParsingException("Error parsing TargetMatch", use);
        } catch (UnknownIdentifierException uie) {
            throw new ParsingException("Unknown MatchId", uie);
        } catch (FunctionTypeException fte) {
            // try to create an abstract function
            try {
                URI funcId = new URI(funcName);
                function = factory.createAbstractFunction(funcId, root);
            } catch (Exception e) {
                // any exception here is an error
                throw new ParsingException("invalid abstract function", e);
            }
        }

        // next, get the designator or selector being used, and the attribute
        // value paired with it
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String name = DOMHelper.getLocalName(node);

            if (XACMLConstants.XACML_VERSION_3_0 == metaData.getXACMLVersion()
                    && "AttributeDesignator".equals(name)){
                eval = AttributeDesignatorFactory.getFactory().getAbstractDesignator(node, metaData);
            } else if(!(XACMLConstants.XACML_VERSION_3_0 == metaData.getXACMLVersion())
                    && (NAMES[matchType] + "AttributeDesignator").equals(name)){
                eval = AttributeDesignatorFactory.getFactory().getAbstractDesignator(node, metaData);
            } else if (name.equals("AttributeSelector")) {
                eval = AttributeSelectorFactory.getFactory().getAbstractSelector(node, metaData);
            } else if (name.equals("AttributeValue")) {
                try {
                    attrValue = attrFactory.createValue(node);
                } catch (UnknownIdentifierException uie) {
                    throw new ParsingException("Unknown Attribute Type", uie);
                }
            }
        }

        // finally, check that the inputs are valid for this function
        List<Evaluatable> inputs = new ArrayList<Evaluatable>();
        inputs.add(attrValue);
        inputs.add(eval);
        function.checkInputsNoBag(inputs);

        if(XACMLConstants.XACML_VERSION_3_0 == metaData.getXACMLVersion()){
            return new TargetMatch(function, eval, attrValue);
        } else {
            return new TargetMatch(matchType, function, eval, attrValue);
        }
    }

    /**
     * Returns the type of this <code>TargetMatch</code>, either <code>SUBJECT</code>,
     * <code>RESOURCE</code>, <code>ACTION</code>, or <code>ENVIRONMENT</code>.
     * 
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the <code>Function</code> used to do the matching.
     * 
     * @return the match function
     */
    public Function getMatchFunction() {
        return function;
    }

    /**
     * Returns the <code>AttributeValue</code> used by the matching function.
     * 
     * @return the <code>AttributeValue</code> for the match
     */
    public AttributeValue getMatchValue() {
        return attrValue;
    }

    /**
     * Returns the <code>AttributeDesignator</code> or <code>AttributeSelector</code> used by the
     * matching function.
     * 
     * @return the designator or selector for the match
     */
    public Evaluatable getMatchEvaluatable() {
        return eval;
    }

    /**
     * Determines whether this <code>TargetMatch</code> matches the input request (whether it is
     * applicable)
     * 
     * @param context the representation of the request
     * 
     * @return the result of trying to match the TargetMatch and the request
     */
    public MatchResult match(EvaluationCtx context) {
        // start by evaluating the AD/AS
        EvaluationResult result = eval.evaluate(context);

        if (result.indeterminate()) {
            // in this case, we don't ask the function for anything, and we
            // simply return INDETERMINATE
            return new MatchResult(MatchResult.INDETERMINATE, result.getStatus());
        }

        // an AD/AS will always return a bag
        BagAttribute bag = (BagAttribute) (result.getAttributeValue());

        if (!bag.isEmpty()) {
            // we got back a set of attributes, so we need to iterate through
            // them, seeing if at least one matches
            Iterator it = bag.iterator();
            boolean atLeastOneError = false;
            Status firstIndeterminateStatus = null;

            while (it.hasNext()) {
                ArrayList<Evaluatable> inputs = new ArrayList<Evaluatable>();

                inputs.add(attrValue);
                inputs.add((Evaluatable)it.next());

                // do the evaluation
                MatchResult match = evaluateMatch(inputs, context);

                // we only need one match for this whole thing to match
                if (match.getResult() == MatchResult.MATCH) {
                    if (attrValue instanceof StringAttribute) {
                        match.setPolicyValue(((StringAttribute) attrValue).getValue());
                    }
                    return match;

                }

                // if it was INDETERMINATE, we want to remember for later
                if (match.getResult() == MatchResult.INDETERMINATE) {
                    atLeastOneError = true;

                    // there are no rules about exactly what status data
                    // should be returned here, so like in the combining
                    // algs, we'll just track the first error
                    if (firstIndeterminateStatus == null)
                        firstIndeterminateStatus = match.getStatus();
                }
            }

            // if we got here, then nothing matched, so we'll either return
            // INDETERMINATE or NO_MATCH
            if (atLeastOneError)
                return new MatchResult(MatchResult.INDETERMINATE, firstIndeterminateStatus);
            else
                return new MatchResult(MatchResult.NO_MATCH);

        } else {
            // this is just an optimization, since the loop above will
            // actually handle this case, but this is just a little
            // quicker way to handle an empty bag
            return new MatchResult(MatchResult.NO_MATCH);
        }
    }

    /**
     * Private helper that evaluates an individual match.
     *
     * @param inputs  <code>List</code> of <code>Evaluatable</code>
     * @param context  <code>EvaluationCtx</code>
     * @return  match result as <code>MatchResult</code>
     */
    private MatchResult evaluateMatch(List inputs, EvaluationCtx context) {
        // first off, evaluate the function
        EvaluationResult result = function.evaluate(inputs, context);

        // if it was indeterminate, then that's what we return immediately
        if (result.indeterminate())
            return new MatchResult(MatchResult.INDETERMINATE, result.getStatus());

        // otherwise, we figure out if it was a match
        BooleanAttribute bool = (BooleanAttribute) (result.getAttributeValue());

        if (bool.getValue())
            return new MatchResult(MatchResult.MATCH);
        else
            return new MatchResult(MatchResult.NO_MATCH);
    }

    /**
     * Encodes this <code>TargetMatch</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        String tagName = "Match";

        if(type != 0){
            tagName = NAMES[type] + "Match";    
        }

        builder.append("<").append(tagName).append(" MatchId=\"").
                append(function.getIdentifier().toString()).append("\">\n");
        
        attrValue.encode(builder);
        eval.encode(builder);

        builder.append("</").append(tagName).append(">\n");
    }

}
