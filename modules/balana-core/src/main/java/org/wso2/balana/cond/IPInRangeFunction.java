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

package org.wso2.balana.cond;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.IPAddressAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

import java.net.InetAddress;
import java.util.List;

/**
 * IP range function developed for Balana.
 */
public class IPInRangeFunction extends FunctionBase {


    /**
     * The identifier for this function
     */
    public static final String NAME = "urn:org.wso2.balana:function:ip-in-range";


    /**
     * Default constructor.
     */
    public IPInRangeFunction() {
        super(NAME, 0, IPAddressAttribute.identifier, false, 3, BooleanAttribute.identifier, false);
    }

    /**
     * Evaluates the ip-in-range function, which takes three <code>IPAddressAttribute</code> values.
     * This function return true if the first value falls between the second and third values
     *
     * @param inputs a <code>List</code> of <code>Evaluatable</code> objects representing the
     *            arguments passed to the function
     * @param context the respresentation of the request
     *
     * @return an <code>EvaluationResult</code> containing true or false
     */
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {


        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);

        // check if any errors occured while resolving the inputs
        if (result != null)
            return result;

        // get the three ip values
        long ipAddressToTest = ipToLong(((IPAddressAttribute)argValues[0]).getAddress());
        long ipAddressMin = ipToLong(((IPAddressAttribute)argValues[1]).getAddress());
        long ipAddressMax = ipToLong(((IPAddressAttribute)argValues[2]).getAddress());

        if(ipAddressMin > ipAddressMax){
            long temp = ipAddressMax;
            ipAddressMax = ipAddressMin;
            ipAddressMin = temp;
        }

        // we're in the range if the middle is now between min and max ip address
        return EvaluationResult.getInstance(ipAddressToTest >= ipAddressMin && ipAddressToTest <= ipAddressMax);
    }


    /**
     * Helper method
     * @param ip
     * @return
     */
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }

}
