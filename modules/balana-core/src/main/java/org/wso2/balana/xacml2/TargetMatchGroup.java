/*
 * @(#)TargetMatchGroup.java
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

package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.ctx.EvaluationCtx;

/**
 * This class contains a group of <code>TargetMatch</code> instances and represents the Subject,
 * Resource, Action, and Environment elements in an XACML Target.
 * 
 * @since 2.0
 * @author Seth Proctor
 */
public class TargetMatchGroup {

    // the list of matches
    private List<TargetMatch> matches;

    // the match type contained in this group
    private int matchType;

    // the logger we'll use for all messages
    private static final Log logger = LogFactory.getLog(TargetMatchGroup.class);


    /**
     * Constructor that creates a new <code>TargetMatchGroup</code> based on the given elements.
     * 
     * @param matchElements a <code>List</code> of <code>TargetMatch</code>
     * @param matchType the match type as defined in <code>TargetMatch</code>
     */
    public TargetMatchGroup(List<TargetMatch> matchElements, int matchType) {
        if (matchElements == null)
            matches = Collections.unmodifiableList(new ArrayList<TargetMatch>());
        else
            matches = Collections.unmodifiableList(new ArrayList<TargetMatch>(matchElements));
        this.matchType = matchType;
    }

    /**
     * Creates a <code>Target</code> based on its DOM node.
     * 
     * @param root the node to parse for the target group
     * @param matchType the type of the match
     * @param metaData meta-date associated with the policy
     * 
     * @return a new <code>TargetMatchGroup</code> constructed by parsing
     * 
     * @throws org.wso2.balana.ParsingException if the DOM node is invalid
     */
    public static TargetMatchGroup getInstance(Node root, int matchType, PolicyMetaData metaData)
            throws ParsingException {
        List<TargetMatch> matches = new ArrayList<TargetMatch>();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = DOMHelper.getLocalName(child);
            String matchName = TargetMatch.NAMES[matchType] + "Match";
            if (name.equals(matchName)) {
                matches.add(TargetMatch.getInstance(child, matchType, metaData));
            }
        }

        return new TargetMatchGroup(matches, matchType);
    }

    /**
     * Determines whether this <code>TargetMatchGroup</code> matches the input request (whether it
     * is applicable).
     * 
     * @param context the representation of the request
     * 
     * @return the result of trying to match the group with the context
     */
    public MatchResult match(EvaluationCtx context) {
        MatchResult result = null;
        
        if (matches.isEmpty()) {
            // nothing in target, return match
            return new MatchResult(MatchResult.MATCH);
        }

        for (TargetMatch targetMatch : matches) {
            result = targetMatch.match(context);
            if (result.getResult() != MatchResult.MATCH)
                break;
        }

        return result;
    }

    /**
     * Encodes this <code>TargetMatchGroup</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>TargetMatchGroup</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        String name = TargetMatch.NAMES[matchType];

        builder.append("<").append(name).append(">\n");

        for (TargetMatch targetMatch : matches) {
            targetMatch.encode(builder);
        }

        builder.append("</").append(name).append(">\n");
    }

    

}
