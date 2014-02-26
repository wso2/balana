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

import org.wso2.balana.attr.AnyURIAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sub string functions .
 */
public class SubStringFunction extends FunctionBase {

	/**
	 * Standard identifier for the string-sub string function.
	 */
	public static final String NAME_STRING_SUB_STRING = FUNCTION_NS_3 + "string-substring";

    /**
     * Standard identifier for the any uri sub string function.
     */
	public static final String NAME_ANY_URI_SUB_STRING = FUNCTION_NS_3 + "anyURI-substring";

    /**
     * private identifiers for the supported functions
     */
	private static final int ID_STRING_SUB_STRING = 0;

    /**
     * private identifiers for the supported functions
     */
	private static final int ID_ANY_URI_SUB_STRING = 1;

	/**
	 * Creates a new <code>StringFunction</code> object.
	 *
	 * @param functionName the standard XACML name of the function to be handled by this object,
	 *            including the full namespace
	 *
	 * @throws IllegalArgumentException if the function is unknown
	 */
	public SubStringFunction(String functionName) {
		super(functionName, getId(functionName), getArgumentType(functionName), false, 3,
				StringAttribute.identifier, false);
	}

    /**
     * Private helper that returns the internal identifier used for the given standard function.
     * 
     * @param functionName function name
     * @return function id
     */
    private static int getId(String functionName) {
        if (functionName.equals(NAME_STRING_SUB_STRING)){
            return ID_STRING_SUB_STRING;
        } else if (functionName.equals(NAME_ANY_URI_SUB_STRING)){
            return ID_ANY_URI_SUB_STRING;
        } else {
            throw new IllegalArgumentException("unknown divide function " + functionName);
        }
    }

    /**
     * Private helper that returns the type used for the given standard function. Note that this
     * doesn't check on the return value since the method always is called after getId, so we assume
     * that the function is present.
     * 
     * @param functionName function name
     * @return identifier of the Data type
     */
    private static String getArgumentType(String functionName) {
        if (functionName.equals(NAME_STRING_SUB_STRING)){
            return StringAttribute.identifier;
        } else {
            return AnyURIAttribute.identifier;
        }
    }

	/**
	 * Returns a <code>Set</code> containing all the function identifiers supported by this class.
	 *
	 * @return a <code>Set</code> of <code>String</code>s
	 */
	public static Set<String> getSupportedIdentifiers() {

		Set<String> set = new HashSet<String>();
		set.add(NAME_STRING_SUB_STRING);
		set.add(NAME_ANY_URI_SUB_STRING);
		return set;
	}


    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {

        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null) {
            return result;
        }

        String processedString = argValues[0].encode().substring(Integer.parseInt(argValues[1].encode()),
                                                        Integer.parseInt(argValues[2].encode()));

        return new EvaluationResult(new StringAttribute(processedString));
    }
}                                                                                                
