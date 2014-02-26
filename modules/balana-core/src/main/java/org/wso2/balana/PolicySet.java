/*
 * @(#)PolicySet.java
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

import org.wso2.balana.combine.CombinerElement;
import org.wso2.balana.combine.CombinerParameter;
import org.wso2.balana.combine.PolicyCombinerElement;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;

import org.wso2.balana.finder.PolicyFinder;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents one of the two top-level constructs in XACML, the PolicySetType. This can contain
 * other policies and policy sets, and can also contain URIs that point to policies and policy sets.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class PolicySet extends AbstractPolicy {

    /**
     * Creates a new <code>PolicySet</code> with only the required elements.
     * 
     * @param id the policy set identifier
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the policies in this set
     * @param target the <code>AbstractTarget</code> for this set
     */
    public PolicySet(URI id, PolicyCombiningAlgorithm combiningAlg, AbstractTarget target) {
        this(id, null, combiningAlg, null, target, null, null, null);
    }

    /**
     * Creates a new <code>PolicySet</code> with only the required elements, plus some policies.
     * 
     * @param id the policy set identifier
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the policies in this set
     * @param target the <code>AbstractTarget</code> for this set
     * @param policies a list of <code>AbstractPolicy</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of policies contains an object that
     *             is not an <code>AbstractPolicy</code>
     */
    public PolicySet(URI id, PolicyCombiningAlgorithm combiningAlg, AbstractTarget target, List policies) {
        this(id, null, combiningAlg, null, target, policies, null, null);
    }

    /**
     * Creates a new <code>PolicySet</code> with the required elements plus some policies and a
     * String description.
     * 
     * @param id the policy set identifier
     * @param version the policy version or null for the default (this is always null for pre-2.0
     *            policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the policies in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this set
     * @param policies a list of <code>AbstractPolicy</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of policies contains an object that
     *             is not an <code>AbstractPolicy</code>
     */
    public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg,
            String description, AbstractTarget target, List policies) {
        this(id, version, combiningAlg, description, target, policies, null, null);
    }

    /**
     * Creates a new <code>PolicySet</code> with the required elements plus some policies, a String
     * description, and policy defaults.
     * 
     * @param id the policy set identifier
     * @param version the policy version or null for the default (this is always null for pre-2.0
     *            policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the policies in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this set
     * @param policies a list of <code>AbstractPolicy</code> objects
     * @param defaultVersion the XPath version to use
     * 
     * @throws IllegalArgumentException if the <code>List</code> of policies contains an object that
     *             is not an <code>AbstractPolicy</code>
     */
    public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg,
            String description, AbstractTarget target, List policies, String defaultVersion) {
        this(id, version, combiningAlg, description, target, policies, defaultVersion, null);
    }

    /**
     * Creates a new <code>PolicySet</code> with the required elements plus some policies, a String
     * description, policy defaults, and obligations.
     * 
     * @param id the policy set identifier
     * @param version the policy version or null for the default (this is always null for pre-2.0
     *            policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the policies in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this set
     * @param policies a list of <code>AbstractPolicy</code> objects
     * @param defaultVersion the XPath version to use
     * @param obligations a set of <code>Obligation</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of policies contains an object that
     *             is not an <code>AbstractPolicy</code>
     */
    public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg,
            String description, AbstractTarget target, List<AbstractPolicy> policies, String defaultVersion,
            Set<AbstractObligation> obligations) {
        super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, null);

        List<CombinerElement> list = null;

        // check that the list contains only AbstractPolicy objects
        if (policies != null) {
            list = new ArrayList<CombinerElement>();
            Iterator it = policies.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!(o instanceof AbstractPolicy))
                    throw new IllegalArgumentException("non-AbstractPolicy " + "in policies");
                list.add(new PolicyCombinerElement((AbstractPolicy) o));
            }
        }

        setChildren(list);
    }

    /**
     * Creates a new <code>PolicySet</code> with the required and optional elements. If you need to
     * provide combining algorithm parameters, you need to use this constructor. Note that unlike
     * the other constructors in this class, the policies list is actually a list of
     * <code>CombinerElement</code>s used to match a policy with any combiner parameters it may
     * have.
     * 
     * @param id the policy set identifier
     * @param version the policy version or null for the default (this is always null for pre-2.0
     *            policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy or null if there is no
     *            description
     * @param target the <code>AbstractTarget</code> for this policy
     * @param policyElements a list of <code>CombinerElement</code> objects or null if there are no
     *            policies
     * @param defaultVersion the XPath version to use or null if there is no default version
     * @param obligations a set of <code>Obligations</code> objects or null if there are no
     *            obligations
     * @param parameters the <code>List</code> of <code>CombinerParameter</code>s provided for
     *            general use by the combining algorithm
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public PolicySet(URI id, String version, PolicyCombiningAlgorithm combiningAlg,
            String description, AbstractTarget target, List policyElements, String defaultVersion,
            Set<AbstractObligation> obligations, List<CombinerParameter>  parameters) {
        
        super(id, version, combiningAlg, description, target, defaultVersion, obligations, null,
                parameters);

        // check that the list contains only CombinerElements
        if (policyElements != null) {
            Iterator it = policyElements.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!(o instanceof PolicyCombinerElement))
                    throw new IllegalArgumentException("non-AbstractPolicy " + "in policies");
            }
        }

        setChildren(policyElements);
    }

    /**
     * Creates a new PolicySet based on the given root node. This is private since every class is
     * supposed to use a getInstance() method to construct from a Node, but since we want some
     * common code in the parent class, we need this functionality in a constructor.
     *
     * @param root  the node to parse for the <code>PolicySet</code>
     * @param finder the <code>PolicyFinder</code> used to handle references
     * the XACML policy, if null use default factories
     * @throws ParsingException ParsingException if the PolicyType is invalid
     */
    private PolicySet(Node root, PolicyFinder finder) throws ParsingException {
        
        super(root, "PolicySet", "PolicyCombiningAlgId");

        List<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();
        HashMap<String, List<CombinerParameter>> policyParameters =
                                                new HashMap<String, List<CombinerParameter>>();
        HashMap<String, List<CombinerParameter>> policySetParameters =
                                                new HashMap<String, List<CombinerParameter>>();
        PolicyMetaData metaData = getMetaData();

        // collect the PolicySet-specific elements
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = DOMHelper.getLocalName(child);

            if (name.equals("PolicySet")) {
                policies.add(PolicySet.getInstance(child, finder));
            } else if (name.equals("Policy")) {
                policies.add(Policy.getInstance(child));
            } else if (name.equals("PolicySetIdReference")) {
                policies.add(PolicyReference.getInstance(child, finder, metaData));
            } else if (name.equals("PolicyIdReference")) {
                policies.add(PolicyReference.getInstance(child, finder, metaData));
            } else if (name.equals("PolicyCombinerParameters")) {
                parameterHelper(policyParameters, child, "Policy");
            } else if (name.equals("PolicySetCombinerParameters")) {
                parameterHelper(policySetParameters, child, "PolicySet");
            }
        }

        // now make sure that we can match up any parameters we may have
        // found to a corresponding Policy or PolicySet...
        List<CombinerElement> elements = new ArrayList<CombinerElement>();
        Iterator it = policies.iterator();

        // right now we have to go though each policy and based on several
        // possible cases figure out what parameters might apply...but
        // there should be a better way to do this

        while (it.hasNext()) {
            AbstractPolicy policy = (AbstractPolicy) (it.next());
            List<CombinerParameter> list = null;

            if (policy instanceof Policy) {
                list = policyParameters.remove(policy.getId().toString());
            } else if (policy instanceof PolicySet) {
                list = policySetParameters.remove(policy.getId().toString());
            } else {
                PolicyReference ref = (PolicyReference) policy;
                String id = ref.getReference().toString();
                if (ref.getReferenceType() == PolicyReference.POLICY_REFERENCE){
                    list = policyParameters.remove(id);
                } else {
                    list = policySetParameters.remove(id);
                }
            }

            elements.add(new PolicyCombinerElement(policy, list));
        }

        // ...and that there aren't extra parameters
        if (!policyParameters.isEmpty()) {
            throw new ParsingException("Unmatched parameters in Policy");
        }
        
        if (!policySetParameters.isEmpty()){
            throw new ParsingException("Unmatched parameters in PolicySet");
        }
        // finally, set the list of Rules
        setChildren(elements);
    }

    /**
     * Private helper method that handles parsing a collection of parameters
     * @param parameters
     * @param root
     * @param prefix
     * @param parameters
     * @throws ParsingException
     */
    private void parameterHelper(HashMap<String, List<CombinerParameter>> parameters,
                                                Node root, String prefix) throws ParsingException {
        
        String ref = root.getAttributes().getNamedItem(prefix + "IdRef").getNodeValue();

        if (parameters.containsKey(ref)) {
            List<CombinerParameter> list = parameters.get(ref);
            parseParameters(list, root);
        } else {
            List<CombinerParameter> list = new ArrayList<CombinerParameter>();
            parseParameters(list, root);
            parameters.put(ref, list);
        }
    }

    /**
     * Private helper method that handles parsing a single parameter.
     * @param parameters
     * @param root
     * @throws ParsingException
     */
    private void parseParameters(List<CombinerParameter> parameters, Node root) throws ParsingException {
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("CombinerParameter")){
                parameters.add(CombinerParameter.getInstance(node));
            }
        }
    }

    /**
     * Creates an instance of a <code>PolicySet</code> object based on a DOM node. The node must be
     * the root of PolicySetType XML object, otherwise an exception is thrown. This
     * <code>PolicySet</code> will not support references because it has no
     * <code>PolicyFinder</code>.
     * 
     * @param root the DOM root of a PolicySetType XML type
     * @return  a <code>PolicySet</code> object
     * @throws ParsingException if the PolicySetType is invalid
     */
    public static PolicySet getInstance(Node root) throws ParsingException {
        return getInstance(root, null);
    }

    /**
     * Creates an instance of a <code>PolicySet</code> object based on a DOM node. The node must be
     * the root of PolicySetType XML object, otherwise an exception is thrown. The finder is used to
     * handle policy references.
     * 
     * @param root the DOM root of a PolicySetType XML type
     * @param finder the <code>PolicyFinder</code> used to handle references
     * @return a <code>PolicySet</code> object
     * @throws ParsingException if the PolicySetType is invalid
     */
    public static PolicySet getInstance(Node root, PolicyFinder finder) throws ParsingException {
        // first off, check that it's the right kind of node
        if (!DOMHelper.getLocalName(root).equals("PolicySet")) {
            throw new ParsingException("Cannot create PolicySet from root of" + " type "
                    + DOMHelper.getLocalName(root));
        }

        return new PolicySet(root, finder);
    }

    /**
     * Encodes this <code>PolicySet</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>PolicySet</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {

        builder.append("<PolicySet PolicySetId=\"").append(getId().toString()).
                append("\" PolicyCombiningAlgId=\"").
                append(getCombiningAlg().getIdentifier().toString()).append("\">\n");

        String description = getDescription();
        if (description != null){
            builder.append("<Description>").append(description).append("</Description>\n");
        }
        
        String version = getDefaultVersion();
        if (version != null){
            builder.append("<PolicySetDefaults><XPathVersion>").append(version).
                    append("</XPathVersion></PolicySetDefaults>\n");
        }
        getTarget().encode(builder);
        encodeCommonElements(builder);

        builder.append("</PolicySet>\n");
    }

}
