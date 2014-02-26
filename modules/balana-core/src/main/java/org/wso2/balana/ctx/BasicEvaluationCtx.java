/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package org.wso2.balana.ctx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.cond.EvaluationResult;

import java.net.URI;
import java.util.Date;

/**
 * Implementation of <code>EvaluationCtx</code>.  This implements some generic methods that
 * commons to most of the  implementations
 */
public abstract class BasicEvaluationCtx implements EvaluationCtx {

    /**
     * the cached current date, time, and date time,
     * which we may or may not be using depending on how this object was constructed
     */
    protected DateAttribute currentDate;
    protected TimeAttribute currentTime;
    protected DateTimeAttribute currentDateTime;

    /**
     * TODO what is this?
     */
    protected boolean useCachedEnvValues = false;

    /**
     * the DOM root the original RequestContext document
     */
    protected Node requestRoot;

    /**
     * Represents a XACML request made to the PDP
     */
    protected AbstractRequestCtx requestCtx;

    /**
     * PDP configurations
     */
    protected PDPConfig pdpConfig;

    /**
     * logger
     */
    private static Log logger = LogFactory.getLog(BasicEvaluationCtx.class);

    /**
     * Returns the DOM root of the original RequestType XML document.
     *
     * @return the DOM root node
     */
    public Node getRequestRoot() {
        return requestRoot;
    }

    /**
     * TODO
     * @return
     */
    public boolean isSearching() {
        return false;
    }


    /**
     * Returns the value for the current time. The current time, current date, and current dateTime
     * are consistent, so that they all represent the same moment. If this is the first time that
     * one of these three values has been requested, and caching is enabled, then the three values
     * will be resolved and stored.
     * <p/>
     * Note that the value supplied here applies only to dynamically resolved values, not those
     * supplied in the Request. In other words, this always returns a dynamically resolved value
     * local to the PDP, even if a different value was supplied in the Request. This is handled
     * correctly when the value is requested by its identifier.
     *
     * @return the current time
     */
    public synchronized TimeAttribute getCurrentTime() {
        long millis = dateTimeHelper();

        if (useCachedEnvValues)
            return currentTime;
        else
            return new TimeAttribute(new Date(millis));
    }

    /**
     * Returns the value for the current date. The current time, current date, and current dateTime
     * are consistent, so that they all represent the same moment. If this is the first time that
     * one of these three values has been requested, and caching is enabled, then the three values
     * will be resolved and stored.
     * <p/>
     * Note that the value supplied here applies only to dynamically resolved values, not those
     * supplied in the Request. In other words, this always returns a dynamically resolved value
     * local to the PDP, even if a different value was supplied in the Request. This is handled
     * correctly when the value is requested by its identifier.
     *
     * @return the current date
     */
    public synchronized DateAttribute getCurrentDate() {
        long millis = dateTimeHelper();

        if (useCachedEnvValues)
            return currentDate;
        else
            return new DateAttribute(new Date(millis));
    }

    /**
     * Returns the value for the current dateTime. The current time, current date, and current
     * dateTime are consistent, so that they all represent the same moment. If this is the first
     * time that one of these three values has been requested, and caching is enabled, then the
     * three values will be resolved and stored.
     * <p/>
     * Note that the value supplied here applies only to dynamically resolved values, not those
     * supplied in the Request. In other words, this always returns a dynamically resolved value
     * local to the PDP, even if a different value was supplied in the Request. This is handled
     * correctly when the value is requested by its identifier.
     *
     * @return the current dateTime
     */
    public synchronized DateTimeAttribute getCurrentDateTime() {
        long millis = dateTimeHelper();

        if (useCachedEnvValues)
            return currentDateTime;
        else
            return new DateTimeAttribute(new Date(millis));
    }

    public AbstractRequestCtx getRequestCtx() {
        return requestCtx;
    }

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
                                         URI contextSelector, String xpathVersion){

        if (pdpConfig.getAttributeFinder() != null) {
            return pdpConfig.getAttributeFinder().findAttribute(path, type, this,
                                        xpathVersion);
        } else {
            logger.warn("Context tried to invoke AttributeFinder but was " +
                           "not configured with one");

            return new EvaluationResult(BagAttribute.createEmptyBag(type));
        }
    }

    /**
     * Private helper that figures out if we need to resolve new values, and returns either the
     * current moment (if we're not caching) or -1 (if we are caching)
     * 
     * @return current moment as long value
     */
    private long dateTimeHelper() {
        // if we already have current values, then we can stop (note this
        // always means that we're caching)
        if (currentTime != null)
            return -1;

        // get the current moment
        Date time = new Date();
        long millis = time.getTime();

        // if we're not caching then we just return the current moment
        if (!useCachedEnvValues) {
            return millis;
        } else {
            // we're caching, so resolve all three values, making sure
            // to use clean copies of the date object since it may be
            // modified when creating the attributes
            currentTime = new TimeAttribute(time);
            currentDate = new DateAttribute(new Date(millis));
            currentDateTime = new DateTimeAttribute(new Date(millis));
        }

        return -1;
    }

    /**
     * Private helper that calls the finder if it's non-null, or else returns an empty bag
     *
     * @param type the type of the attribute value(s) to find
     * @param id the id of the attribute value(s) to find
     * @param issuer the issuer of the attribute value(s) to find or null
     * @param category the category the attribute value(s) must be in
     *
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    protected EvaluationResult callHelper(URI type, URI id, String issuer, URI category) {
        if (pdpConfig.getAttributeFinder() != null) {
            return pdpConfig.getAttributeFinder().findAttribute(type, id, issuer, category, this);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Context tried to invoke AttributeFinder but was "
                        + "not configured with one");
            }

            return new EvaluationResult(BagAttribute.createEmptyBag(type));
        }
    }

}
