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
import org.wso2.balana.ParsingException;
import org.wso2.balana.PolicyMetaData;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents ObligationExpressionType in the XACML 3.0 policy schema
 */
public class ObligationExpressions {

    /**
     * <code>Set</code> of <code>ObligationExpression</code> that contains in
     * <code>ObligationExpressions</code>
     */
    Set<ObligationExpression> obligationExpressions;

    /**
     * Constructor that creates a new <code>ObligationExpressions</code> based on
     * the given elements.
     * 
     * @param obligationExpressions  <code>Set</code> of <code>ObligationExpression</code>
     */
    public ObligationExpressions(Set<ObligationExpression> obligationExpressions) {
        this.obligationExpressions = obligationExpressions;
    }


    /**
     * creates a <code>ObligationExpressions</code> based on its DOM node.
     *
     * @param root root the node to parse for the ObligationExpressions
     * @param metaData meta-date associated with the policy
     * @return  a new <code>ObligationExpressions</code> constructed by parsing
     * @throws ParsingException if the DOM node is invalid
     */
    public static ObligationExpressions getInstance(Node root, PolicyMetaData metaData) throws ParsingException {

        Set<ObligationExpression> obligationExpressions = new HashSet<ObligationExpression>();

        NodeList children = root.getChildNodes();

        for(int i = 0; i < children.getLength(); i ++){
            Node child = children.item(i);
            if("ObligationExpression".equals(DOMHelper.getLocalName(child))){
                obligationExpressions.add(ObligationExpression.getInstance(child, metaData));    
            }
        }

        if(obligationExpressions.isEmpty()){
            throw new ParsingException("ObligationExpressions must contain at least one " +
                    "ObligationExpression");            
        }

        return new ObligationExpressions(obligationExpressions);
    }

}
