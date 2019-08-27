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

package org.wso2.balana.ctx.xacml2;

import org.wso2.balana.*;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.*;
import org.wso2.balana.finder.ResourceFinderResult;
import org.wso2.balana.xacml3.Attributes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.xacml3.MultipleCtxResult;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class XACML2EvaluationCtx extends BasicEvaluationCtx {


    private Set<Attributes> attributesSet;

    private int xacmlVersion;

    // the 4 maps that contain the attribute data
    private HashMap subjectMap;
    private HashMap resourceMap;
    private HashMap actionMap;
    private HashMap environmentMap;

    // the resource and its scope
    private AttributeValue resourceId;
    private int scope;
    
    private RequestCtx requestCtx;

    //private Set<ObligationResult>  obligationResults;

    //private Set<Advice>  advices;

    // the logger we'll use for all messages

    private static final Log logger = LogFactory.getLog(XACML2EvaluationCtx.class);

    public XACML2EvaluationCtx() {
        
    }

    public XACML2EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig) throws ParsingException {

        // keep track of the finder
        this.pdpConfig = pdpConfig;

        this.requestCtx = requestCtx;

        xacmlVersion = requestCtx.getXacmlVersion();

        // remember the root of the DOM tree for XPath queries
        requestRoot = requestCtx.getDocumentRoot();

        attributesSet = requestCtx.getAttributesSet();
        // initialize the cached date/time values so it's clear we haven't
        // retrieved them yet
        this.useCachedEnvValues = false;
        currentDate = null;
        currentTime = null;
        currentDateTime = null;

        // get the subjects, make sure they're correct, and setup tables
        subjectMap = new HashMap();
        setupSubjects(requestCtx.getSubjects());

        // next look at the Resource data, which needs to be handled specially
        resourceMap = new HashMap();
        setupResource(requestCtx.getResource());

        // setup the action data, which is generic
        actionMap = new HashMap();
        mapAttributes(requestCtx.getAction(), actionMap);

        // finally, set up the environment data, which is also generic
        environmentMap = new HashMap();
        mapAttributes(requestCtx.getEnvironmentAttributes(), environmentMap);

    }

    /**
     * This is quick helper function to provide a little structure for the subject attributes so we
     * can search for them (somewhat) quickly. The basic idea is to have a map indexed by
     * SubjectCategory that keeps Maps that in turn are indexed by id and keep the unique
     * ctx.Attribute objects.
     */
    private void setupSubjects(Set subjects)  {

        // now go through the subject attributes
        Iterator it = subjects.iterator();
        while (it.hasNext()) {
            Subject subject = (Subject) (it.next());

            URI category = subject.getCategory();
            Map categoryMap = null;

            // see if we've already got a map for the category
            if (subjectMap.containsKey(category)) {
                categoryMap = (Map) (subjectMap.get(category));
            } else {
                categoryMap = new HashMap();
                subjectMap.put(category, categoryMap);
            }

            // iterate over the set of attributes
            Iterator attrIterator = subject.getAttributes().iterator();

            while (attrIterator.hasNext()) {
                Attribute attr = (Attribute) (attrIterator.next());
                String id = attr.getId().toString();

                if (categoryMap.containsKey(id)) {
                    // add to the existing set of Attributes w/this id
                    Set existingIds = (Set) (categoryMap.get(id));
                    existingIds.add(attr);
                } else {
                    // this is the first Attr w/this id
                    HashSet newIds = new HashSet();
                    newIds.add(attr);
                    categoryMap.put(id, newIds);
                }
            }
        }
    }

    /**
     * This basically does the same thing that the other types need to do, except that we also look
     * for a resource-id attribute, not because we're going to use, but only to make sure that it's
     * actually there, and for the optional scope attribute, to see what the scope of the attribute
     * is
     */
    private void setupResource(Set resource) throws ParsingException {
        mapAttributes(resource, resourceMap);

        // make sure there resource-id attribute was included
        if (!resourceMap.containsKey(XACMLConstants.RESOURCE_ID)) {
            logger.error("Resource must contain resource-id attr");
            throw new ParsingException("resource missing resource-id");
        } else {
            // make sure there's only one value for this
            Set set = (Set) (resourceMap.get(XACMLConstants.RESOURCE_ID));
            if (set.size() > 1) {
                logger.error("Resource may contain only one resource-id Attribute");
                throw new ParsingException("too many resource-id attrs");
            } else {
                // keep track of the resource-id attribute
                resourceId = ((Attribute) (set.iterator().next())).getValue();
            }
        }

        // see if a resource-scope attribute was included
        if (resourceMap.containsKey(XACMLConstants.RESOURCE_SCOPE_1_0)) {
            Set set = (Set) (resourceMap.get(XACMLConstants.RESOURCE_SCOPE_1_0));

            // make sure there's only one value for resource-scope
            if (set.size() > 1) {
                logger.error("Resource may contain only one resource-scope Attribute");
                throw new ParsingException("too many resource-scope attrs");
            }

            Attribute attr = (Attribute) (set.iterator().next());
            AttributeValue attrValue = attr.getValue();

            // scope must be a string, so throw an exception otherwise
            if (!attrValue.getType().toString().equals(StringAttribute.identifier)) {
                logger.error("scope attr must be a string");
                throw new ParsingException("scope attr must be a string");
            }

            String value = ((StringAttribute) attrValue).getValue();

            if (value.equals("Immediate")) {
                scope = XACMLConstants.SCOPE_IMMEDIATE;
            } else if (value.equals("Children")) {
                scope = XACMLConstants.SCOPE_CHILDREN;
            } else if (value.equals("Descendants")) {
                scope = XACMLConstants.SCOPE_DESCENDANTS;
            } else {
                logger.error("Unknown scope type: " + value);
                throw new ParsingException("invalid scope type: " + value);
            }
        } else {
            // by default, the scope is always Immediate
            scope = XACMLConstants.SCOPE_IMMEDIATE;
        }
    }

    /**
     * Generic routine for resource, attribute and environment attributes to build the lookup map
     * for each. The Form is a Map that is indexed by the String form of the attribute ids, and that
     * contains Sets at each entry with all attributes that have that id
     */
    private void mapAttributes(Set input, Map output) {
        Iterator it = input.iterator();
        while (it.hasNext()) {
            Attribute attr = (Attribute) (it.next());
            String id = attr.getId().toString();

            if (output.containsKey(id)) {
                Set set = (Set) (output.get(id));
                set.add(attr);
            } else {
                Set set = new HashSet();
                set.add(attr);
                output.put(id, set);
            }
        }
    }

    /**
     * Returns the resource scope of the request, which will be one of the three fields denoting
     * Immediate, Children, or Descendants.
     *
     * @return the scope of the resource in the request
     */
    public int getScope() {
        return scope;
    }

    /**
     * Returns the resource named in the request as resource-id.
     *
     * @return the resource
     */
    public AttributeValue getResourceId() {
        return resourceId;
    }

    /**
     * Changes the value of the resource-id attribute in this context. This is useful when you have
     * multiple resources (ie, a scope other than IMMEDIATE), and you need to keep changing only the
     * resource-id to evaluate the different effective requests.
     *
     * @param resourceId the new resource-id value
     */
    public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet) {
        this.resourceId = resourceId;

        // there will always be exactly one value for this attribute
        Set attrSet = (Set) (resourceMap.get(XACMLConstants.RESOURCE_ID));
        Attribute attr = (Attribute) (attrSet.iterator().next());

        // remove the old value...
        attrSet.remove(attr);

        // ...and insert the new value
        attrSet.add(new Attribute(attr.getId(), attr.getIssuer(), attr.getIssueInstant(),
                resourceId,XACMLConstants.XACML_VERSION_2_0));
    }

    public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category) {

        if(XACMLConstants.SUBJECT_CATEGORY.equals(category.toString())){
            return getSubjectAttribute(type, id, category, issuer);
        } else if(XACMLConstants.RESOURCE_CATEGORY.equals(category.toString())){
            return getResourceAttribute(type, id, category, issuer);
        } else if(XACMLConstants.ACTION_CATEGORY.equals(category.toString())){
            return getActionAttribute(type, id, category, issuer);
        } else if(XACMLConstants.ENT_CATEGORY.equals(category.toString())){
            return getEnvironmentAttribute(type, id, category, issuer);
        } else {
            ArrayList<String> code = new ArrayList<String>();
            code.add(Status.STATUS_PROCESSING_ERROR);                                                                            ;
            Status status = new Status(code);
            return  new EvaluationResult(status);
        }
    }

    public int getXacmlVersion() {
        return xacmlVersion;
    }

    /**
     * Returns attribute value(s) from the subject section of the request.
     *
     * @param type     the type of the attribute value(s) to find
     * @param id       the id of the attribute value(s) to find
     * @param issuer   the issuer of the attribute value(s) to find or null
     * @param category the category the attribute value(s) must be in
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult getSubjectAttribute(URI type, URI id, URI category, String issuer) {
        // This is the same as the other three lookups except that this
        // has an extra level of indirection that needs to be handled first
        Map map = (Map) (subjectMap.get(category));

        if (map == null) {
            // the request didn't have that category, so we should try asking
            // the attribute finder
            return callHelper(type, id, issuer, category);
        }

        return getGenericAttributes(type, id, category, issuer, map);
    }

    /**
     * Returns attribute value(s) from the resource section of the request.
     *
     * @param type   the type of the attribute value(s) to find
     * @param id     the id of the attribute value(s) to find
     * @param issuer the issuer of the attribute value(s) to find or null
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult getResourceAttribute(URI type, URI id, URI category, String issuer) {
        return getGenericAttributes(type, id, category, issuer, resourceMap);
    }

    /**
     * Returns attribute value(s) from the action section of the request.
     *
     * @param type   the type of the attribute value(s) to find
     * @param id     the id of the attribute value(s) to find
     * @param issuer the issuer of the attribute value(s) to find or null
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult getActionAttribute(URI type, URI id, URI category, String issuer) {
        return getGenericAttributes(type, id, category, issuer, actionMap);
    }

    /**
     * Returns attribute value(s) from the environment section of the request.
     *
     * @param type   the type of the attribute value(s) to find
     * @param id     the id of the attribute value(s) to find
     * @param issuer the issuer of the attribute value(s) to find or null
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */
    public EvaluationResult getEnvironmentAttribute(URI type, URI id, URI category, String issuer) {
        return getGenericAttributes(type, id, category, issuer, environmentMap);
    }

    /**
     * Helper function for the resource, action and environment methods to get an attribute.
     */
    private EvaluationResult getGenericAttributes(URI type, URI id, URI category, String issuer,
                                                                                        Map map) {
        // try to find the id
        Set attrSet = (Set) (map.get(id.toString()));
        if (attrSet == null) {
            // the request didn't have an attribute with that id, so we should
            // try asking the attribute finder
            return callHelper(type, id, issuer, category);
        }

        // now go through each, considering each Attribute object
        List<AttributeValue> attributes = new ArrayList<AttributeValue>();
        Iterator it = attrSet.iterator();

        while (it.hasNext()) {
            Attribute attr = (Attribute) (it.next());

            // make sure the type and issuer are correct
            if ((attr.getType().equals(type))
                    && ((issuer == null) || ((attr.getIssuer() != null) && (attr.getIssuer()
                    .equals(issuer))))) {

                // if we got here, then we found a match, so we want to pull
                // out the values and put them in out list
                attributes.add(attr.getValue());
            }
        }

        // see if we found any acceptable attributes
        if (attributes.size() == 0) {
            // we failed to find any that matched the type/issuer, or all the
            // Attribute types were empty...so ask the finder
            if (logger.isDebugEnabled())
                logger.debug("Attribute not in request: " + id.toString()
                        + " ... querying AttributeFinder");

            return callHelper(type, id, issuer, category);
        }

        // if we got here, then we found at least one useful AttributeValue
        return new EvaluationResult(new BagAttribute(type, attributes));
    }


    public PDPConfig getPdpConfig() {
        return pdpConfig;
    }

    public AbstractRequestCtx getRequestCtx() {
        return requestCtx;
    }

    public MultipleCtxResult getMultipleEvaluationCtx() {

        Set<EvaluationCtx> evaluationCtxSet = new HashSet<EvaluationCtx>();

        if(scope != XACMLConstants.SCOPE_IMMEDIATE){
            MultipleCtxResult result = processHierarchicalAttributes(this);
            if(result.isIndeterminate()){
                return result;
            } else {
                evaluationCtxSet.addAll(result.getEvaluationCtxSet());
            }
        }
        
        if(evaluationCtxSet.size() > 0){
            return new MultipleCtxResult(evaluationCtxSet, null, false);
        } else {
            evaluationCtxSet.add(this);
            return new MultipleCtxResult(evaluationCtxSet, null, false);
        }
    }

    public int getResourceScope() {
        return scope;
    }

    private MultipleCtxResult processHierarchicalAttributes(XACML2EvaluationCtx evaluationCtx) {

        ResourceFinderResult resourceResult = null;
        Set<EvaluationCtx> children = new HashSet<EvaluationCtx>();
        AttributeValue resourceId = evaluationCtx.getResourceId();
        int resourceScope = evaluationCtx.getResourceScope();

        if(resourceId != null){
            if(resourceScope == XACMLConstants.SCOPE_CHILDREN){
                resourceResult = evaluationCtx.getPdpConfig().getResourceFinder().
                                                findChildResources(resourceId, evaluationCtx);
            } else if(resourceScope == XACMLConstants.SCOPE_DESCENDANTS) {
                resourceResult = evaluationCtx.getPdpConfig().getResourceFinder().
                                                findDescendantResources(resourceId, evaluationCtx);
            } else {
                logger.error("Unknown scope type: " );
                //TODO
            }
        } else {
             logger.error("ResourceId Attribute is NULL: " );
            // TODO
        }

        if(resourceResult == null || resourceResult.isEmpty()){
            logger.error("Resource Finder result is NULL: " );
            // TODO
        } else {
            for (AttributeValue resource : resourceResult.getResources()) {
                evaluationCtx.setResourceId(resource, attributesSet);
                children.add(evaluationCtx);
            }
        }

        return new MultipleCtxResult(children, null, false);

    }

}
