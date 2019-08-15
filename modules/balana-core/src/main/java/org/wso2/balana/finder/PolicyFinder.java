/*
 * @(#)PolicyFinder.java
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

package org.wso2.balana.finder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.*;
import org.wso2.balana.ctx.EvaluationCtx;

import org.wso2.balana.ctx.Status;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is used by the PDP to find all policies used in evaluation. A PDP is given a
 * pre-configured <code>PolicyFinder</code> on construction. The <code>PolicyFinder</code> provides
 * the functionality both to find policies based on a request (ie, retrieve policies and match
 * against the target) and based on an idReference (as can be included in a PolicySet).
 * <p>
 * While this class is typically used by the PDP, it is intentionally designed to support
 * stand-alone use, so it could be the base for a distributed service, or for some application that
 * needs just this functionality. There is nothing in the <code>PolicyFinder</code that relies on
 * the functionality in the PDP. An example of this is a PDP that offloads all policy work by
 * passing requests to another server that does all the retrieval, and passes back the applicable
 * policy. This would require custom code undefined in the XACML spec, but it would free up the
 * server to focus on core policy processing.
 * <p>
 * Note that it is an error to have more than one top-level policy (as explained in the
 * OnlyOneApplicable combining algorithm), so any module that is added to this finder will be
 * evaluated each time a policy is requested. This means that you should think carefully about how
 * many modules you include, and how they can cache policy data.
 * 
 * @since 1.0
 * @author Seth Proctor
 */
public class PolicyFinder {

    /**
     * all modules in this finder
     */
    private Set allModules;

    /**
     * all the request modules
     */
    private Set requestModules;

    /**
     * all the reference modules
     */
    private Set referenceModules;

    /**
     * the logger we'll use for all messages
     */
    private static final Log logger = LogFactory.getLog(PolicyFinder.class);

    /**
     * Default constructor that creates a <code>PDPConfig</code> from components.
     */
    public PolicyFinder() {

    }

    /**
     * Returns the unordered <code>Set</code> of <code>PolicyFinderModule</code>s used by this class
     * to find policies.
     * 
     * @return a <code>Set</code> of <code>PolicyFinderModule</code>s
     */
    public Set getModules() {
        return new HashSet(allModules);
    }

    /**
     * Sets the unordered <code>Set</code> of <code>PolicyFinderModule</code>s used by this class to
     * find policies.
     * 
     * @param modules a <code>Set</code> of <code>PolicyFinderModule</code>s
     */
    public void setModules(Set modules) {
        Iterator it = modules.iterator();

        allModules = new HashSet(modules);
        requestModules = new HashSet();
        referenceModules = new HashSet();

        while (it.hasNext()) {
            PolicyFinderModule module = (PolicyFinderModule) (it.next());

            if (module.isRequestSupported())
                requestModules.add(module);

            if (module.isIdReferenceSupported())
                referenceModules.add(module);
        }
    }

    /**
     * Initializes all modules in this finder.
     */
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing PolicyFinder");
        }

        Iterator it = allModules.iterator();

        while (it.hasNext()) {
            PolicyFinderModule module = (PolicyFinderModule) (it.next());
            module.init(this);
        }
    }

    /**
     * Finds a policy based on a request's context. This may involve using the request data as
     * indexing data to lookup a policy. This will always do a Target match to make sure that the
     * given policy applies. If more than one applicable policy is found, this will return an error.
     * 
     * @param context the representation of the request data
     * 
     * @return the result of trying to find an applicable policy
     */
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        PolicyFinderResult result = null;
        Iterator it = requestModules.iterator();

        // look through all of the modules
        while (it.hasNext()) {
            PolicyFinderModule module = (PolicyFinderModule) (it.next());
            PolicyFinderResult newResult = module.findPolicy(context);

            // if there was an error, we stop right away
            if (newResult.indeterminate()) {
                logger.error("An error occured while trying to find a "
                        + "single applicable policy for a request: "
                        + newResult.getStatus().getMessage());

                return newResult;
            }

            // if we found a policy...
            if (!newResult.notApplicable()) {
                // ...if we already had found a policy, this is an error...
                if (result != null) {
                    logger.error("More than one top-level applicable policy " + "for the request");

                    ArrayList code = new ArrayList();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable " + "top-level policies");
                    return new PolicyFinderResult(status);
                }

                // ...otherwise we remember the result
                result = newResult;
            }
        }

        // if we got here then we didn't have any errors, so the only
        // question is whether or not we found anything
        if (result != null) {
            return result;
        } else {
            logger.debug("No applicable policies were found for the request");

            return new PolicyFinderResult();
        }
    }

    /**
     * Finds a policy based on an id reference. This may involve using the reference as indexing
     * data to lookup a policy. This will always do a Target match to make sure that the given
     * policy applies. If more than one applicable policy is found, this will return an error.
     * 
     * @param idReference the identifier used to resolve a policy
     * @param type type of reference (policy or policySet) as identified by the fields in
     *            <code>PolicyReference</code>
     * @param constraints any optional constraints on the version of the referenced policy
     * @param parentMetaData the meta-data from the parent policy, which provides XACML version,
     *            factories, etc.
     * 
     * @return the result of trying to find an applicable policy
     * 
     * @throws IllegalArgumentException if <code>type</code> is invalid
     */
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints,
            PolicyMetaData parentMetaData) throws IllegalArgumentException {
        PolicyFinderResult result = null;
        Iterator it = referenceModules.iterator();

        if ((type != PolicyReference.POLICY_REFERENCE)
                && (type != PolicyReference.POLICYSET_REFERENCE))
            throw new IllegalArgumentException("Unknown reference type");

        // look through all of the modules
        while (it.hasNext()) {
            PolicyFinderModule module = (PolicyFinderModule) (it.next());
            PolicyFinderResult newResult = module.findPolicy(idReference, type, constraints,
                    parentMetaData);

            // if there was an error, we stop right away
            if (newResult.indeterminate()) {
                logger.error("An error occured while trying to find the " + "referenced policy "
                        + idReference.toString() + ": " + newResult.getStatus().getMessage());

                return newResult;
            }

            // if we found a policy...
            if (!newResult.notApplicable()) {
                // ...if we already had found a policy, this is an error...
                if (result != null) {
                    logger.error("More than one policy applies for the " + "reference: "
                            + idReference.toString());
                    ArrayList code = new ArrayList();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable " + "top-level policies");
                    return new PolicyFinderResult(status);
                }

                // ...otherwise we remember the result
                result = newResult;
            }
        }

        // if we got here then we didn't have any errors, so the only
        // question is whether or not we found anything
        if (result != null) {
            return result;
        } else {
            logger.debug("No policies were resolved for the reference: " + idReference.toString());
            return new PolicyFinderResult();
        }
    }

}
