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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wso2.balana.Balana;
import org.wso2.balana.ParsingException;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Factory that creates the AbstractRequestCtx
 */
public class RequestCtxFactory {

    /**
     * Instance of this class
     */
    private static volatile RequestCtxFactory factoryInstance;

    /**
     *  the logger we'll use for all messages
     */
    private static final Log log = LogFactory.getLog(RequestCtxFactory.class);

    /**
     *  Returns instance of <code>AbstractRequestCtx</code> based one the XACML version.
     *
     * @param root  the node to parse for the <code>AbstractRequestCtx</code>
     * @return <code>AbstractRequestCtx</code> object
     * @throws org.wso2.balana.ParsingException  if the DOM node is invalid
     */
    public AbstractRequestCtx getRequestCtx(Node root) throws ParsingException {

        String requestCtxNs = root.getNamespaceURI();

        if(requestCtxNs != null){
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(requestCtxNs.trim())){
                return RequestCtx.getInstance(root);
            } else if(XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(requestCtxNs.trim()) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(requestCtxNs.trim())) {
                return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
            } else {
                throw new ParsingException("Invalid namespace in XACML request");
            }
        } else {
            log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
            return RequestCtx.getInstance(root);
        }
    }

    /**
     *  Returns instance of <code>AbstractRequestCtx</code> based one the XACML version.
     *
     * @param request  the String to parse for the <code>AbstractRequestCtx</code>
     * @return <code>AbstractRequestCtx</code> object
     * @throws ParsingException  if the request is invalid
     */
    public AbstractRequestCtx getRequestCtx(String request) throws ParsingException {

        Node root = getXacmlRequest(request);
        String requestCtxNs = root.getNamespaceURI();

        if(requestCtxNs != null){
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(requestCtxNs.trim())){
                return RequestCtx.getInstance(root);
            } else if(XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(requestCtxNs.trim()) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(requestCtxNs.trim())) {
                return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
            } else {
                throw new ParsingException("Invalid namespace in XACML request");
            }
        } else {
            log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
            return RequestCtx.getInstance(root);
        }
    }


    /**
     *  Returns instance of <code>AbstractRequestCtx</code> based one the XACML version.
     *
     * Creates a new <code>RequestCtx</code> by parsing XML from an input stream. Note that this a
     * convenience method, and it will not do schema validation by default. You should be parsing
     * the data yourself, and then providing the root node to the other <code>getInstance</code>
     * method. If you use this convenience method, you probably want to turn on validation by
     * setting the context schema file (see the programmer guide for more information on this).
     *
     * @param input input a stream providing the XML data
     * @return <code>AbstractRequestCtx</code> object
     * @throws ParsingException  if the DOM node is invalid
     */
    public AbstractRequestCtx getRequestCtx(InputStream input) throws ParsingException {

        Node root  = InputParser.parseInput(input, "Request");
        String requestCtxNs = root.getNamespaceURI();

        if(requestCtxNs != null){
            if(XACMLConstants.REQUEST_CONTEXT_3_0_IDENTIFIER.equals(requestCtxNs.trim())){
                return RequestCtx.getInstance(root);
            } else if(XACMLConstants.REQUEST_CONTEXT_1_0_IDENTIFIER.equals(requestCtxNs.trim()) ||
                    XACMLConstants.REQUEST_CONTEXT_2_0_IDENTIFIER.equals(requestCtxNs.trim())) {
                return org.wso2.balana.ctx.xacml2.RequestCtx.getInstance(root);
            } else {
                throw new ParsingException("Invalid namespace in XACML request");
            }
        } else {
            log.warn("No Namespace defined in XACML request and Assume as XACML 3.0");
            return RequestCtx.getInstance(root);
        }
    }


    /**
     * Returns an instance of this factory. This method enforces a singleton model, meaning that
     * this always returns the same instance, creating the factory if it hasn't been requested
     * before.
    *
     * @return the factory instance
     */
    public static RequestCtxFactory getFactory() {
        if (factoryInstance == null) {
            synchronized (RequestCtxFactory.class) {
                if (factoryInstance == null) {
                    factoryInstance = new RequestCtxFactory();
                }
            }
        }

        return factoryInstance;
    }


    /**
     * Creates DOM representation of the XACML request
     *
     * @param request  XACML request as a String object
     * @return  XACML request as a DOM element
     * @throws ParsingException throws, if fails
     */
    public Element getXacmlRequest(String request) throws ParsingException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
        DocumentBuilderFactory  builder = Utils.getSecuredDocumentBuilderFactory();
        if(builder == null){
            throw  new ParsingException("DOM Builder can not be null");
        }

        Document doc;
        try {
            doc = builder.newDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            throw new ParsingException("DOM of request element can not be created from String", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                    log.error("Error in closing input stream of XACML request");
            }
        }

        return doc.getDocumentElement();
    }
}
