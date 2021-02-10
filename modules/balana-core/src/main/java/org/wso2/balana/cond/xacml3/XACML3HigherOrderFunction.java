/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.balana.cond.xacml3;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;
import org.wso2.balana.cond.Function;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents all of the XACML3 higher order functions. (any-of, all-of and any-of-any)
 */
public class XACML3HigherOrderFunction implements Function {

    // Standard identifier for the any-of function.
    public static final String NAME_ANY_OF = FunctionBase.FUNCTION_NS_3 + "any-of";

    // Standard identifier for the all-of function.
    public static final String NAME_ALL_OF = FunctionBase.FUNCTION_NS_3 + "all-of";

    // Standard identifier for the any-of-any function.
    public static final String NAME_ANY_OF_ANY = FunctionBase.FUNCTION_NS_3 + "any-of-any";

    // Internal identifiers for each of the supported functions.
    private static final int ID_ANY_OF = 0;
    private static final int ID_ALL_OF = 1;
    private static final int ID_ANY_OF_ANY = 2;

    // Internal mapping of names to ids.
    private final static Map<String, Integer> ID_MAP;

    private int functionId;
    private URI identifier;
    private static URI returnTypeURI;
    private static RuntimeException earlyException;

    // Try to create the return type URI, and also setup the id map.
    static {
        try {
            returnTypeURI = new URI(BooleanAttribute.identifier);
        } catch (URISyntaxException e) {
            earlyException = new IllegalArgumentException(e);
        }

        Map<String, Integer> nameIdMap = new HashMap<>();
        nameIdMap.put(NAME_ANY_OF, ID_ANY_OF);
        nameIdMap.put(NAME_ALL_OF, ID_ALL_OF);
        nameIdMap.put(NAME_ANY_OF_ANY, ID_ANY_OF_ANY);
        ID_MAP = Collections.unmodifiableMap(nameIdMap);
    }

