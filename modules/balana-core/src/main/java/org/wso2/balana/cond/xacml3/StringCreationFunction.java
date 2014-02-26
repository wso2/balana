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

import org.wso2.balana.attr.*;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

import java.util.*;

/**
 * String creation function that creates String from other data types
 */
public class StringCreationFunction extends FunctionBase {

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_BOOLEAN = FUNCTION_NS_3 + "string-from-boolean";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_DOUBLE= FUNCTION_NS_3 + "string-from-double";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_TIME = FUNCTION_NS_3 + "string-from-time";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_DATE_TIME = FUNCTION_NS_3 + "string-from-date";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_DATE= FUNCTION_NS_3 + "dateTime-from-string";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_INTEGER = FUNCTION_NS_3 + "string-from-integer";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_URI = FUNCTION_NS_3 + "string-from-anyURI";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_DAYTIME_DURATION = FUNCTION_NS_3 +
                                                                    "string-from-dayTimeDuration";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_YEAR_MONTH_DURATION = FUNCTION_NS_3 +
                                                                    "string-from-yearMonthDuration";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_X500NAME = FUNCTION_NS_3 + "string-from-x500Name";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_RFC822NAME = FUNCTION_NS_3 + "string-from-rfc822Name";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_DNS = FUNCTION_NS_3 + "string-from-dnsName";

    /**
     *  Standard identifier for the String-from-boolean function.
     */
    public static final String NAME_STRING_FROM_IP_ADDRESS = FUNCTION_NS_3 + "string-from-ipAddress";

    private static Map<String, String> dataTypeMap;

    /**
     * Static initializer sets up a map of standard function names to their associated argument
     * data types
     */
    static {

        dataTypeMap = new HashMap<String, String>();
        
        dataTypeMap.put(NAME_STRING_FROM_BOOLEAN, BooleanAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_INTEGER, IntegerAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_DOUBLE, DoubleAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_DATE, DateAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_TIME, TimeAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_DATE_TIME, DateTimeAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_DAYTIME_DURATION, DayTimeDurationAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_YEAR_MONTH_DURATION, YearMonthDurationAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_URI, AnyURIAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_X500NAME, X500NameAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_RFC822NAME, RFC822NameAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_IP_ADDRESS, IPAddressAttribute.identifier);
        dataTypeMap.put(NAME_STRING_FROM_DNS, DNSNameAttribute.identifier);

    }

    /**
     * Creates a new <code>EqualFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *            including the full namespace
     */
    public StringCreationFunction(String functionName) {
        super(functionName, 0, getArgumentType(functionName), false, 1, StringAttribute.identifier, false);
    }

    /**
     * Private helper that returns the parameter type used for the given standard function.
     *
     * @param functionName function name
     * @return identifier of the Data type
     */
    private static String getArgumentType(String functionName) {
        return dataTypeMap.get(functionName);
    }

    /**
     * Returns a <code>Set</code> containing all the function identifiers supported by this class.
     *
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set<String> getSupportedIdentifiers() {
        return Collections.unmodifiableSet(dataTypeMap.keySet());
    }

    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {
        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null){
            return result;
        }

        return new EvaluationResult(new StringAttribute(argValues[0].encode()));
    }

}
