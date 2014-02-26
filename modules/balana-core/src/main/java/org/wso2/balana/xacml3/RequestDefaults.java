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

package org.wso2.balana.xacml3;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.Indenter;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *  Represents RequestDefaultsType XML type found in the context schema in XACML 3.0. 
 */
public class RequestDefaults {

    /**
     * XPath Version
     */
    private String xPathVersion;

    /**
     * Constructor that creates a new <code>RequestDefaults</code> based on
     * the given elements.
     *
     * @param xPathVersion  XPath version as <code>String</code>
     */
    public RequestDefaults(String xPathVersion) {
        this.xPathVersion = xPathVersion;
    }

    /**
     * creates a <code>RequestDefaults</code> based on its DOM node.
     *
     * @param root  root the node to parse for the RequestDefaults
     * @return  a new <code>RequestDefaults</code> constructed by parsing
     */
    public static RequestDefaults getInstance(Node root){

        String xPathVersion = null;

        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if ("XPathVersion".equals(DOMHelper.getLocalName(node))){
                xPathVersion = node.getFirstChild().getNodeValue();
            }
        }

        return new RequestDefaults(xPathVersion);

    }

    /**
     * returns XPath version
     *
     * @return XPath version as <code>String</code>
     */
    public String getXPathVersion() {
        return xPathVersion;
    }



    /**
     * Encodes this <code>RequestDefaults</code> into its XML representation and writes this encoding to the given
     * <code>OutputStream</code> with no indentation.
     *
     * @param output a stream into which the XML-encoded data is written
     */
    public void encode(OutputStream output) {
        encode(output, new Indenter(0));
    }

    /**
     * Encodes this <code>RequestDefaults</code> into its XML representation and writes this encoding to the given
     * <code>OutputStream</code> with indentation.
     *
     * @param output a stream into which the XML-encoded data is written
     * @param indenter an object that creates indentation strings
     */
    public void encode(OutputStream output, Indenter indenter) {

        String indent = indenter.makeString();
        PrintStream out = new PrintStream(output);

        out.println(indent + "<RequestDefaults>");

        if(xPathVersion != null){
            indenter.in();
            out.println(indent + "<XPathVersion>"  + xPathVersion + "</XPathVersion>");
            indenter.out();
        }
        
        out.println(indent + "</RequestDefaults>");
    }
}
