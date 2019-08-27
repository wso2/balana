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

package org.wso2.balana.ctx.xacml3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.*;
import org.wso2.balana.attr.*;
import org.wso2.balana.attr.xacml3.XPathAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.*;
import org.wso2.balana.finder.ResourceFinderResult;
import org.wso2.balana.xacml3.*;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This is implementation of XACML3 evaluation context
 *
 */
public class XACML3EvaluationCtx extends BasicEvaluationCtx {

    /**
     * Attributes set of the request
     */
    private Set<Attributes> attributesSet;

    /**
     * multiple content selectors.
     */
    private Set<Attributes> multipleContentSelectors;

    /**
     * whether multiple attributes are present or not
     */
    private boolean multipleAttributes;

    /**
     * Set of policy references
     */
    private Set<PolicyReference> policyReferences;

    /**
     * attributes categorized as Map
     *
     * Category  --> Attributes Set
     */
    private Map<String, List<Attributes>> mapAttributes;

    /**
     * XACML3 request
     */
    private RequestCtx requestCtx;

    /**
     * XACML3 request scope. used with multiple resource profile
     */
    private Attribute resourceScopeAttribute;

    /**
     * XACML3 request scope. used with hierarchical resource
     */
    private int resourceScope;

    /**
     * XACML 3 request resource id.  used with hierarchical resource
     */
    private Attribute resourceId;

    /**
     * logger
     */
    private static final Log logger = LogFactory.getLog(XACML3EvaluationCtx.class);

    /**
     * Creates a new <code>XACML3EvaluationCtx</code>
     *
     * @param requestCtx  XACML3  RequestCtx
     * @param pdpConfig PDP configurations
     */
    public XACML3EvaluationCtx(RequestCtx requestCtx, PDPConfig pdpConfig) {

        // initialize the cached date/time values so it's clear we haven't
        // retrieved them yet
        currentDate = null;
        currentTime = null;
        currentDateTime = null;

        mapAttributes = new HashMap<String, List<Attributes>> ();

        attributesSet = requestCtx.getAttributesSet();
        this.pdpConfig = pdpConfig;
        this.requestCtx = requestCtx;

        setupAttributes(attributesSet, mapAttributes);
    }

    public EvaluationResult getAttribute(URI type, URI id, String issuer, URI category) {

        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
        List<Attributes> attributesSet = mapAttributes.get(category.toString());
        if(attributesSet != null && attributesSet.size() > 0){
            Set<Attribute> attributeSet  = attributesSet.get(0).getAttributes();
            for(Attribute attribute : attributeSet) {
                if(attribute.getId().toString().equals(id.toString())
                        && attribute.getType().toString().equals(type.toString())
                        && (issuer == null || issuer.equals(attribute.getIssuer()))
                        && attribute.getValue() != null){
                    List<AttributeValue> attributeValueList = attribute.getValues();
                    for (AttributeValue attributeVal : attributeValueList) {
                    	attributeValues.add(attributeVal);
                    }
                }
            }
        }
        if (attributeValues.isEmpty()) {
            return callHelper(type, id, issuer, category);
        }

        // if we got here, then we found at least one useful AttributeValue
        return new EvaluationResult(new BagAttribute(type, attributeValues));
    }


