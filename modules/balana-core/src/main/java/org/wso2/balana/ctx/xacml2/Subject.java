/*
 * @(#)Subject.java
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

import org.wso2.balana.attr.AttributeDesignator;

import java.net.URI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the collection of <code>Attribute</code>s associated with a particular
 * subject.
 * 
 * @since 1.1
 * @author seth proctor
 */
public class Subject {

    // the subject's category
    private URI category;

    // the attributes associated with the subject
    private Set attributes;

    /**
     * <code>URI</code> form of the default subject category
     */
    public static final URI DEFAULT_CATEGORY;

    // the exception thrown if the default category was invalid
    private static RuntimeException earlyException = null;

    /**
     * Tries to initialize the default category, keeping track of the exception for later use (if
     * there was a problem). Note that this should never happen, but the error case will be reported
     * correctly if the default string is invalid.
     */
    static {
        URI defaultURI = null;

        try {
            defaultURI = new URI(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT);
        } catch (Exception e) {
            earlyException = new IllegalArgumentException("invalid URI");
            earlyException.initCause(e);
        }

        DEFAULT_CATEGORY = defaultURI;
    }

    /**
     * Creates a new collection of subject attributes using the default subject cateorgy.
     * 
     * @param attributes a non-null <code>Set</code> of <code>Attribute</code> objects
     */
    public Subject(Set attributes) {
        this(null, attributes);

        if (earlyException != null)
            throw earlyException;
    }

    /**
     * Creates a new collection of subject attributes using the given subject category.
     * 
     * @param category the subject category or null for the default category
     * @param attributes a non-null <code>Set</code> of <code>Attribute</code> objects
     */
    public Subject(URI category, Set attributes) {
        if (category == null)
            this.category = DEFAULT_CATEGORY;
        else
            this.category = category;

        this.attributes = Collections.unmodifiableSet(new HashSet(attributes));
    }

    /**
     * Returns the category of this subject's attributes.
     * 
     * @return the category
     */
    public URI getCategory() {
        return category;
    }

    /**
     * Returns the <code>Attribute</code>s associated with this subject.
     * 
     * @return the immutable <code>Set</code> of <code>Attribute</code>s
     */
    public Set getAttributes() {
        return attributes;
    }

}
