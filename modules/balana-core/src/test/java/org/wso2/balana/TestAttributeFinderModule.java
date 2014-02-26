
/*
 * @(#)TestAttributeFinderModule.java
 *
 * Copyright 2004 Sun Microsystems, Inc. All Rights Reserved.
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


import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;

import org.wso2.balana.cond.EvaluationResult;

import org.wso2.balana.ctx.Status;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;


/**
 * An <code>AttributeFinderModule</code> used to handle the example
 * role attribute used by the conformance tests.
 *
 * @author Seth Proctor
 */
public class TestAttributeFinderModule extends AttributeFinderModule
{

    /**
     * The example identifier this module supports
     */
    public static final String ROLE_IDENTIFIER =
        "urn:oasis:names:tc:xacml:1.0:example:attribute:role";
    
    // the standard identifier for subject-id
    private static URI subjectIdentifier = null;

    // initialize the standard subject identifier
    static {
        try {
            subjectIdentifier =
                new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
        } catch (URISyntaxException urise) {
            // won't happen in this code
        }
    };

    /**
     * Default constructor.
     */
    public TestAttributeFinderModule() {

    }

    /**
     * Always returns true, since designators are supported.
     *
     * @return true
     */
    public boolean isDesignatorSupported() {
        return true;
    }

    /**
     * Returns only <code>SUBJECT_TARGET</code> since this module only
     * supports Subject attributes.
     *
     * @return a <code>Set</code> with an <code>Integer</code> of value
     *         <code>AttributeDesignator.SUBJECT_TARGET</code>
     */
    public Set getSupportedDesignatorTypes() {
        Set set = new HashSet();

        //set.add(new Integer(XACMLConstants.SUBJECT_TARGET));

        return set;
    }

    /**
     * Returns the one identifer this module supports.
     *
     * @return a <code>Set</code> containing <code>ROLE_IDENTIFIER</code>
     */
    public Set getSupportedIds() {
        Set set = new HashSet();

        set.add(ROLE_IDENTIFIER);

        return set;
    }

    /**
     * Supports the retrieval of exactly one kind of attribute.
     */
    public EvaluationResult findAttribute(URI attributeType, URI attributeId,
                                          URI issuer, URI subjectCategory,
                                          EvaluationCtx context,
                                          int designatorType) {
        // make sure this is the identifier we support
        if (! attributeId.toString().equals(ROLE_IDENTIFIER))
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));

        // make sure we've been asked for a string
        if (! attributeType.toString().equals(StringAttribute.identifier))
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));

        // retrieve the subject identifer from the context

        EvaluationResult result = null; //TODO
//        EvaluationResult result = context.getSubjectAttribute(attributeType, subjectIdentifier,
//                                        issuer, subjectCategory);
        if (result.indeterminate())
            return result;

        // check that we succeeded in getting the subject identifier
        BagAttribute bag = (BagAttribute)(result.getAttributeValue());
        if (bag.isEmpty()) {
            ArrayList code = new ArrayList();
            code.add(Status.STATUS_MISSING_ATTRIBUTE);
            Status status = new Status(code, "missing subject-id");
            return new EvaluationResult(status);
        }
        
        // finally, look for the subject who has the role-mapping defined,
        // and if they're the identified subject, add their role
        BagAttribute returnBag = null;
        Iterator it = bag.iterator();
        while (it.hasNext()) {
            StringAttribute attr = (StringAttribute)(it.next());
            if (attr.getValue().equals("Julius Hibbert")) {
                List<AttributeValue> set = new ArrayList<AttributeValue>();
                set.add(new StringAttribute("Physician"));
                returnBag = new BagAttribute(attributeType, set);
                break;
            }
        }

        return new EvaluationResult(returnBag);
    }

}
