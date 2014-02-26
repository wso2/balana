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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class that implements all the *-start-with functions. It takes two arguments of given
 * data-type and returns a <code>BooleanAttribute</code> data type.
 * The result shall be true if the second string begins with the first string, and false otherwise.
 */
public class StringComparingFunction extends FunctionBase {
    
    /**
     * Standard identifier for the string-starts-with function.
     */
    public static final String NAME_STRING_START_WITH = FUNCTION_NS_3 + "string-starts-with";

    /**
     * Standard identifier for the anyURI-starts-with function.
     */
    public static final String NAME_ANY_URI_START_WITH = FUNCTION_NS_3 + "anyURI-starts-with";

    /**
     * Standard identifier for the string-starts-with function.
     */
    public static final String NAME_STRING_ENDS_WITH = FUNCTION_NS_3 + "string-ends-with";

    /**
     * Standard identifier for the anyURI-starts-with function.
     */
    public static final String NAME_ANY_URI_ENDS_WITH = FUNCTION_NS_3 + "anyURI-ends-with";
        
    /**
     * Standard identifier for the string-starts-with function.
     */
    public static final String NAME_STRING_CONTAIN = FUNCTION_NS_3 + "string-contains";

    /**
     * Standard identifier for the anyURI-starts-with function.
     */
    public static final String NAME_ANY_URI_CONTAIN = FUNCTION_NS_3 + "anyURI-contains";


    // internal identifiers for each of the supported functions
    private static final int ID_STRING_START_WITH = 0;

    private static final int ID_ANY_URI_START_WITH = 1;

    private static final int ID_STRING_ENDS_WITH = 2;

    private static final int ID_ANY_URI_ENDS_WITH = 3;

    private static final int ID_STRING_CONTAIN = 4;

    private static final int ID_ANY_URI_CONTAIN = 5;

    /**
     * Creates a new <code>AddFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *            including the full namespace
     *
     * @throws IllegalArgumentException if the function is unknown
     */
    public StringComparingFunction(String functionName) {
        super(functionName, getId(functionName), getArgumentType(functionName), false, 2,
                BooleanAttribute.identifier, false);
    }

    /**
     * Private helper that returns the internal identifier used for the given standard function.
     * 
     * @param functionName function name
     * @return function id
     */
    private static int getId(String functionName) {
        if (functionName.equals(NAME_STRING_START_WITH)){
            return ID_STRING_START_WITH;
        } else if (functionName.equals(NAME_ANY_URI_START_WITH)){
            return ID_ANY_URI_START_WITH;
        } else if (functionName.equals(NAME_STRING_ENDS_WITH)){
            return ID_STRING_ENDS_WITH;
        } else if (functionName.equals(NAME_ANY_URI_ENDS_WITH)){
            return ID_ANY_URI_ENDS_WITH;
        } else if (functionName.equals(NAME_STRING_CONTAIN)){
            return ID_STRING_CONTAIN;
        } else if (functionName.equals(NAME_ANY_URI_CONTAIN)){
            return ID_ANY_URI_CONTAIN;
        } else {
            throw new IllegalArgumentException("unknown start-with function " + functionName);
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
        if (functionName.equals(NAME_STRING_START_WITH) || functionName.equals(NAME_STRING_ENDS_WITH)
                || functionName.equals(NAME_STRING_CONTAIN)){
            return IntegerAttribute.identifier;
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

        set.add(NAME_STRING_START_WITH);
        set.add(NAME_ANY_URI_START_WITH);
        set.add(NAME_STRING_ENDS_WITH);
        set.add(NAME_ANY_URI_ENDS_WITH);
        set.add(NAME_STRING_CONTAIN);
        set.add(NAME_ANY_URI_CONTAIN);

        return set;
    }


    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {

        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null) {
            return result;
        }

        int id = getFunctionId();

        if(id == ID_STRING_START_WITH || id == ID_ANY_URI_START_WITH){
            // do not want to check for anyURI and String data types. As both attribute values would
            // returns String data type after encode() is done,
            return EvaluationResult.getInstance(argValues[1].encode().
                                                            startsWith(argValues[0].encode()));
        } else if(id == ID_STRING_ENDS_WITH || id == ID_ANY_URI_ENDS_WITH){
            // do not want to check for anyURI and String data types. As both attribute values would
            // returns String data type after encode() is done,
            return EvaluationResult.getInstance(argValues[1].encode().
                                                            endsWith(argValues[0].encode()));
        } else {
            // do not want to check for anyURI and String data types. As both attribute values would
            // returns String data type after encode() is done,
            return EvaluationResult.getInstance(argValues[1].encode().
                                                            contains(argValues[0].encode()));
        }
    }
}
