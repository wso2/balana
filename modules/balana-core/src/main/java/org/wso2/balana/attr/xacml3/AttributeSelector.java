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

package org.wso2.balana.attr.xacml3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.attr.AbstractAttributeSelector;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Supports the standard selector functionality in XACML 3.0 version, which uses XPath expressions to resolve
 * values from the Request or elsewhere. All selector queries are done by
 * <code>AttributeFinderModule</code>s so that it's easy to plugin different XPath implementations.
 */
public class AttributeSelector extends AbstractAttributeSelector {

    /**
     * category of the select 
     */
    private URI category;

    /**
     * the XPath search for context node
     */
    private URI contextSelectorId;

    /**
     * the XPath to search fpr attributes
     */
    private String path;

    /**
     * the logger we'll use for all messages
     */
    private static final Log logger = LogFactory.getLog(AttributeSelector.class);



    /**
     * Creates a new <code>AttributeSelector</code>.
     *
     * @param category category of the attribute select
     * @param type the data type of the attribute values this selector looks for
     * @param contextSelectorId  XPath search for context node
     * @param path the XPath to query attribute
     * @param mustBePresent must resolution find a match
     * @param xpathVersion the XPath version to use, which must be a valid XPath version string (the
     *            identifier for XPath 1.0 is provided in <code>PolicyMetaData</code>)
     */
    public AttributeSelector(URI category, URI type, URI contextSelectorId, String path,
                                                    boolean mustBePresent, String xpathVersion) {
        this.category = category;
        this.type = type;
        this.contextSelectorId = contextSelectorId;
        this.mustBePresent = mustBePresent;
        this.xpathVersion = xpathVersion;
        this.path = path;
    }


    /**
     * Creates a new <code>AttributeSelector</code> based on the DOM root of the XML type. Note that
     * as of XACML 1.1 the XPathVersion element is required in any policy that uses a selector, so
     * if the <code>xpathVersion</code> string is null, then this will throw an exception.
     *
     * @param root the root of the DOM tree for the XML AttributeSelectorType XML type
     * @param metaData the meta-data associated with the containing policy
     *
     * @return an <code>AttributeSelector</code>
     *
     * @throws ParsingException if the AttributeSelectorType was invalid
     */
    public static AttributeSelector getInstance(Node root, PolicyMetaData metaData)
                                                                        throws ParsingException {
        URI category = null;
        URI type = null;
        URI contextSelectorId = null;
        String path = null;
        boolean mustBePresent = false;
        String xpathVersion = metaData.getXPathIdentifier();

        // make sure we were given an xpath version
        if (xpathVersion == null){
            throw new ParsingException("An XPathVersion is required for "
                    + "any policies that use selectors");
        }

        NamedNodeMap attrs = root.getAttributes();

        try {
            // there's always a DataType attribute
            category = new URI(attrs.getNamedItem("Category").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required Category "
                    + "attribute in AttributeSelector", e);
        }
        
        try {
            // there's always a DataType attribute
            type = new URI(attrs.getNamedItem("DataType").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required DataType "
                    + "attribute in AttributeSelector", e);
        }

        try {
            // there's always a RequestPath
            path = attrs.getNamedItem("Path").getNodeValue();
        } catch (Exception e) {
            throw new ParsingException("Error parsing required "
                    + "Path attribute in " + "AttributeSelector", e);
        }

        try {
            String stringValue = attrs.getNamedItem("MustBePresent").getNodeValue();
            mustBePresent = Boolean.parseBoolean(stringValue);
        } catch (Exception e) {
            throw new ParsingException("Error parsing required MustBePresent attribute "
                    + "in AttributeSelector", e);
        }

        try {
            Node node = attrs.getNamedItem("ContextSelectorId");
            if(node != null){
                contextSelectorId = new URI(node.getNodeValue());
            }
        } catch (Exception e) {
            throw new ParsingException("Error parsing required MustBePresent attribute "
                    + "in AttributeSelector", e);
        }

        return new AttributeSelector(category, type, contextSelectorId, path, mustBePresent,
                                                                                    xpathVersion);
    }


    /**
     * Invokes the <code>AttributeFinder</code> used by the given <code>EvaluationCtx</code> to try
     * to resolve an attribute value. If the selector is defined with MustBePresent as true, then
     * failure to find a matching value will result in Indeterminate, otherwise it will result in an
     * empty bag. To support the basic selector functionality defined in the XACML specification,
     * use a finder that has only the <code>SelectorModule</code> as a module that supports selector
     * finding.
     *
     * @param context representation of the request to search
     *
     * @return a result containing a bag either empty because no values were found or containing at
     *         least one value, or status associated with an Indeterminate result
     */

    public EvaluationResult evaluate(EvaluationCtx context) {
        // query the context
        EvaluationResult result = context.getAttribute(path, type, category,
                                                                contextSelectorId, xpathVersion);

        // see if we got anything
        if (!result.indeterminate()) {
            BagAttribute bag = (BagAttribute) (result.getAttributeValue());

            // see if it's an empty bag
            if (bag.isEmpty()) {
                // see if this is an error or not
                if (mustBePresent) {
                    // this is an error
                    if (logger.isDebugEnabled()) {
                        logger.debug("AttributeSelector failed to resolve a "
                                + "value for a required attribute: " + path);
                    }

                    ArrayList<String> code = new ArrayList<String>();
                    code.add(Status.STATUS_MISSING_ATTRIBUTE);

                    String message = "couldn't resolve XPath expression " + path
                            + " for type " + type.toString();
                    return new EvaluationResult(new Status(code, message));
                } else {
                    // return the empty bag
                    return result;
                }
            } else {
                // return the values
                return result;
            }
        } else {
            // return the error
            return result;
        }
    }

    public boolean evaluatesToBag() {
        return true;
    }

    public List getChildren() {
        return null;
    }



    public boolean returnsBag() {
        return true;
    }


    public void encode(StringBuilder builder) {

    }
}
