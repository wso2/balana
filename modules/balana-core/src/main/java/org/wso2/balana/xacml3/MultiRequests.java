/*
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.ParsingException;
import org.wso2.balana.XACMLConstants;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the MultiRequestsType XML type found in the context schema.
 */
public class MultiRequests {

    /**
     * <code>Set</code> of <code>RequestReference</code> that contains in <code>MultiRequests</code>
     */
    private Set<RequestReference> requestReferences;

    /**
     * Constructor that creates a new <code>MultiRequests</code> based on
     * the given elements.
     *
     * @param requestReferences <code>Set</code> of <code>RequestReference</code>
     */
    private MultiRequests(Set<RequestReference> requestReferences) {
        this.requestReferences = requestReferences;
    }

    /**
     * creates a <code>MultiRequests</code> based on its DOM node.
     * @param root  root the node to parse for the AttributeAssignment
     * @return  a new <code>MultiRequests</code> constructed by parsing
     * @throws ParsingException  if the DOM node is invalid
     */
    public static MultiRequests getInstance(Node root) throws ParsingException {

        Set<RequestReference> requestReferences = new HashSet<RequestReference>();

        // First check that we're really parsing an MultiRequests
        if (!DOMHelper.getLocalName(root).equals("MultiRequests")) {
            throw new ParsingException("MultiRequests object cannot be created "
                    + "with root node of type: " + DOMHelper.getLocalName(root));
        }

        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if ("RequestReference".equals(DOMHelper.getLocalName(node))){
                Set<AttributesReference> attributesReferences = new HashSet<AttributesReference>();
                RequestReference requestReference = new RequestReference();
                NodeList childNodes = node.getChildNodes();
                for(int j = 0; j < childNodes.getLength(); j++){
                    Node childNode = childNodes.item(j);
                    if("AttributesReference".equals(DOMHelper.getLocalName(childNode))){
                        AttributesReference attributesReference = new AttributesReference();
                        NamedNodeMap nodeAttributes = childNode.getAttributes();
                        try {
                            String referenceId = nodeAttributes.getNamedItem("ReferenceId").getNodeValue();
                            attributesReference.setId(referenceId);
                            attributesReferences.add(attributesReference);
                        } catch (Exception e) {
                            throw new ParsingException("Error parsing required ReferenceId in " +
                                    "AttributesReferenceType", e);
                        }
                    }
                }

                if(attributesReferences.isEmpty()){
                    throw new ParsingException("RequestReference must contain at least one " +
                            "AttributesReferenceType");
                }
                requestReference.setReferences(attributesReferences);
                requestReferences.add(requestReference);
            }
        }

        if(requestReferences.isEmpty()){
            throw new ParsingException("MultiRequests must contain at least one RequestReferenceType");
        }

        return new MultiRequests(requestReferences);
    }

    /**
     * returns <code>Set</code> of <code>RequestReference</code>
     *
     * @return <code>Set</code> of <code>RequestReference</code>
     */
    public Set<RequestReference> getRequestReferences() {
        return requestReferences;
    }
}