    public EvaluationResult getAttribute(String path, URI type, URI category,
                                         URI contextSelector, String xpathVersion){

        if(pdpConfig.getAttributeFinder() == null){

            logger.warn("Context tried to invoke AttributeFinder but was " +
                           "not configured with one");
            return new EvaluationResult(BagAttribute.createEmptyBag(type));
        }

        List<Attributes> attributesSet = null;

        if(category != null){
            attributesSet = mapAttributes.get(category.toString());
        }

        if(attributesSet != null && attributesSet.size() > 0){
            Attributes attributes  = attributesSet.get(0);
            Object content = attributes.getContent();
            if(content instanceof Node){
                Node root = (Node) content;
                if(contextSelector != null && contextSelector.toString().trim().length() > 0){
                    for(Attribute attribute : attributes.getAttributes()) {
                        if(attribute.getId().equals(contextSelector)){
                            List<AttributeValue> values = attribute.getValues();
                            for(AttributeValue value : values){
                                if(value instanceof XPathAttribute){
                                    XPathAttribute xPathAttribute = (XPathAttribute)value;
                                    if(xPathAttribute.getXPathCategory().
                                                                    equals(category.toString())){
                                        return pdpConfig.getAttributeFinder().findAttribute(path,
                                                            xPathAttribute.getValue(), type,
                                                            root, this, xpathVersion);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return pdpConfig.getAttributeFinder().findAttribute(path, null, type,
                                                         root, this, xpathVersion);
                }
            }
        }

        return new EvaluationResult(BagAttribute.createEmptyBag(type));
    }


    public int getXacmlVersion() {
        return requestCtx.getXacmlVersion();
    }


    /**
     * Generic routine for resource, attribute and environment attributes to build the lookup map
     * for each. The Form is a Map that is indexed by the String form of the category ids, and that
     * contains Sets at each entry with all attributes that have that id
     *
     * @param attributeSet
     * @param mapAttributes
     * @return
     */
    private void setupAttributes(Set<Attributes> attributeSet, Map<String,
                                            List<Attributes>> mapAttributes)  {
        for (Attributes attributes : attributeSet) {
            String category = attributes.getCategory().toString();
            for(Attribute attribute : attributes.getAttributes()){
                if(XACMLConstants.RESOURCE_CATEGORY.equals(category)){
                    if(XACMLConstants.RESOURCE_SCOPE_2_0.equals(attribute.getId().toString())){
                        resourceScopeAttribute = attribute;
                        AttributeValue value = attribute.getValue();
                        if (value instanceof StringAttribute) {
                            String scope = ((StringAttribute) value).getValue();
                            if (scope.equals("Children")) {
                                resourceScope = XACMLConstants.SCOPE_CHILDREN;
                            } else if (scope.equals("Descendants")) {
                                resourceScope = XACMLConstants.SCOPE_DESCENDANTS;
                            }
                        } else {
                            logger.error("scope attribute must be a string");     //TODO
                            //throw new ParsingException("scope attribute must be a string");
                        }
                    }

                    if (XACMLConstants.RESOURCE_ID.equals(attribute.getId().toString())){
                        if(resourceId == null) { //TODO  when there are more than one resource ids??
                            resourceId = attribute;
                        }
                    }
                }

                if(attribute.getId().toString().equals(XACMLConstants.MULTIPLE_CONTENT_SELECTOR)){
                    if(multipleContentSelectors == null){
                        multipleContentSelectors = new HashSet<Attributes>();
                    }
                    multipleContentSelectors.add(attributes);
                }
            }

            if (mapAttributes.containsKey(category)) {
                List<Attributes> set = mapAttributes.get(category);
                set.add(attributes);
                multipleAttributes = true;
             } else {
                List<Attributes> set = new ArrayList<Attributes>();
                set.add(attributes);
                mapAttributes.put(category, set);
            }
        }
    }

    public MultipleCtxResult getMultipleEvaluationCtx()  {

        Set<EvaluationCtx> evaluationCtxSet = new HashSet<EvaluationCtx>();
        MultiRequests multiRequests =  requestCtx.getMultiRequests();

        // 1st check whether there is a multi request attribute
        if(multiRequests != null){

            MultipleCtxResult result = processMultiRequestElement(this);
            if(result.isIndeterminate()){
                return result;
            } else {
                evaluationCtxSet.addAll(result.getEvaluationCtxSet());
            }
        }

        // 2nd check repeated values for category attribute
        if(evaluationCtxSet.size() > 0){
            Set<EvaluationCtx> newSet = new HashSet<EvaluationCtx>(evaluationCtxSet);
            for(EvaluationCtx evaluationCtx : newSet){
                if(((XACML3EvaluationCtx)evaluationCtx).isMultipleAttributes()){
                    evaluationCtxSet.remove(evaluationCtx);
                    MultipleCtxResult result = processMultipleAttributes((XACML3EvaluationCtx)evaluationCtx);
                    if(result.isIndeterminate()){
                        return result;
                    } else {
                        evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                    }
                }
            }
        } else {
            if(multipleAttributes){
                MultipleCtxResult result = processMultipleAttributes(this);
                if(result.isIndeterminate()){
                    return result;
                } else {
                    evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                }
            }
        }

        // 3rd check for both scope and multiple-content-selector attributes. Spec does not mention
        // which one to pick when both are present, there for high priority has given to scope
        // attribute
        if(evaluationCtxSet.size() > 0){
            Set<EvaluationCtx> newSet = new HashSet<EvaluationCtx>(evaluationCtxSet);
            for(EvaluationCtx evaluationCtx : newSet){
                if(((XACML3EvaluationCtx)evaluationCtx).getResourceScope() != XACMLConstants.SCOPE_IMMEDIATE){
                    evaluationCtxSet.remove(evaluationCtx);
                    MultipleCtxResult result = processHierarchicalAttributes((XACML3EvaluationCtx)evaluationCtx);
                    if(result.isIndeterminate()){
                        return result;
                    } else {
                        evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                    }
                } else if(((XACML3EvaluationCtx)evaluationCtx).getMultipleContentSelectors() != null){
                    MultipleCtxResult result = processMultipleContentSelectors((XACML3EvaluationCtx)evaluationCtx);
                    if(result.isIndeterminate()){
                        return result;
                    } else {
                        evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                    }
                }
            }
        } else {
            if(resourceScope != XACMLConstants.SCOPE_IMMEDIATE){
                MultipleCtxResult result = processHierarchicalAttributes(this);
                if(result.isIndeterminate()){
                    return result;
                } else {
                    evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                }
            } else if(multipleContentSelectors != null){
                MultipleCtxResult result = processMultipleContentSelectors(this);
                if(result.isIndeterminate()){
                    return result;
                } else {
                    evaluationCtxSet.addAll(result.getEvaluationCtxSet());
                }
            }
        }

        if(evaluationCtxSet.size() > 0){
            return new MultipleCtxResult(evaluationCtxSet);
        } else {
            evaluationCtxSet.add(this);
            return new MultipleCtxResult(evaluationCtxSet);
        }
    }

    /**
     * Process multi request element
     *
     * @param evaluationCtx <code>XACML3EvaluationCtx</code>
     * @return <code>MultipleCtxResult</code>
     */
    private MultipleCtxResult processMultiRequestElement(XACML3EvaluationCtx evaluationCtx)  {

        Set<EvaluationCtx> children = new HashSet<EvaluationCtx>();
        MultiRequests multiRequests =  requestCtx.getMultiRequests();

        if(multiRequests == null){
            return new MultipleCtxResult(children);
        }

        Set<RequestReference> requestReferences =  multiRequests.getRequestReferences();
        for(RequestReference reference :  requestReferences) {
            Set<AttributesReference>  attributesReferences = reference.getReferences();
            if(attributesReferences != null && attributesReferences.size() > 0){
                Set<Attributes> attributes = new HashSet<Attributes>();
                for(AttributesReference attributesReference : attributesReferences){
                    String referenceId = attributesReference.getId();
                    if(referenceId != null){
                        Attributes newAttributes = null;
                        for(Attributes attribute : evaluationCtx.getAttributesSet()){
                            // check equal with reference id
                            if(attribute.getId() != null && attribute.getId().equals(referenceId)){
                                newAttributes = attribute;
                            }
                        }
                        if(newAttributes != null){
                            attributes.add(newAttributes);
                        } else {
                            // This must be only for one result. But here it is used to create error for
                            List<String> code = new ArrayList<String>();
                            code.add(Status.STATUS_SYNTAX_ERROR);
                            return new MultipleCtxResult(new Status(code,
                                                            "Invalid reference to attributes"));
                        }
                    }
                }
                RequestCtx ctx = new RequestCtx(attributes, null);
                children.add(new XACML3EvaluationCtx(ctx, pdpConfig));
            }
        }

        return new MultipleCtxResult(children);
    }

    /**
     * Process multiple attributes with same category
     *
     * @param evaluationCtx <code>XACML3EvaluationCtx</code>
     * @return <code>MultipleCtxResult</code>
     */
    private MultipleCtxResult processMultipleAttributes(XACML3EvaluationCtx evaluationCtx) {

        Set<EvaluationCtx> children = new HashSet<EvaluationCtx>();

        Map<String, List<Attributes>> mapAttributes = evaluationCtx.getMapAttributes();

        Set<Set<Attributes>> tempRequestAttributes =
                    new HashSet<Set<Attributes>>(Arrays.asList(evaluationCtx.getAttributesSet()));

        for(Map.Entry<String, List<Attributes>> mapAttributesEntry : mapAttributes.entrySet()){
            if(mapAttributesEntry.getValue().size() > 1){
                Set<Set<Attributes>> temp = new HashSet<Set<Attributes>>();
                for(Attributes attributesElement :  mapAttributesEntry.getValue()){
                    for(Set<Attributes> tempRequestAttribute : tempRequestAttributes){
                        Set<Attributes> newSet = new HashSet<Attributes>(tempRequestAttribute);
                        newSet.removeAll(mapAttributesEntry.getValue());
                        newSet.add(attributesElement);
                        temp.add(newSet);
                    }
                }
                tempRequestAttributes = temp;
            }
        }

        for(Set<Attributes> ctx : tempRequestAttributes){
            RequestCtx requestCtx = new RequestCtx(ctx, null);
            children.add(new XACML3EvaluationCtx(requestCtx, pdpConfig));
        }

        return new MultipleCtxResult(children);
    }


    /**
     *
     * @param evaluationCtx
     * @return
     */
    private MultipleCtxResult processHierarchicalAttributes(XACML3EvaluationCtx evaluationCtx) {

        ResourceFinderResult resourceResult = null;
        Set<EvaluationCtx> children = new HashSet<EvaluationCtx>();

        Attribute resourceId = evaluationCtx.getResourceId();
        if(resourceId != null){

            if(evaluationCtx.getResourceScope() == XACMLConstants.SCOPE_CHILDREN){
                resourceResult = pdpConfig.getResourceFinder().
                                                findChildResources(resourceId.getValue(), evaluationCtx);
            } else if(evaluationCtx.getResourceScope()  == XACMLConstants.SCOPE_DESCENDANTS) {
                resourceResult = pdpConfig.getResourceFinder().
                                                findDescendantResources(resourceId.getValue(), evaluationCtx);
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
                Set<Attributes> newSet = new HashSet<Attributes>(evaluationCtx.getAttributesSet());
                Attributes resourceAttributes = null;
                for(Attributes attributes : newSet){
                    if(XACMLConstants.RESOURCE_CATEGORY.equals(attributes.getCategory().toString())){
                        Set<Attribute> attributeSet = new HashSet<Attribute>(attributes.getAttributes());
                        attributeSet.remove(resourceScopeAttribute);
                        attributeSet.remove(resourceId);
                        try{
                            Attribute attribute = new Attribute(new URI(XACMLConstants.RESOURCE_ID),
                                    resourceId.getIssuer(), null, resource, resourceId.isIncludeInResult(),
                                    XACMLConstants.XACML_VERSION_3_0);
                            attributeSet.add(attribute);
                            Attributes newAttributes = new Attributes(new URI(XACMLConstants.RESOURCE_CATEGORY),
                                        (Node)attributes.getContent(), attributeSet, attributes.getId());
                            newSet.add(newAttributes);
                            resourceAttributes = attributes;
                        } catch (URISyntaxException e) {
                            //ignore
                        }
                        break;
                    }
                }
                if(resourceAttributes != null){
                    newSet.remove(resourceAttributes);
                    children.add(new XACML3EvaluationCtx(new RequestCtx(newSet, null), pdpConfig));
                }
            }
        }

        return new MultipleCtxResult(children);

    }

    /**
     *
     * @param evaluationCtx
     * @return
     */
    private MultipleCtxResult processMultipleContentSelectors(XACML3EvaluationCtx evaluationCtx) {

        Set<EvaluationCtx> children = new HashSet<EvaluationCtx>();
        Set<Attributes> newAttributesSet = new HashSet<Attributes>();

        for(Attributes attributes : evaluationCtx.getMultipleContentSelectors()){
            Set<Attribute> newAttributes = null;
            Attribute oldAttribute = null;
            Object content = attributes.getContent();
            if(content != null && content instanceof Node){
                Node root = (Node) content;
                for(Attribute attribute : attributes.getAttributes()){
                    oldAttribute = attribute;
                    if(attribute.getId().toString().equals(XACMLConstants.MULTIPLE_CONTENT_SELECTOR)){
                        List<AttributeValue> values = attribute.getValues();
                        for(AttributeValue value : values){
                            if(value instanceof XPathAttribute){
                                XPathAttribute xPathAttribute = (XPathAttribute)value;
                                if(xPathAttribute.getXPathCategory().
                                                    equals(attributes.getCategory().toString())){
                                    Set<String> xPaths = getChildXPaths(root, xPathAttribute.getValue());
                                    for(String xPath : xPaths){
                                        try {
                                            AttributeValue newValue = Balana.getInstance().getAttributeFactory().
                                                createValue(value.getType(), xPath,
                                                new String[] {xPathAttribute.getXPathCategory()});
                                            Attribute newAttribute =
                                                new Attribute(new URI(XACMLConstants.CONTENT_SELECTOR),
                                                attribute.getIssuer(), attribute.getIssueInstant(),
                                                newValue, attribute.isIncludeInResult(),
                                                XACMLConstants.XACML_VERSION_3_0);
                                            if(newAttributes == null){
                                                newAttributes = new HashSet<Attribute>();
                                            }
                                            newAttributes.add(newAttribute);
                                        } catch (Exception e) {
                                            logger.error(e);  // TODO
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(newAttributes != null){
                    attributes.getAttributes().remove(oldAttribute);
                    for(Attribute attribute : newAttributes){
                        Set<Attribute> set = new HashSet<Attribute>(attributes.getAttributes());
                        set.add(attribute);
                        Attributes attr = new Attributes(attributes.getCategory(),
                                                attributes.getContent(), set, attributes.getId());
                        newAttributesSet.add(attr);
                    }
                    evaluationCtx.getAttributesSet().remove(attributes);
                }
            }
        }

        for(Attributes attributes : newAttributesSet){
            Set<Attributes> set = new HashSet<Attributes>(evaluationCtx.getAttributesSet());
            set.add(attributes);
            RequestCtx requestCtx = new RequestCtx(set, null);
            children.add(new XACML3EvaluationCtx(requestCtx, pdpConfig));
        }

        return new MultipleCtxResult(children);

    }

    /**
     * Changes the value of the resource-id attribute in this context. This is useful when you have
     * multiple resources (ie, a scope other than IMMEDIATE), and you need to keep changing only the
     * resource-id to evaluate the different effective requests.
     *
     * @param resourceId  resourceId the new resource-id value
     * @param attributesSet a <code>Set</code> of <code>Attributes</code>
     */
    public void setResourceId(AttributeValue resourceId, Set<Attributes> attributesSet) {

        for(Attributes attributes : attributesSet){
            if(XACMLConstants.RESOURCE_CATEGORY.equals(attributes.getCategory().toString())){
                Set<Attribute> attributeSet = attributes.getAttributes();
                Set<Attribute> newSet = new HashSet<Attribute>(attributeSet);
                Attribute resourceIdAttribute = null;

                for (Attribute attribute : newSet){
                    if(XACMLConstants.RESOURCE_ID.equals(attribute.getId().toString())){
                        resourceIdAttribute = attribute;
                        attributeSet.remove(attribute);
                    } else if(XACMLConstants.RESOURCE_SCOPE_2_0.equals(attribute.getId().toString())){
                        attributeSet.remove(attribute);
                    }
                }

                if(resourceIdAttribute != null) {
                    attributeSet.add(new Attribute(resourceIdAttribute.getId(), resourceIdAttribute.getIssuer(),
                    resourceIdAttribute.getIssueInstant(), resourceId, resourceIdAttribute.isIncludeInResult(),
                                                                XACMLConstants.XACML_VERSION_3_0));
                }
                break;
            }

        }
    }

    private Set<String> getChildXPaths(Node root, String xPath){

        Set<String> xPaths = new HashSet<String>();
        NamespaceContext namespaceContext = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        if(namespaceContext == null){

            //see if the request root is in a namespace
            String namespace = null;
            if(root != null){
                namespace = root.getNamespaceURI();
            }
            // name spaces are used, so we need to lookup the correct
            // prefix to use in the search string
            NamedNodeMap namedNodeMap = root.getAttributes();

            Map<String, String> nsMap = new HashMap<String, String>();
            if(namedNodeMap != null){
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node n = namedNodeMap.item(i);
                    // we found the matching namespace, so get the prefix
                    // and then break out
                    String prefix = DOMHelper.getLocalName(n);
                    String nodeValue= n.getNodeValue();
                    nsMap.put(prefix, nodeValue);
                }
            }

            // if there is not any namespace is defined for content element, default XACML request
            //  name space would be there.
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(namespace) ||
                    XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(namespace)){
                nsMap.put("xacml", namespace);
            }

            namespaceContext = new DefaultNamespaceContext(nsMap);
        }

        xpath.setNamespaceContext(namespaceContext);


        try {
            XPathExpression expression = xpath.compile(xPath);
            NodeList matches = (NodeList) expression.evaluate(root, XPathConstants.NODESET);
            if(matches != null && matches.getLength() > 0){

                for (int i = 0; i < matches.getLength(); i++) {
                    String text = null;
                    Node node = matches.item(i);
                    short nodeType = node.getNodeType();

                    // see if this is straight text, or a node with data under
                    // it and then get the values accordingly
                    if ((nodeType == Node.CDATA_SECTION_NODE) || (nodeType == Node.COMMENT_NODE)
                            || (nodeType == Node.TEXT_NODE) || (nodeType == Node.ATTRIBUTE_NODE)) {
                        // there is no child to this node
                        text = node.getNodeValue();
                    } else {

                        // the data is in a child node
                        text = "/" + DOMHelper.getLocalName(node);
                    }
                    String newXPath = '(' + xPath + ")[" + (i+1) + ']';
                    xPaths.add(newXPath);
                }
            }
        } catch (Exception e) {
            // TODO
        }

        return xPaths;
    }

    public boolean isMultipleAttributes() {
        return multipleAttributes;
    }

    public AbstractRequestCtx getRequestCtx() {
        return requestCtx;
    }

    /**
     *
     * @return
     */
    public Set<PolicyReference> getPolicyReferences() {
        return policyReferences;
    }

    /**
     *
     * @param policyReferences
     */
    public void setPolicyReferences(Set<PolicyReference> policyReferences) {
        this.policyReferences = policyReferences;
    }

    /**
     *
     * @param category
     * @return
     */
    public List<Attributes> getAttributes(String category){
        return mapAttributes.get(category);
    }

    public Set<Attributes> getMultipleContentSelectors() {
        return multipleContentSelectors;
    }

    public Map<String, List<Attributes>> getMapAttributes() {
        return mapAttributes;
    }

    public Set<Attributes> getAttributesSet() {
        return attributesSet;
    }

    public Attribute getResourceId() {
        return resourceId;
    }

    public int getResourceScope() {
        return resourceScope;
    }

    public Attribute getResourceScopeAttribute() {
        return resourceScopeAttribute;
    }
}
