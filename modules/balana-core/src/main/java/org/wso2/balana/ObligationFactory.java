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

package org.wso2.balana;

import org.w3c.dom.Node;
import org.wso2.balana.xacml3.ObligationExpression;

import java.util.HashMap;

/**
 *
 */
public class ObligationFactory {

    private HashMap<String, AbstractObligation> targetMap = new HashMap<String, AbstractObligation>();

    private static volatile ObligationFactory factoryInstance;

    public AbstractObligation getObligation(Node node, PolicyMetaData metaData) throws ParsingException {

        if(XACMLConstants.XACML_VERSION_3_0 == metaData.getXACMLVersion()){
            return ObligationExpression.getInstance(node, metaData);
        } else {
            return org.wso2.balana.xacml2.Obligation.getInstance(node);
        }

    }

    /**
     * Returns an instance of this factory. This method enforces a singleton model, meaning that
     * this always returns the same instance, creating the factory if it hasn't been requested
     * before.
    *
     * @return the factory instance
     */
    public static ObligationFactory getFactory() {
        if (factoryInstance == null) {
            synchronized (ObligationFactory.class) {
                if (factoryInstance == null) {
                    factoryInstance = new ObligationFactory();
                }
            }
        }

        return factoryInstance;
    }

}
