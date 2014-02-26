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
package org.wso2.balana.cond.xacml3;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.DefaultNamespaceContext;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.attr.xacml3.XPathAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx;
import org.wso2.balana.xacml3.Attributes;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.util.*;

/**
 * The class that implement all XPath based functions. An XPath expression evaluates to a node-set,
 * which is a set of XML nodes that match the expression. All comparison or other operations on
 * node-sets are performed in isolation of the particular function specified.
 */
public class XPathFunction extends FunctionBase {

    /**
     * Standard identifier for the xpath-node-count function.
     */
    public static final String NAME_XPATH_NODE_COUNT = FUNCTION_NS_3 + "xpath-node-count";

    /**
     * Standard identifier for the xpath-node-match function.
     */
    public static final String NAME_XPATH_NODE_MATCH = FUNCTION_NS_3 + "xpath-node-match";

    /**
     * Standard identifier for the xpath-node-equal function.
     */
    public static final String NAME_XPATH_NODE_EQUAL = FUNCTION_NS_3 + "xpath-node-equal";


    /**
     * private identifiers for xpath-node-count function
     */
	private static final int ID_XPATH_NODE_COUNT = 0;

    /**
     * private identifiers for xpath-node-match function.
     */
	private static final int ID_XPATH_NODE_MATCH = 1;

    /**
     * private identifiers for xpath-node-equal function.
     */
	private static final int ID_XPATH_NODE_EQUAL = 2;


    /**
     * Creates a new <code>StringFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *            including the full namespace
     *
     * @throws IllegalArgumentException if the function is unknown
     */
    public XPathFunction(String functionName) {
        super(functionName, getId(functionName), XPathAttribute.identifier, false,
                                getNumArgs(functionName), getReturnType(functionName), false);
    }

    /**
     * Private helper that returns the internal identifier used for the given standard function.
     *
     * @param functionName function name
     * @return function id
     */
    private static int getId(String functionName) {
        
        if (functionName.equals(NAME_XPATH_NODE_COUNT)){
            return ID_XPATH_NODE_COUNT;
        } else if (functionName.equals(NAME_XPATH_NODE_EQUAL)){
            return ID_XPATH_NODE_EQUAL;
        } else if (functionName.equals(NAME_XPATH_NODE_MATCH)){
            return ID_XPATH_NODE_MATCH;            
        } else {
            throw new IllegalArgumentException("unknown divide function " + functionName);
        }
    }

    /**
     * Private helper that returns the return type for the given standard function. Note that this
     * doesn't check on the return value since the method always is called after getId, so we assume
     * that the function is present.
     *
     * @param functionName function name
     * @return identifier of the Data type
     */
    private static String getReturnType(String functionName) {
        
        if (functionName.equals(NAME_XPATH_NODE_COUNT)){
            return IntegerAttribute.identifier;
        } else {
            return BooleanAttribute.identifier;
        }
    }

    /**
     * Private helper that returns the argument count for the given standard function. Note that
     * this doesn't check on the return value since the method always is called after getId, so we
     * assume that the function is present.
     *
     * @param functionName function name
     * @return identifier of the Data type
     */
    private static int getNumArgs(String functionName) {

        if (functionName.equals(NAME_XPATH_NODE_COUNT)){
            return 1;
        } else {
            return 2;
        }
    }


    /**
     * Returns a <code>Set</code> containing all the function identifiers supported by this class.
     *
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set<String> getSupportedIdentifiers() {

        Set<String> set = new HashSet<String>();
        set.add(NAME_XPATH_NODE_COUNT);
        set.add(NAME_XPATH_NODE_EQUAL);
        set.add(NAME_XPATH_NODE_MATCH);
        return set;
    }

    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {

        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null) {
            return result;
        }

		switch (getFunctionId()) {

            case ID_XPATH_NODE_COUNT: {

                XPathAttribute xpathAttribute = ((XPathAttribute) argValues[0]);
                String xpathValue = xpathAttribute.getValue();
                String category = xpathAttribute.getXPathCategory();

                Node contextNode = null;

                // this must be XACML 3
                List<Attributes> attributesSet = ((XACML3EvaluationCtx) context).getAttributes(category);
                if(attributesSet != null && attributesSet.size() > 0){
                    // only one attributes can be there
                    Attributes attributes = attributesSet.get(0);
                    contextNode = attributes.getContent();
                }

                if(contextNode != null){
                    // now apply XPath
                    try {
                        NodeList nodeList = getXPathResults(contextNode, xpathValue);
                        return new EvaluationResult(new IntegerAttribute(nodeList.getLength()));
                    } catch (XPathExpressionException e) {
                        List<String> codes = new ArrayList<String>();
                        codes.add(Status.STATUS_SYNTAX_ERROR);
                        Status status = new Status(codes, e.getMessage());
                        return new EvaluationResult(status);
                    }
                }
            }

            case ID_XPATH_NODE_EQUAL :{
                    //TODO
            }

        }

        List<String> codes = new ArrayList<String>();
        codes.add(Status.STATUS_SYNTAX_ERROR);
        Status status = new Status(codes, "Not supported function");
        return new EvaluationResult(status);
    }


    /**
     * Gets Xpath results
     *
     * @param contextNode
     * @param xpathValue
     * @return
     * @throws XPathExpressionException
     */
    private NodeList getXPathResults(Node contextNode, String xpathValue)
                                                                throws XPathExpressionException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        //see if the request root is in a namespace
        String namespace = contextNode.getNamespaceURI();
        // name spaces are used, so we need to lookup the correct
        // prefix to use in the search string
        NamedNodeMap namedNodeMap = contextNode.getAttributes();

        Map<String, String> nsMap = new HashMap<String, String>();

        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node n = namedNodeMap.item(i);
            // we found the matching namespace, so get the prefix
            // and then break out
            String prefix = DOMHelper.getLocalName(n);
            String nodeValue= n.getNodeValue();
            nsMap.put(prefix, nodeValue);
        }

        // if there is not any namespace is defined for content element, default XACML request
        //  name space would be there.
        if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(namespace) ||
                XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(namespace) ||
                XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(namespace)){
            nsMap.put("xacml", namespace);
        }

        NamespaceContext namespaceContext = new DefaultNamespaceContext(nsMap);
        xpath.setNamespaceContext(namespaceContext);

        XPathExpression expression = xpath.compile(xpathValue);
        return (NodeList) expression.evaluate(contextNode, XPathConstants.NODESET);
    }
}
