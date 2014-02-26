/*
 * @(#)AttributeFinderModule.java
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

package org.wso2.balana.finder;

import org.wso2.balana.ctx.EvaluationCtx;

import org.wso2.balana.attr.BagAttribute;

import org.wso2.balana.cond.EvaluationResult;

import java.net.URI;

import java.util.Set;

import org.w3c.dom.Node;

/**
 * This is the abstract class that all <code>AttributeFinder</code> modules extend. All methods have
 * default values to represent that the given feature isn't supported by this module, so module
 * writers needs only implement the methods for the features they're supporting.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public abstract class AttributeFinderModule {

    /**
     * Returns this module's identifier. A module does not need to provide a unique identifier, but
     * it is a good idea, especially in support of management software. Common identifiers would be
     * the full package and class name (the default if this method isn't overridden), just the class
     * name, or some other well-known string that identifies this class.
     * 
     * @return this module's identifier
     */
    public String getIdentifier() {
        return getClass().getName();
    }

    /**
     * Returns true if this module supports retrieving attributes based on the data provided in an
     * AttributeDesignatorType. By default this method returns false.
     * 
     * @return true if retrieval based on designator data is supported
     */
    public boolean isDesignatorSupported() {
        return false;
    }

    /**
     * Returns true if this module supports retrieving attributes based on the data provided in an
     * AttributeSelectorType. By default this method returns false.
     * 
     * @return true if retrieval based on selector data is supported
     */
    public boolean isSelectorSupported() {
        return false;
    }

    /**
     * Returns a <code>Set</code> of <code>String</code>s that represent which AttributeDesignator
     * categories are supported (eg, Subject, Resource, etc.), or null meaning that no particular types
     * are supported. A return value of null can mean that this module doesn't support designator
     * retrieval, or that it supports designators of all types. If the set is non-null, it should
     * contain the values specified in the <code>AttributeDesignator</code>
     * 
     * @return a <code>Set</code> of <code>Integer</code>s, or null
     */
    public Set<String> getSupportedCategories() {
        return null;
    }

    /**
     * Returns a <code>Set</code> of <code>URI</code>s that represent the attributeIds handled by
     * this module, or null if this module doesn't handle any specific attributeIds. A return value
     * of null means that this module will try to resolve attributes of any id.
     * 
     * @return a <code>Set</code> of <code>URI</code>s, or null
     */
    public Set getSupportedIds() {
        return null;
    }

    /**
     * This is an experimental method that asks the module to invalidate any cache values it may
     * contain. This is not used by any of the core processing code, but it may be used by
     * management software that wants to have some control over these modules. Since a module is
     * free to decide how or if it caches values, and whether it is capable of updating values once
     * in a cache, a module is free to intrepret this message in any way it sees fit (including
     * igoring the message). It is preferable, however, for a module to make every effort to clear
     * any dynamically cached values it contains.
     * <p>
     * This method has been introduced to see what people think of this functionality, and how they
     * would like to use it. It may be removed in future versions, or it may be changed to a more
     * general message-passing system (if other useful messages are identified).
     * 
     * @since 1.2
     */
    public void invalidateCache() {

    }

    /**
     * Tries to find attribute values based on the given designator data. The result, if successful,
     * must always contain a <code>BagAttribute</code>, even if only one value was found. If no
     * values were found, but no other error occurred, an empty bag is returned. This method may
     * need to invoke the context data to look for other attribute values, so a module writer must
     * take care not to create a scenario that loops forever.
     * 
     * @param attributeType the datatype of the attributes to find
     * @param attributeId the identifier of the attributes to find
     * @param issuer the issuer of the attributes, or null if unspecified
     * @param category the category of the attribute whether it is Subject, Resource or any thing
     * @param context the representation of the request data
     * 
     * @return the result of attribute retrieval, which will be a bag of attributes or an error
     */
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer,
            URI category, EvaluationCtx context) {
        return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    }

    /**
     * Tries to find attribute values based on the given selector data. The result, if successful,
     * must always contain a <code>BagAttribute</code>, even if only one value was found. If no
     * values were found, but no other error occurred, an empty bag is returned. This method may
     * need to invoke the context data to look for other attribute values, so a module writer must
     * take care not to create a scenario that loops forever.
     *
     * @param contextPath the XPath expression to search against
     * @param attributeType the datatype of the attributes to find
     * @param contextSelector the selector to find the context to apply XPath expression
     *              if this is null, applied for default content. This is only used with XACML 3.0
     * @param root  the DOM node that XPath evaluation is done. this only used by XACML 3.0
     *              this can be null, if other XACML versions are used.
     * @param context the representation of the request data
     * @param xpathVersion the XPath version to use
     * 
     * @return the result of attribute retrieval, which will be a bag of attributes or an error
     */
    public EvaluationResult findAttribute(String contextPath, URI attributeType,
                    String contextSelector, Node root, EvaluationCtx context, String xpathVersion) {
        
        return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    }

}