    /**
     * Creates a new instance of the given function.
     *
     * @param functionName the function to create
     * @throws IllegalArgumentException if the function is unknown
     */
    public XACML3HigherOrderFunction(String functionName) {

        // Try to get the function's identifier.
        Integer i = ID_MAP.get(functionName);
        if (i == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
        functionId = i;

        // Setup the URI form of this function's identity.
        try {
            identifier = new URI(functionName);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI", e);
        }
    }

    /**
     * Returns a <code>Set</code> containing all the function identifiers supported by this class.
     *
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set getSupportedIdentifiers() {

        return Collections.unmodifiableSet(ID_MAP.keySet());
    }

    @Override
    public void checkInputs(List inputs) throws IllegalArgumentException {

        Object[] list = inputs.toArray();

        // First, check that we got the right number of parameters.
        if (list.length < 2) {
            throw new IllegalArgumentException("requires more than two inputs");
        }

        // Try to cast the first element into a function.
        Function function;

        if (list[0] instanceof Function) {
            function = (Function) (list[0]);
        } else {
            throw new IllegalArgumentException("first arg to higher-order "
                    + " function must be a function");
        }

        // Check that the function returns a boolean
        if (!function.getReturnType().toString().equals(BooleanAttribute.identifier))
            throw new IllegalArgumentException("higher-order function must "
                    + "use a boolean function");

        // Separate the remaining inputs into primitive data types or bags of primitive types.
        List<Evaluatable> bagArgs = new ArrayList();
        List<Evaluatable> args = new ArrayList();
        for (int i = 1; i < list.length; i++) {
            Evaluatable eval = (Evaluatable) (list[i]);
            if (eval.returnsBag()) {
                bagArgs.add(eval);
            } else {
                args.add(eval);
            }
        }
        if (functionId == ID_ALL_OF || functionId == ID_ANY_OF) {
            // The n-1 parameters SHALL be values of primitive data-types and one SHALL be a bag of a primitive
            // data-type for any-of and all-of.
            if (bagArgs.size() != 1) {
                throw new IllegalArgumentException("Only one argument SHALL be a bag of a primitive data-type for " +
                        getIdentifier());
            }
            //  The expression SHALL be evaluated as if the function named in the <Function> argument were applied
            //  to the n-1 non-bag arguments and each element of the bag argument for any-of and all-of.
            for (Evaluatable arg : args) {
                List<Evaluatable> inputForCheck = new ArrayList();
                inputForCheck.add(arg);
                inputForCheck.addAll(bagArgs);
                function.checkInputsNoBag(inputForCheck);
            }
        } else {
            // The remaining arguments are either primitive data types or bags of primitive types.
            if (!args.isEmpty() && !bagArgs.isEmpty()) {
                throw new IllegalArgumentException("The arguments can be are either primitive data types or " +
                        "bags of primitive types. " + getIdentifier());
            }
            // The expression SHALL be evaluated as if the function named in the <Function> argument was applied
            // between every tuple of the cross product on all bags and the primitive values.
            if (!args.isEmpty()) {
                validateAnyOfAnyInput(args, function);
            } else {
                validateAnyOfAnyInput(bagArgs, function);
            }
        }
    }

    private void validateAnyOfAnyInput(List<Evaluatable> inputs, Function function) {

        for (int i = 0; i < inputs.size(); i++) {
            for (int j = i + 1; j < inputs.size(); j++) {
                List<Evaluatable> inputForCheck = new ArrayList();
                inputForCheck.add(inputs.get(i));
                inputForCheck.add(inputs.get(j));
                function.checkInputsNoBag(inputForCheck);
            }
        }
    }

    @Override
    public void checkInputsNoBag(List inputs) throws IllegalArgumentException {

        // This always throws an exception, since this function by definition must work on bags.
        throw new IllegalArgumentException("higher-order functions require use of bags");
    }

    @Override
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {

        Iterator iterator = inputs.iterator();

        // Get the first arg, which is the function.
        Expression xpr = (Expression) (iterator.next());
        Function function = null;

        if (xpr instanceof Function) {
            function = (Function) xpr;
        }

        // Separate the remaining inputs into primitive data types or bags of primitive types.
        List<AttributeValue> args = new ArrayList<>();
        List<BagAttribute> bagArgs = new ArrayList<>();

        while (iterator.hasNext()) {
            Evaluatable eval = (Evaluatable) (iterator.next());
            EvaluationResult result = eval.evaluate(context);
            if (result.indeterminate()) {
                return result;
            }
            if (result.getAttributeValue().returnsBag()) {
                bagArgs.add((BagAttribute) (result.getAttributeValue()));
            } else {
                args.add(result.getAttributeValue());
            }
        }

        switch (functionId) {
            case ID_ANY_OF:
                return anyAndAllHelper(context, function, args, bagArgs.get(0), false);

            case ID_ALL_OF:
                return anyAndAllHelper(context, function, args, bagArgs.get(0), true);

            case ID_ANY_OF_ANY:
                return anyOfAny(context, function, args, bagArgs);
        }
        return null;
    }

    private EvaluationResult anyAndAllHelper(EvaluationCtx context, Function function, List<AttributeValue> values,
                                             BagAttribute bag, boolean isAllFunction) {

        Iterator it = bag.iterator();
        while (it.hasNext()) {
            AttributeValue bagValue = (AttributeValue) (it.next());
            for (AttributeValue value : values) {
                EvaluationResult result = getEvaluationResult(context, function, value, bagValue, isAllFunction);
                if (result != null) {
                    return result;
                }
            }
        }
        return new EvaluationResult(BooleanAttribute.getInstance(isAllFunction));
    }

    private EvaluationResult anyOfAny(EvaluationCtx context, Function function, List<AttributeValue> args,
                                      List<BagAttribute> bagArgs) {

        // The expression SHALL be evaluated as if the function named in the <Function> argument was applied
        // between every tuple of the cross product on all bags and the primitive values, and the results were
        // combined using "urn:oasis:names:tc:xacml:1.0:function:or"

        EvaluationResult result = new EvaluationResult(BooleanAttribute.getInstance(false));
        if (!args.isEmpty()) {
            for (int i = 0; i < args.size() - 1; i++) {
                AttributeValue value = args.get(i);
                List<AttributeValue> bagValue = new ArrayList<>();
                bagValue.add(value);
                BagAttribute bagArg = new BagAttribute(value.getType(), bagValue);
                result = anyAndAllHelper(context, function, args.subList(i + 1, args.size()), bagArg, false);
                if (result.indeterminate() || ((BooleanAttribute) (result.getAttributeValue())).getValue()) {
                    return result;
                }
            }
            return new EvaluationResult(BooleanAttribute.getInstance(false));
        }
        if (!bagArgs.isEmpty()) {
            for (int i = 0; i < bagArgs.size(); i++) {
                for (int j = i + 1; j < bagArgs.size(); j++) {
                    Iterator iIterator = bagArgs.get(i).iterator();
                    while (iIterator.hasNext()) {
                        AttributeValue iValue = (AttributeValue) (iIterator.next());
                        Iterator jIterator = bagArgs.get(j).iterator();
                        while (jIterator.hasNext()) {
                            AttributeValue jValue = (AttributeValue) (jIterator.next());
                            result = getEvaluationResult(context, function, jValue, iValue, false);
                            if (result != null && (result.indeterminate() ||
                                    ((BooleanAttribute) (result.getAttributeValue())).getValue())) {
                                return result;
                            }
                        }
                    }
                }
            }
            return new EvaluationResult(BooleanAttribute.getInstance(false));
        }
        return null;
    }

    private EvaluationResult getEvaluationResult(EvaluationCtx context, Function function, AttributeValue val1,
                                                 AttributeValue val2, boolean isAllFunction) {

        List<Evaluatable> params = new ArrayList<>();
        params.add(val1);
        params.add(val2);
        EvaluationResult result = function.evaluate(params, context);

        if (result.indeterminate()) {
            return result;
        }

        BooleanAttribute bool = (BooleanAttribute) (result.getAttributeValue());
        if (bool.getValue() != isAllFunction) {
            return result;
        }
        return null;
    }

    @Override
    public URI getIdentifier() {

        return identifier;
    }

    @Override
    public URI getType() {

        return getReturnType();
    }

    @Override
    public URI getReturnType() {

        if (earlyException != null) {
            throw earlyException;
        }
        return returnTypeURI;
    }

    @Override
    public boolean returnsBag() {

        return false;
    }

    @Override
    public String encode() {

        StringBuilder builder = new StringBuilder();
        encode(builder);
        return builder.toString();
    }

    @Override
    public void encode(StringBuilder builder) {

        builder.append("<Function FunctionId=\"").append(getIdentifier().toString()).append("\"/>\n");
    }
}
