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

import org.w3c.dom.Node;
import org.wso2.balana.Indenter;
import org.wso2.balana.xacml3.Attributes;

import java.io.OutputStream;
import java.util.Set;

/**
 * Represents a XACML request made to the PDP. This is the class that contains all the data used to start
 * a policy evaluation.abstract class has been defined to give a unique interface for both XACML 2.0
 * and XACML 3.0 RequestCtx
 */
public abstract class AbstractRequestCtx {

    //XACML version of the request
    protected int xacmlVersion;

    // Hold onto the root of the document for XPath searches
    protected Node documentRoot = null;

    /**
     * XACML3 attributes as <code>Attributes</code> objects
     */
    protected Set<Attributes> attributesSet = null;

    protected boolean isSearch;

    /**
     * Returns the root DOM node of the document used to create this object, or null if this object
     * was created by hand (ie, not through the <code>getInstance</code> method) or if the root node
     * was not provided to the constructor.
     *
     * @return the root DOM node or null
     */
    public Node getDocumentRoot() {
        return documentRoot;
    }    

    public void setSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public int getXacmlVersion() {
        return xacmlVersion;
    }

    public void setXacmlVersion(int xacmlVersion) {
        this.xacmlVersion = xacmlVersion;
    }

    /**
     *  Returns a <code>Set</code> containing <code>Attribute</code> objects.
     *
     * @return  the request' s all attributes as <code>Set</code>
     */
    public Set<Attributes> getAttributesSet() {
        return attributesSet;
    }

    /**
     * Encodes this <code>AbstractRequestCtx</code> into its XML representation and writes this encoding to the given
     * <code>OutputStream</code> with indentation.
     *
     * @param output a stream into which the XML-encoded data is written
     * @param indenter an object that creates indentation strings
     */
    public abstract void encode(OutputStream output, Indenter indenter);

    /**
     * Encodes this <code>AbstractRequestCtx</code>  into its XML representation and writes this encoding to the given
     * <code>OutputStream</code>. No indentation is used.
     *
     * @param output a stream into which the XML-encoded data is written
     */
    public abstract void encode(OutputStream output);    

}
