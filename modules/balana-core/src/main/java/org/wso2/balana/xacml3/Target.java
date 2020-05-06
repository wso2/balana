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
import org.wso2.balana.*;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the TargetType XML type in XACML 3.0. This extends the AbstractTarget.
 * This also stores several other XML type: AnyOf
 */
public class Target extends AbstractTarget {

    /**
     *  AnyOf sections of this target as list of <code>AnyOfSelection</code> 
     */
    List<AnyOfSelection> anyOfSelections;

    /**
     * Constructor that creates an XACML 3.0 <code>Target</code>
     *
     */
    public Target() {
        this.anyOfSelections = new ArrayList<AnyOfSelection>();
    }

    /**
     * Constructor that creates an XACML 3.0 <code>Target</code> from components.
     *
     * @param anyOfSelections List of <code>AnyOfSelection</code> objects that
     * representing the AnyOf sections of this target
     */
    public Target(List<AnyOfSelection> anyOfSelections) {
        this.anyOfSelections = anyOfSelections;
    }

    /**
     * Creates a <code>Target</code> by parsing a node.
     *
     * @param root the node to parse for the <code>Target</code>
     * @param metaData the meta-data associated with the policy
     * @return new <code>Target</code> constructed by parsing
     * @throws org.wso2.balana.ParsingException if the DOM node is invalid
     */
    public static Target getInstance(Node root, PolicyMetaData metaData)
                                                                throws ParsingException {

        List<AnyOfSelection> anyOfSelections = new ArrayList<AnyOfSelection>();
        NodeList children = root.getChildNodes();

        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if("AnyOf".equals(DOMHelper.getLocalName(child))){
                anyOfSelections.add(AnyOfSelection.getInstance(child, metaData));
            }
        }

        return new Target(anyOfSelections);
    }

    /**
     * Determines whether this <code>Target</code> matches the input request (whether it is
     * applicable).
     *
     * @param context the representation of the request
     *
     * @return the result of trying to match the target and the request
     */
    public MatchResult match(EvaluationCtx context) {

        Status firstIndeterminateStatus = null;

        for (AnyOfSelection anyOfSelection : anyOfSelections) {
            MatchResult result = anyOfSelection.match(context);
            if (result.getResult() == MatchResult.NO_MATCH){
                return result;
            } else if(result.getResult() == MatchResult.INDETERMINATE){
                if(firstIndeterminateStatus == null){
                    firstIndeterminateStatus = result.getStatus();    
                }
            }
        }

        if(firstIndeterminateStatus == null){
            return new MatchResult(MatchResult.MATCH);
        } else {
            return new MatchResult(MatchResult.INDETERMINATE,
                                   firstIndeterminateStatus);
        }
    }

    public List<AnyOfSelection> getAnyOfSelections() {
        return anyOfSelections;
    }

    @Override
    public String encode() {
        StringBuilder sb = new StringBuilder();
        encode(sb);
        return sb.toString();
    }

    @Override
    public void encode(StringBuilder builder) {

        builder.append("<Target>\n");

        if(anyOfSelections != null){
            for(AnyOfSelection anyOfSelection : anyOfSelections){
                anyOfSelection.encode(builder);
            }
        }

        builder.append("</Target>\n");
    }
}
