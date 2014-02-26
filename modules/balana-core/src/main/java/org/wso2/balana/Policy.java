/*
 * @(#)Policy.java
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
import org.wso2.balana.combine.RuleCombinerElement;
import org.wso2.balana.combine.RuleCombiningAlgorithm;

import org.wso2.balana.cond.VariableDefinition;
import org.wso2.balana.cond.VariableManager;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents one of the two top-level constructs in XACML, the PolicyType. This optionally contains
 * rules, which in turn contain most of the logic of a policy.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class Policy extends AbstractPolicy {

    // the set of variable definitions in this policy
    private Set<VariableDefinition> definitions;

    /**
     * Creates a new <code>Policy</code> with only the required elements.
     * 
     * @param id the policy identifier
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param target the <code>AbstractTarget</code> for this policy
     */
    public Policy(URI id, RuleCombiningAlgorithm combiningAlg, AbstractTarget target) {
        this(id, null, combiningAlg, null, target, null, null, null);
    }

    /**
     * Creates a new <code>Policy</code> with only the required elements plus rules.
     * 
     * @param id the policy identifier
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param target the <code>AbstractTarget</code> for this policy
     * @param rules a list of <code>Rule</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public Policy(URI id, RuleCombiningAlgorithm combiningAlg, AbstractTarget target, List<Rule> rules) {
        this(id, null, combiningAlg, null, target, null, rules, null);
    }

    /**
     * Creates a new <code>Policy</code> with the required elements plus a version, rules, and a
     * String description. Note that the version is an XACML 2.0 feature.
     * 
     * @param id the policy identifier
     * @param version the policy version or null for the default (this must always be null for XACML
     *            1.x policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this policy
     * @param rules a list of <code>Rule</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description,
            AbstractTarget target, List<Rule> rules) {
        this(id, version, combiningAlg, description, target, null, rules, null);
    }

    /**
     * Creates a new <code>Policy</code> with the required elements plus a version, rules, a String
     * description and policy defaults. Note that the version is an XACML 2.0 feature.
     * 
     * @param id the policy identifier
     * @param version the policy version or null for the default (this must always be null for XACML
     *            1.x policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this policy
     * @param defaultVersion the XPath version to use
     * @param rules a list of <code>Rule</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description,
            AbstractTarget target, String defaultVersion, List<Rule> rules) {
        this(id, version, combiningAlg, description, target, defaultVersion, rules, null);
    }

    /**
     * Creates a new <code>Policy</code> with the required elements plus a version, rules, a String
     * description, policy defaults, and obligations. Note that the version is an XACML 2.0 feature.
     * 
     * @param id the policy identifier
     * @param version the policy version or null for the default (this must always be null for XACML
     *            1.x policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this policy
     * @param defaultVersion the XPath version to use
     * @param rules a list of <code>Rule</code> objects
     * @param obligations a set of <code>Obligations</code> objects
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description,
            AbstractTarget target, String defaultVersion, List<Rule> rules,
                                                            Set<AbstractObligation> obligations) {
        this(id, version, combiningAlg, description, target, defaultVersion, rules, obligations,
                null);
    }

    /**
     * Creates a new <code>Policy</code> with the required elements plus a version, rules, a String
     * description, policy defaults, obligations, and variable definitions. Note that the version
     * and definitions are XACML 2.0 features.
     * 
     * @param id the policy identifier
     * @param version the policy version or null for the default (this must always be null for XACML
     *            1.x policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy
     * @param target the <code>AbstractTarget</code> for this policy
     * @param defaultVersion the XPath version to use
     * @param rules a list of <code>Rule</code> objects
     * @param obligations a set of <code>Obligations</code> objects
     * @param definitions a set of <code>VariableDefinition</code> objects that must provide all
     *            definitions referenced by all <code>VariableReference</code>s in the policy
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>Rule</code>
     */
    public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description,
            AbstractTarget target, String defaultVersion, List<Rule> rules,
                    Set<AbstractObligation> obligations, Set<VariableDefinition> definitions) {
        super(id, version, combiningAlg, description, target, defaultVersion, obligations, null, null);

        List<CombinerElement> list = null;

        // check that the list contains only rules
        if (rules != null) {
            list = new ArrayList<CombinerElement>();
            for (Rule rule : rules) {
                list.add(new RuleCombinerElement(rule));
            }
        }

        setChildren(list);

        // save the definitions
        if (definitions == null)
            this.definitions = new HashSet<VariableDefinition>();
        else
            this.definitions = Collections.unmodifiableSet(new HashSet<VariableDefinition>(definitions));
    }

    /**
     * Creates a new <code>Policy</code> with the required and optional elements. If you need to
     * provide combining algorithm parameters, you need to use this constructor. Note that unlike
     * the other constructors in this class, the rules list is actually a list of
     * <code>CombinerElement</code>s used to match a rule with any combiner parameters it may have.
     * 
     * @param id the policy identifier
     * @param version the policy version or null for the default (this must always be null for XACML
     *            1.x policies)
     * @param combiningAlg the <code>CombiningAlgorithm</code> used on the rules in this set
     * @param description a <code>String</code> describing the policy or null if there is no
     *            description
     * @param target the <code>AbstractTarget</code> for this policy
     * @param defaultVersion the XPath version to use or null if there is no default version
     * @param ruleElements a list of <code>RuleCombinerElement</code> objects or null if there are
     *            no rules
     * @param obligations a set of <code>Obligations</code> objects or null if there are no
     *            obligations
     * @param definitions a set of <code>VariableDefinition</code> objects that must provide all
     *            definitions referenced by all <code>VariableReference</code>s in the policy
     * @param parameters the <code>List</code> of <code>CombinerParameter</code>s provided for
     *            general use by the combining algorithm
     * 
     * @throws IllegalArgumentException if the <code>List</code> of rules contains an object that is
     *             not a <code>RuleCombinerElement</code>
     */
    public Policy(URI id, String version, RuleCombiningAlgorithm combiningAlg, String description,
            AbstractTarget target, String defaultVersion, List<CombinerElement> ruleElements,
            Set<AbstractObligation> obligations, Set<VariableDefinition> definitions,
            List<CombinerParameter> parameters) {

        super(id, version, combiningAlg, description, target, defaultVersion, obligations, null,
                parameters);

        // check that the list contains only RuleCombinerElements
        if (ruleElements != null) {
            for (Object o : ruleElements) {
                if (!(o instanceof RuleCombinerElement)){
                    throw new IllegalArgumentException("non-Rule in rules");
                }
            }
        }

        setChildren(ruleElements);

        // save the definitions
        if (definitions == null)
            this.definitions = new HashSet<VariableDefinition>();
        else
            this.definitions = Collections.unmodifiableSet(new HashSet<VariableDefinition>(definitions));
    }

    /**
     * Creates a new Policy based on the given root node. This is private since every class is
     * supposed to use a getInstance() method to construct from a Node, but since we want some
     * common code in the parent class, we need this functionality in a constructor.
     *
     * @param root  the node to parse for the <code>Policy</code>
     * the XACML policy, if null use default factories
     * @throws ParsingException ParsingException if the PolicyType is invalid
     */
    private Policy(Node root) throws ParsingException {
        super(root, "Policy", "RuleCombiningAlgId");

        List<Rule> rules = new ArrayList<Rule>();
        HashMap<String, List<CombinerParameter>> parameters =
                                                new HashMap<String, List<CombinerParameter>>();
        HashMap<String, Node> variableIds = new HashMap<String, Node>();
        PolicyMetaData metaData = getMetaData();

        // first off, go through and look for any definitions to get their
        // identifiers up front, since before we parse any references we'll
        // need to know what definitions we support
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (DOMHelper.getLocalName(child).equals("VariableDefinition")) {
                String id = child.getAttributes().getNamedItem("VariableId").getNodeValue();

                // it's an error to have more than one definition with the
                // same identifier
                if (variableIds.containsKey(id))
                    throw new ParsingException("multiple definitions for " + "variable " + id);

                variableIds.put(id, child);
            }
        }

        // now create a manager with the defined variable identifiers
        VariableManager manager = new VariableManager(variableIds, metaData);
        definitions = new HashSet<VariableDefinition>();

        // next, collect the Policy-specific elements
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = DOMHelper.getLocalName(child);

            if (name.equals("Rule")) {
                rules.add(Rule.getInstance(child, metaData, manager));
            } else if (name.equals("RuleCombinerParameters")) {
                String ref = child.getAttributes().getNamedItem("RuleIdRef").getNodeValue();

                // if we found the parameter before than add it the end of
                // the previous paramters, otherwise create a new entry
                if (parameters.containsKey(ref)) {
                    List<CombinerParameter> list = parameters.get(ref);
                    parseParameters(list, child);
                } else {
                    List<CombinerParameter> list = new ArrayList<CombinerParameter>();
                    parseParameters(list, child);
                    parameters.put(ref, list);
                }
            } else if (name.equals("VariableDefinition")) {
                String id = child.getAttributes().getNamedItem("VariableId").getNodeValue();

                // parsing definitions is a little strange, since they can
                // contain references to definitions we haven't yet parsed
                // or circular references, but we still want to verify the
                // references and the types...so, for each definition, we
                // ask the manager though getDefinition, which takes care
                // of loading any forward references, handles loops, etc.
                // It also handles caching definitions, so we don't end
                // up parsing the same definitions multiple times
                definitions.add(manager.getDefinition(id));
            }
        }

        definitions = Collections.unmodifiableSet(definitions);

        // now make sure that we can match up any parameters we may have
        // found to a corresponding Rule
        List<CombinerElement> elements = new ArrayList<CombinerElement>();

        for (Rule rule : rules) {
            String id = rule.getId().toString();
            List list = (List) (parameters.remove(id));

            elements.add(new RuleCombinerElement(rule, list));
        }

        // ...and that there aren't extra parameters
        if (!parameters.isEmpty())
            throw new ParsingException("Unmatched parameters in Rule");

        // finally, set the list of Rules
        setChildren(elements);
    }

    /**
     * Helper method that parses out a collection of combiner parameters.
     */
    private void parseParameters(List<CombinerParameter> parameters, Node root) throws ParsingException {
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("CombinerParameter"))
                parameters.add(CombinerParameter.getInstance(node));
        }
    }

    /**
     * Creates an instance of a <code>Policy</code> object based on a DOM node. The node must be the
     * root of PolicyType XML object, otherwise an exception is thrown.
     *
     * @param root the DOM root of a PolicyType XML type
     * the XACML policy, if null use default factories
     * @return  a <code>Policy</code> object
     * @throws ParsingException if the PolicyType is invalid
     */
    public static Policy getInstance(Node root) throws ParsingException {
        // first off, check that it's the right kind of node
        if (!DOMHelper.getLocalName(root).equals("Policy")) {
            throw new ParsingException("Cannot create Policy from root of " + "type "
                    + DOMHelper.getLocalName(root));
        }

        return new Policy(root);
    }

    /**
     * Returns the variable definitions in this Policy.
     * 
     * @return a <code>Set</code> of <code>VariableDefinition</code>s
     */
    public Set getVariableDefinitions() {
        return definitions;
    }

    /**
     * Encodes this <code>Policy</code> into its XML form
     *
     * @return <code>String</code>
     */
    public String encode() {
        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    /**
     * Encodes this <code>Policy</code> into its XML form and writes this out to the provided
     * <code>StringBuilder<code>
     *
     * @param builder string stream into which the XML-encoded data is written
     */
    public void encode(StringBuilder builder) {
        
        String xacmlVersionId = metaData.getXACMLIdentifier();

        String version = getVersion();

        builder.append("<Policy xmlns=\"" + xacmlVersionId + "\""  + " PolicyId=\"" + getId() +
                "\"" + " Version=\"" + version + "").
                append("\" RuleCombiningAlgId=\"").append(getCombiningAlg().getIdentifier().toString()).append("\">\n");

        String description = getDescription();
        if (description != null){
            builder.append("<Description>").append(description).append("</Description>\n");
        }

        String xPathVersion = metaData.getXPathIdentifier();
        if (xPathVersion != null){
            builder.append("<PolicyDefaults><XPathVersion>").
                    append(xPathVersion).append("</XPathVersion></PolicyDefaults>\n");
        }

        getTarget().encode(builder);

        for (VariableDefinition definition : definitions) {
            definition.encode(builder);
        }

        encodeCommonElements(builder);

        builder.append("</Policy>\n");
    }
}
