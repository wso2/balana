/*
 * @(#)RequestCtx.java
 *
 * Copyright 2003-2004 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.ctx.xacml2;

import org.wso2.balana.DOMHelper;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.Indenter;
import org.wso2.balana.ParsingException;

import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URI;
import java.util.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.ctx.*;
import org.wso2.balana.xacml3.Attributes;

/**
 * Represents a XACML2 request made to the PDP. This is the class that contains all the data used to start
 * a policy evaluation.
 * 
 * @since 1.0
 * @author Seth Proctor
 * @author Marco Barreno
 */
public class RequestCtx extends AbstractRequestCtx {

    /**
     * The optional, generic resource content
     */
    private String resourceContent;

    // There must be at least one subject
    private Set<Subject> subjects = null;

    // There must be exactly one resource
    private Set resource = null;

    // There must be exactly one action
    private Set action = null;

    // There may be any number of environment attributes
    private Set environment = null;
    
    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     */
    public RequestCtx(Set<Attributes> attributesSet, Node documentRoot) {
        this(attributesSet, documentRoot, null);
    }


    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param documentRoot the root node of the DOM tree for this request
     * @param version xacml version of the request
     */
    public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, int version) {
        this(attributesSet, documentRoot,  null);
    }

    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param resourceContent a text-encoded version of the content, suitable for including in the
     *            RequestType, including the root <code>RequestContent</code> node
     */
    public RequestCtx(Set<Attributes> attributesSet,  String resourceContent) {
        this( attributesSet, null, resourceContent);
    }

    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param attributesSet
     * @param documentRoot the root node of the DOM tree for this request
     * @param resourceContent a text-encoded version of the content, suitable for including in the
     *            RequestType, including the root <code>RequestContent</code> node
     * 
     * @throws IllegalArgumentException if the inputs are not well formed
     */
    public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, String resourceContent)
                                                                    throws IllegalArgumentException {

        this.attributesSet = attributesSet;
        this.documentRoot = documentRoot;
        this.resourceContent = resourceContent;
        this.xacmlVersion = XACMLConstants.XACML_VERSION_2_0;
    }

    /**
     *
     * @param subjects
     * @param resource
     * @param action
     * @param environment
     * @throws IllegalArgumentException
     */
    public RequestCtx(Set<Subject> subjects, Set<Attribute> resource, Set<Attribute> action,
                      Set<Attribute> environment) throws IllegalArgumentException {
        this(null, null, subjects, resource, action, environment, null);

    }

    /**
     * Constructor that creates a <code>RequestCtx</code> from components.
     *
     * @param attributesSet
     * @param documentRoot the root node of the DOM tree for this request
     * @param resourceContent a text-encoded version of the content, suitable for including in the
     *            RequestType, including the root <code>RequestContent</code> node
     *
     * @throws IllegalArgumentException if the inputs are not well formed
     */
    public RequestCtx(Set<Attributes> attributesSet, Node documentRoot, Set<Subject> subjects,
                      Set<Attribute> resource, Set<Attribute> action,  Set<Attribute> environment,
                      String resourceContent) throws IllegalArgumentException {

        this.attributesSet = attributesSet;
        this.documentRoot = documentRoot;
        this.subjects = subjects;
        this.resource = resource;
        this.action = action;
        this.environment = environment;
        this.resourceContent = resourceContent;
        this.xacmlVersion = XACMLConstants.XACML_VERSION_2_0;
    }

    /**
     * Create a new <code>RequestCtx</code> by parsing a node. This node should be created by
     * schema-verified parsing of an <code>XML</code> document.
     * 
     * @param root the node to parse for the <code>RequestCtx</code>
     * 
     * @return a new <code>RequestCtx</code> constructed by parsing
     * 
     * @throws ParsingException if the DOM node is invalid
     */
    public static RequestCtx getInstance(Node root) throws ParsingException {

        Set<Subject> newSubjects = new HashSet<Subject>();
        Set<Attributes> attributesSet =  new HashSet<Attributes>();
        Node content = null;
        Set<Attribute> newResource = null;
        Set<Attribute> newAction = null;
        Set<Attribute> newEnvironment = null;

        // First check to be sure the node passed is indeed a Request node.
        String tagName = DOMHelper.getLocalName(root);
        if (!tagName.equals("Request")) {
            throw new ParsingException("Request cannot be constructed using " + "type: "
                    + DOMHelper.getLocalName(root));
        }

        // Now go through its child nodes, finding Subject,
        // Resource, Action, and Environment data
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String tag = DOMHelper.getLocalName(node);
            if (tag.equals("Subject")) {
                // see if there is a category
                Node catNode = node.getAttributes().getNamedItem("SubjectCategory");
                URI category = null;

                if (catNode != null) {
                    try {
                        category = new URI(catNode.getNodeValue());
                    } catch (Exception e) {
                        throw new ParsingException("Invalid Category URI", e);
                    }
                }
                
                // now we get the attributes
                Set<Attribute> attributes = parseAttributes(node);
                // finally, add the list to the set of subject attributes
                newSubjects.add(new Subject(category, attributes));
                // finally, add the list to the set of subject attributes
                attributesSet.add(new Attributes(category, null, attributes, null));

                // make sure that there is at least one Subject
                if(newSubjects.size() < 1){
                    throw new ParsingException("Request must a contain subject");
                }

            } else if (tag.equals("Resource")) {

                NodeList nodes = node.getChildNodes();
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node child = nodes.item(j);
                    if (DOMHelper.getLocalName(node).equals(XACMLConstants.RESOURCE_CONTENT)) {
                        // only one value can be in an Attribute
                        if (content != null){
                            throw new ParsingException("Too many resource content elements are defined.");
                        }
                        // now get the value
                        content = node;
                    }
                }
                // For now, this code doesn't parse the content, since it's
                // a set of anys with a set of anyAttributes, and therefore
                // no useful data can be gleaned from it anyway. The theory
                // here is that it's only useful in the instance doc, so
                // we won't bother parse it, but we may still want to go
                // back and provide some support at some point...

                newResource = parseAttributes(node);
                attributesSet.add(new Attributes(null, content, newResource, null));

            } else if (tag.equals("Action")) {
                newAction = parseAttributes(node);
                attributesSet.add(new Attributes(null, content, newAction, null));
            } else if (tag.equals("Environment")) {
                newEnvironment = parseAttributes(node);
                attributesSet.add(new Attributes(null, content, newEnvironment, null));
            }
        }

        // if we didn't have an environment section, the only optional section
        // of the four, then create a new empty set for it
        if (newEnvironment == null){
            newEnvironment = new HashSet<Attribute>();
            attributesSet.add(new Attributes(null, content, newEnvironment, null));
        }
        // Now create and return the RequestCtx from the information
        // gathered
        return new RequestCtx(attributesSet, root,newSubjects, newResource,
                newAction, newEnvironment, null);
    }

    /*
     * Helper method that parses a set of Attribute types. The Subject, Action and Environment
     * sections all look like this.
     */
    private static Set<Attribute> parseAttributes(Node root) throws ParsingException {
        Set<Attribute> set = new HashSet<Attribute>();

        // the Environment section is just a list of Attributes
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (DOMHelper.getLocalName(node).equals("Attribute"))
                set.add(Attribute.getInstance(node, XACMLConstants.XACML_VERSION_2_0));
        }

        return set;
    }
    
    /**
     * Returns a <code>Set</code> containing <code>Subject</code> objects.
     *
     * @return the request's subject attributes
     */
    public Set getSubjects() {
        return subjects;
    }

    /**
     * Returns a <code>Set</code> containing <code>Attribute</code> objects.
     *
     * @return the request's resource attributes
     */
    public Set getResource() {
        return resource;
    }

    /**
     * Returns a <code>Set</code> containing <code>Attribute</code> objects.
     *
     * @return the request's action attributes
     */
    public Set getAction() {
        return action;
    }

    /**
     * Returns a <code>Set</code> containing <code>Attribute</code> objects.
     *
     * @return the request's environment attributes
     */
    public Set getEnvironmentAttributes() {
        return environment;
    }

    /**
     * Returns the root DOM node of the document used to create this object, or null if this object
     * was created by hand (ie, not through the <code>getInstance</code> method) or if the root node
     * was not provided to the constructor.
     *
     * @return the root DOM node or null
     */
    public Node getDocumentRoot() {
        return documentRoot;
    }    

    /**
     * Encodes this  <code>AbstractRequestCtx</code>  into its XML representation and writes this encoding to the given
     * <code>OutputStream</code>. No indentation is used.
     * 
     * @param output a stream into which the XML-encoded data is written
     */
    public void encode(OutputStream output) {
        encode(output, new Indenter(0));
    }

    /**
     * Encodes this  <code>AbstractRequestCtx</code>  into its XML representation and writes this encoding to the given
     * <code>OutputStream</code> with indentation.
     * 
     * @param output a stream into which the XML-encoded data is written
     * @param indenter an object that creates indentation strings
     */
    public void encode(OutputStream output, Indenter indenter) {

        // Make a PrintStream for a nicer printing interface
        PrintStream out = new PrintStream(output);

        // Prepare the indentation string
        String topIndent = indenter.makeString();
        out.println(topIndent + "<Request xmlns=\"" + XACMLConstants.RESOURCE_SCOPE_2_0 + "\" >");

        // go in one more for next-level elements...
        indenter.in();
        String indent = indenter.makeString();

        // ...and go in again for everything else
        indenter.in();

        // first off, go through all subjects
        Iterator it = subjects.iterator();
        while (it.hasNext()) {
            Subject subject = (Subject) (it.next());

            out.print(indent + "<Subject SubjectCategory=\"" + subject.getCategory().toString()
                    + "\"");

            Set subjectAttrs = subject.getAttributes();

            if (subjectAttrs.size() == 0) {
                // there's nothing in this Subject, so just close the tag
                out.println("/>");
            } else {
                // there's content, so fill it in
                out.println(">");

                encodeAttributes(subjectAttrs, out, indenter);

                out.println(indent + "</Subject>");
            }
        }

        // next do the resource
        if ((resource.size() != 0) || (resourceContent != null)) {
            out.println(indent + "<Resource>");
            if (resourceContent != null)
                out.println(indenter.makeString() + "<ResourceContent>" + resourceContent
                        + "</ResourceContent>");
            encodeAttributes(resource, out, indenter);
            out.println(indent + "</Resource>");
        } else {
            out.println(indent + "<Resource/>");
        }

        // now the action
        if (action.size() != 0) {
            out.println(indent + "<Action>");
            encodeAttributes(action, out, indenter);
            out.println(indent + "</Action>");
        } else {
            out.println(indent + "<Action/>");
        }

        // finally the environment, if there are any attrs
        if (environment.size() != 0) {
            out.println(indent + "<Environment>");
            encodeAttributes(environment, out, indenter);
            out.println(indent + "</Environment>");
        }

        // we're back to the top
        indenter.out();
        indenter.out();

        out.println(topIndent + "</Request>");
    }

    /**
     * Private helper function to encode the attribute sets
     */
    private void encodeAttributes(Set attributes, PrintStream out, Indenter indenter) {
//        Iterator it = attributes.iterator();
//        while (it.hasNext()) {                       TODO
//            Attribute attr = (Attribute) (it.next());
//            attr.encode(out, indenter);
//        }
    }

}
