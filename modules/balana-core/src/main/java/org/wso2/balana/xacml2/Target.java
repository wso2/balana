/*
 * @(#)Target.java
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

package org.wso2.balana.xacml2;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.ctx.EvaluationCtx;

/**
 * Represents the TargetType XML type in XACML. This also stores several other XML types: Subjects,
 * Resources, Actions, and Environments (in XACML 2.0 and later). The target is used to quickly
 * identify whether the parent element (a policy set, policy, or rule) is applicable to a given
 * request.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class Target extends AbstractTarget {

    // the four sections of a Target
    private TargetSection subjectsSection;
    private TargetSection resourcesSection;
    private TargetSection actionsSection;
    private TargetSection environmentsSection;

    // the version of XACML of the policy containing this target
    private int xacmlVersion;

    // the logger we'll use for all messages
    private static Log logger = LogFactory.getLog(Target.class);

    /**
     * Constructor that creates an XACML 1.x <code>Target</code> from components. Each of the
     * sections must be non-null, but they may match any request. Because this is only used for 1.x
     * Targets, there is no Environments section.
     * 
     * @param subjectsSection a <code>TargetSection</code> representing the Subjects section of this
     *            target
     * @param resourcesSection a <code>TargetSection</code> representing the Resources section of
     *            this target
     * @param actionsSection a <code>TargetSection</code> representing the Actions section of this
     *            target
     */
    public Target(TargetSection subjectsSection, TargetSection resourcesSection,
            TargetSection actionsSection) {
        if ((subjectsSection == null) || (resourcesSection == null) || (actionsSection == null))
            throw new ProcessingException("All sections of a Target must " + "be non-null");

        this.subjectsSection = subjectsSection;
        this.resourcesSection = resourcesSection;
        this.actionsSection = actionsSection;
        this.environmentsSection = new TargetSection(null, TargetMatch.ENVIRONMENT,
                XACMLConstants.XACML_VERSION_1_0);
        this.xacmlVersion = XACMLConstants.XACML_VERSION_1_0;
    }

    /**
     * Constructor that creates an XACML 2.0 <code>Target</code> from components. Each of the
     * sections must be non-null, but they may match any request.
     * 
     * @param subjectsSection a <code>TargetSection</code> representing the Subjects section of this
     *            target
     * @param resourcesSection a <code>TargetSection</code> representing the Resources section of
     *            this target
     * @param actionsSection a <code>TargetSection</code> representing the Actions section of this
     *            target
     * @param environmentsSection a <code>TargetSection</code> representing the Environments section
     *            of this target
     */
    public Target(TargetSection subjectsSection, TargetSection resourcesSection,
            TargetSection actionsSection, TargetSection environmentsSection) {
        if ((subjectsSection == null) || (resourcesSection == null) || (actionsSection == null)
                || (environmentsSection == null))
            throw new ProcessingException("All sections of a Target must " + "be non-null");

        this.subjectsSection = subjectsSection;
        this.resourcesSection = resourcesSection;
        this.actionsSection = actionsSection;
        this.environmentsSection = environmentsSection;
        this.xacmlVersion = XACMLConstants.XACML_VERSION_2_0;
    }

    /**
     * Creates a <code>Target</code> by parsing a node.
     * 
     * @deprecated As of 2.0 you should avoid using this method and should instead use the version
     *             that takes a <code>PolicyMetaData</code> instance. This method will only work for
     *             XACML 1.x policies.
     * 
     * @param root the node to parse for the <code>Target</code>
     * @param xpathVersion the XPath version to use in any selectors, or null if this is unspecified
     *            (ie, not supplied in the defaults section of the policy)
     * 
     * @return a new <code>Target</code> constructed by parsing
     * 
     * @throws ParsingException if the DOM node is invalid
     */
    public static Target getInstance(Node root, String xpathVersion) throws ParsingException {
        return getInstance(root, new PolicyMetaData(XACMLConstants.XACML_1_0_IDENTIFIER,
                xpathVersion));
    }

    /**
     * Creates a <code>Target</code> by parsing a node.
     * 
     * @param root the node to parse for the <code>Target</code>
     * @param metaData
     * @return a new <code>Target</code> constructed by parsing
     * 
     * @throws ParsingException if the DOM node is invalid
     */
    public static Target getInstance(Node root, PolicyMetaData metaData) throws ParsingException {
        
        TargetSection subjects = null;
        TargetSection resources = null;
        TargetSection actions = null;
        TargetSection environments = null;
        int version = metaData.getXACMLVersion();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = DOMHelper.getLocalName(child);

            if (name.equals("Subjects")) {
                subjects = TargetSection.getInstance(child, TargetMatch.SUBJECT, metaData);
            } else if (name.equals("Resources")) {
                resources = TargetSection.getInstance(child, TargetMatch.RESOURCE, metaData);
            } else if (name.equals("Actions")) {
                actions = TargetSection.getInstance(child, TargetMatch.ACTION, metaData);
            } else if (name.equals("Environments")) {
                environments = TargetSection.getInstance(child, TargetMatch.ENVIRONMENT, metaData);
            }
        }

        // starting in 2.0 an any-matching section is represented by a
        // missing element, and in 1.x there were no Environments elements,
        // so these need to get turned into non-null arguments


        if (subjects == null)
            subjects = new TargetSection(null, TargetMatch.SUBJECT, version);
        if (resources == null)
            resources = new TargetSection(null, TargetMatch.RESOURCE, version);
        if (actions == null)
            actions = new TargetSection(null, TargetMatch.ACTION, version);

        if (version == XACMLConstants.XACML_VERSION_2_0) {
            if (environments == null)
                environments = new TargetSection(null, TargetMatch.ENVIRONMENT, version);
            return new Target(subjects, resources, actions, environments);
        } else {
            return new Target(subjects, resources, actions);
        }

    }

    /**
     * Returns the Subjects section of this Target.
     * 
     * @return a <code>TargetSection</code> representing the Subjects
     */
    public TargetSection getSubjectsSection() {
        return subjectsSection;
    }

    /**
     * Returns the Resources section of this Target.
     * 
     * @return a <code>TargetSection</code> representing the Resources
     */
    public TargetSection getResourcesSection() {
        return resourcesSection;
    }

    /**
     * Returns the Actions section of this Target.
     * 
     * @return a <code>TargetSection</code> representing the Actions
     */
    public TargetSection getActionsSection() {
        return actionsSection;
    }

    /**
     * Returns the Environments section of this Target. Note that if this is an XACML 1.x policy,
     * then the section will always match anything, since XACML 1.x doesn't support matching on the
     * Environment.
     * 
     * @return a <code>TargetSection</code> representing the Environments
     */
    public TargetSection getEnvironmentsSection() {
        return environmentsSection;
    }

    /**
     * Returns whether or not this <code>Target</code> matches any request.
     * 
     * @return true if this Target matches any request, false otherwise
     */
    public boolean matchesAny() {
        return subjectsSection.matchesAny() && resourcesSection.matchesAny()
                && actionsSection.matchesAny() && environmentsSection.matchesAny();
    }

    /**
     * Determines whether this <code>Target</code> matches the input request (whether it is
     * applicable).
     * 
     * @param context the representation of the request
     * 
     * @return the result of trying to match the target and the request
     */
    public MatchResult match(EvaluationCtx context) {
        MatchResult result = null;
        String subjectPolicyValue;
        String resourcePolicyValue;
        String actionPolicyValue;
        String envPolicyValue;
        

        // before matching, see if this target matches any request
        if (matchesAny())
            return new MatchResult(MatchResult.MATCH);

        // first, try matching the Subjects section
        result = subjectsSection.match(context);
        if (result.getResult() != MatchResult.MATCH) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to match Subjects section of Target");
            }
            return result;
        }
        subjectPolicyValue = result.getPolicyValue();

        // now try matching the Resources section
        result = resourcesSection.match(context);
        if (result.getResult() != MatchResult.MATCH) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to match Resources section of Target");
            }
            return result;
        }

        resourcePolicyValue = result.getPolicyValue();

        // next, look at the Actions section
        result = actionsSection.match(context);
        if (result.getResult() != MatchResult.MATCH) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to match Actions section of Target");
            }
            return result;
        }
        
        actionPolicyValue = result.getPolicyValue();

        // finally, match the Environments section
        result = environmentsSection.match(context);
        if (result.getResult() != MatchResult.MATCH) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to match Environments section of Target");
            }
            return result;
        }
        
        envPolicyValue = result.getPolicyValue();
        
        result.setActionPolicyValue(actionPolicyValue);
        result.setSubjectPolicyValue(subjectPolicyValue);
        result.setEnvPolicyValue(envPolicyValue);
        result.setResourcePolicyValue(resourcePolicyValue);

        // if we got here, then everything matched
        return result;
    }

    /**
     * Encodes this <code>Target</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>Target</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {
        // see if this Target matches anything
        boolean matchesAny = (subjectsSection.matchesAny() && resourcesSection.matchesAny()
                && actionsSection.matchesAny() && environmentsSection.matchesAny());

        if (matchesAny && (xacmlVersion == XACMLConstants.XACML_VERSION_2_0)) {
            // in 2.0, if all the sections match any request, then the Target
            // element is empty and should be encoded simply as en empty tag
            builder.append("<Target/>\n");
        } else {
            builder.append("<Target>\n");
            subjectsSection.encode(builder);
            resourcesSection.encode(builder);
            actionsSection.encode(builder);

            // we should only do this if we're a 2.0 policy
            if (xacmlVersion == XACMLConstants.XACML_VERSION_2_0){
                environmentsSection.encode(builder);
            }
            builder.append("</Target>\n");
        }
    }

}
