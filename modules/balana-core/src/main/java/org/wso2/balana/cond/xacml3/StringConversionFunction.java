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

import org.wso2.balana.Balana;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.*;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * String conversion functions that creates different data-types from String type
 */
public class StringConversionFunction extends FunctionBase{

    /**
     *  Standard identifier for the boolean-from-string function.
     */
    public static final String NAME_BOOLEAN_FROM_STRING = FUNCTION_NS_3 + "boolean-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_INTEGER_FROM_STRING = FUNCTION_NS_3 + "integer-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_DOUBLE_FROM_STRING = FUNCTION_NS_3 + "double-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_TIME_FROM_STRING = FUNCTION_NS_3 + "time-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_DATE_FROM_STRING = FUNCTION_NS_3 + "date-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_DATE_TIME_FROM_STRING = FUNCTION_NS_3 + "dateTime-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_URI_FROM_STRING = FUNCTION_NS_3 + "anyURI-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_DAYTIME_DURATION_FROM_STRING = FUNCTION_NS_3 + "dayTimeDuration-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_YEAR_MONTH_DURATION_FROM_STRING = FUNCTION_NS_3 + "yearMonthDuration-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_X500NAME_FROM_STRING = FUNCTION_NS_3 + "x500Name-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_RFC822_FROM_STRING = FUNCTION_NS_3 + "rfc822Name-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_IP_ADDRESS_FROM_STRING = FUNCTION_NS_3 + "ipAddress-from-string";

    /**
     *  Standard identifier for the integer-from-boolean function.
     */
    public static final String NAME_DNS_FROM_STRING = FUNCTION_NS_3 + "dnsName-from-string";


    private static Map<String, String> dataTypeMap;

    /**
     * Static initializer sets up a map of standard function names to their associated return 
     * data types
     */
    static {

        dataTypeMap = new HashMap<String, String>();

        dataTypeMap.put(NAME_BOOLEAN_FROM_STRING, BooleanAttribute.identifier);
        dataTypeMap.put(NAME_INTEGER_FROM_STRING, IntegerAttribute.identifier);
        dataTypeMap.put(NAME_DOUBLE_FROM_STRING, DoubleAttribute.identifier);
        dataTypeMap.put(NAME_DATE_FROM_STRING, DateAttribute.identifier);
        dataTypeMap.put(NAME_TIME_FROM_STRING, TimeAttribute.identifier);
        dataTypeMap.put(NAME_DATE_TIME_FROM_STRING, DateTimeAttribute.identifier);
        dataTypeMap.put(NAME_DAYTIME_DURATION_FROM_STRING, DayTimeDurationAttribute.identifier);
        dataTypeMap.put(NAME_YEAR_MONTH_DURATION_FROM_STRING, YearMonthDurationAttribute.identifier);
        dataTypeMap.put(NAME_URI_FROM_STRING, AnyURIAttribute.identifier);
        dataTypeMap.put(NAME_X500NAME_FROM_STRING, X500NameAttribute.identifier);
        dataTypeMap.put(NAME_RFC822_FROM_STRING, RFC822NameAttribute.identifier);
        dataTypeMap.put(NAME_IP_ADDRESS_FROM_STRING, IPAddressAttribute.identifier);
        dataTypeMap.put(NAME_DNS_FROM_STRING, DNSNameAttribute.identifier);
    }

    /**
     * Creates a new <code>EqualFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *            including the full namespace
     */
    public StringConversionFunction(String functionName) {
        super(functionName, 0, StringAttribute.identifier, false, 1, getReturnType(functionName), false);
    }

    /**
     * Private helper that returns the return type used for the given standard function.
     *
     * @param functionName function name
     * @return identifier of the Data type
     */
    private static String getReturnType(String functionName) {
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

        try {
            URI dataType = new URI (dataTypeMap.get(getFunctionName()));
            AttributeValue value = Balana.getInstance().getAttributeFactory().createValue(dataType, 
                                                                            argValues[0].encode());
            return new EvaluationResult(value);
        } catch (URISyntaxException e) {
			List<String> code = new ArrayList<String>();
			code.add(Status.STATUS_PROCESSING_ERROR);
			return new EvaluationResult(new Status(code, e.getMessage()));
        } catch (ParsingException e) {
			List<String> code = new ArrayList<String>();
			code.add(Status.STATUS_PROCESSING_ERROR);
			return new EvaluationResult(new Status(code, e.getMessage()));
        } catch (UnknownIdentifierException e) {
			List<String> code = new ArrayList<String>();
			code.add(Status.STATUS_PROCESSING_ERROR);
			return new EvaluationResult(new Status(code, e.getMessage()));
        }
    }
}
