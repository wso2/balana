/*
 * @(#)EvaluationCtx.java
 *
 * Copyright 2003-2006 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.ctx;

import org.wso2.balana.xacml3.MultipleCtxResult;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.TimeAttribute;

import org.wso2.balana.cond.EvaluationResult;

import java.net.URI;

import org.w3c.dom.Node;

/**
 * Manages the context of a single policy evaluation. Typically, an instance is instantiated
 * whenever the PDP gets a request and needs to perform an evaluation as a result.
 * There are two implementations of <code>XACML3EvaluationCtx</code> class for XACML3 and
 *  <code>XACML3EvaluationCtx</code> for XACML2
 * @since 1.0
 * @author Seth Proctor
 */

public interface EvaluationCtx {


    /**
     * Returns the DOM root of the original RequestType XML document, if this context is backed by
     * an XACML Request. If this context is not backed by an XML representation, then an exception
     * is thrown.
     * 
     * @return the DOM root node
     * 
     * @throws UnsupportedOperationException if the context is not backed by an XML representation
     */
    public Node getRequestRoot();

    /**
     * TODO what is this ?
     * @return
     */
    public boolean isSearching();

    /**
     * Returns the value for the current time as known by the PDP (if this value was also supplied
     * in the Request, this will generally be a different value). Details of caching or
     * location-based resolution are left to the underlying implementation.
     * 
     * @return the current time
     */
    public TimeAttribute getCurrentTime();

    /**
     * Returns the value for the current date as known by the PDP (if this value was also supplied
     * in the Request, this will generally be a different value). Details of caching or
     * location-based resolution are left to the underlying implementation.
     * 
     * @return the current date
     */
    public DateAttribute getCurrentDate();

    /**
     * Returns the value for the current dateTime as known by the PDP (if this value was also
     * supplied in the Request, this will generally be a different value). Details of caching or
     * location-based resolution are left to the underlying implementation.
     * 
     * @return the current date
     */
    public DateTimeAttribute getCurrentDateTime();

    /**
     * Returns available subject attribute value(s).
     * 
     * @param type the type of the attribute value(s) to find
     * @param id the id of the attribute value(s) to find
     * @param issuer the issuer of the attribute value(s) to find or null
     * @param category the category the attribute value(s) must be in
     * 
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category);

    /**
     * Returns the attribute value(s) retrieved using the given XPath expression.
     *
     * @param path the XPath expression to search
     * @param type the type of the attribute value(s) to find
     * @param category the category the attribute value(s) must be in
     * @param contextSelector the selector to find the context to apply XPath expression
     *                       if this is null, applied for default content    
     * @param xpathVersion the version of XPath to use
     *
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */

    public EvaluationResult getAttribute(String path, URI type, URI category,
                                         URI contextSelector, String xpathVersion);


    /**
     * Returns XACML version of the context
     *
     * @return  version
     */
    public int getXacmlVersion();


    /**
     * Returns XACML request
     *
     * @return  <code>AbstractRequestCtx</code>
     */
    public AbstractRequestCtx getRequestCtx();

    /**
     * Returns multiple context results. if, request is combination of multiple requests
     *
     * @return <code>MultipleCtxResult</code>
     */
    public MultipleCtxResult getMultipleEvaluationCtx();

}
