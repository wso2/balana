/*
 * @(#)EqualFunction.java
 *
 * Copyright 2003-2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */

package org.wso2.balana.cond;

import org.wso2.balana.ctx.EvaluationCtx;

import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AnyURIAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.Base64BinaryAttribute;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.DayTimeDurationAttribute;
import org.wso2.balana.attr.DNSNameAttribute;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.HexBinaryAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.attr.IPAddressAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.attr.TimeAttribute;
import org.wso2.balana.attr.YearMonthDurationAttribute;
import org.wso2.balana.attr.X500NameAttribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A class that implements all the *-equal functions. It takes two operands of the appropriate type
 * and returns a <code>BooleanAttribute</code> indicating whether both of the operands are equal. If
 * either of the operands is indeterminate, an indeterminate result is returned.
 *
 * @author Steve Hanna
 * @author Seth Proctor
 * @since 1.0
 */
public class EqualFunction extends FunctionBase {

    /**
     * Standard identifier for the string-equal function.
     */
    public static final String NAME_STRING_EQUAL = FUNCTION_NS + "string-equal";

    /**
     * Standard identifier for the boolean-equal function.
     */
    public static final String NAME_BOOLEAN_EQUAL = FUNCTION_NS + "boolean-equal";

    /**
     * Standard identifier for the integer-equal function.
     */
    public static final String NAME_INTEGER_EQUAL = FUNCTION_NS + "integer-equal";

    /**
     * Standard identifier for the double-equal function.
     */
    public static final String NAME_DOUBLE_EQUAL = FUNCTION_NS + "double-equal";

    /**
     * Standard identifier for the date-equal function.
     */
    public static final String NAME_DATE_EQUAL = FUNCTION_NS + "date-equal";

    /**
     * Standard identifier for the time-equal function.
     */
    public static final String NAME_TIME_EQUAL = FUNCTION_NS + "time-equal";

    /**
     * Standard identifier for the dateTime-equal function.
     */
    public static final String NAME_DATETIME_EQUAL = FUNCTION_NS + "dateTime-equal";

    /**
     * Standard identifier for the dayTimeDuration-equal function.
     */
    public static final String NAME_DAYTIME_DURATION_EQUAL = FUNCTION_NS + "dayTimeDuration-equal";

    /**
     * Standard identifier for the yearMonthDuration-equal function.
     */
    public static final String NAME_YEARMONTH_DURATION_EQUAL = FUNCTION_NS
            + "yearMonthDuration-equal";

    /**
     * Standard identifier for the anyURI-equal function.
     */
    public static final String NAME_ANYURI_EQUAL = FUNCTION_NS + "anyURI-equal";

    /**
     * Standard identifier for the x500Name-equal function.
     */
    public static final String NAME_X500NAME_EQUAL = FUNCTION_NS + "x500Name-equal";

    /**
     * Standard identifier for the rfc822Name-equal function.
     */
    public static final String NAME_RFC822NAME_EQUAL = FUNCTION_NS + "rfc822Name-equal";

    /**
     * Standard identifier for the hexBinary-equal function.
     */
    public static final String NAME_HEXBINARY_EQUAL = FUNCTION_NS + "hexBinary-equal";

    /**
     * Standard identifier for the base64Binary-equal function.
     */
    public static final String NAME_BASE64BINARY_EQUAL = FUNCTION_NS + "base64Binary-equal";

    /**
     * Standard identifier for the ipAddress-equal function.
     */
    public static final String NAME_IPADDRESS_EQUAL = FUNCTION_NS_2 + "ipAddress-equal";

    /**
     * Standard identifier for the dnsName-equal function.
     */
    public static final String NAME_DNSNAME_EQUAL = FUNCTION_NS_2 + "dnsName-equal";

    /**
     * Standard identifier for the sting equal with ignore case
     */
    public static final String NAME_EQUAL_CASE_IGNORE = FUNCTION_NS_3 + "string-equal-ignore-case";

    // internal identifiers for each of the supported functions   
    private static final int ID_EQUAL_CASE_IGNORE = 1;

    // private mapping of standard functions to their argument types
    private static HashMap typeMap;

    /**
     * Static initializer sets up a map of standard function names to their associated datatypes
     */
    static {
        typeMap = new HashMap();

        typeMap.put(NAME_STRING_EQUAL, StringAttribute.identifier);
        typeMap.put(NAME_BOOLEAN_EQUAL, BooleanAttribute.identifier);
        typeMap.put(NAME_INTEGER_EQUAL, IntegerAttribute.identifier);
        typeMap.put(NAME_DOUBLE_EQUAL, DoubleAttribute.identifier);
        typeMap.put(NAME_DATE_EQUAL, DateAttribute.identifier);
        typeMap.put(NAME_TIME_EQUAL, TimeAttribute.identifier);
        typeMap.put(NAME_DATETIME_EQUAL, DateTimeAttribute.identifier);
        typeMap.put(NAME_DAYTIME_DURATION_EQUAL, DayTimeDurationAttribute.identifier);
        typeMap.put(NAME_YEARMONTH_DURATION_EQUAL, YearMonthDurationAttribute.identifier);
        typeMap.put(NAME_ANYURI_EQUAL, AnyURIAttribute.identifier);
        typeMap.put(NAME_X500NAME_EQUAL, X500NameAttribute.identifier);
        typeMap.put(NAME_RFC822NAME_EQUAL, RFC822NameAttribute.identifier);
        typeMap.put(NAME_HEXBINARY_EQUAL, HexBinaryAttribute.identifier);
        typeMap.put(NAME_BASE64BINARY_EQUAL, Base64BinaryAttribute.identifier);
        typeMap.put(NAME_IPADDRESS_EQUAL, IPAddressAttribute.identifier);
        typeMap.put(NAME_DNSNAME_EQUAL, DNSNameAttribute.identifier);
        typeMap.put(NAME_EQUAL_CASE_IGNORE, StringAttribute.identifier);
    }

    /**
     * Creates a new <code>EqualFunction</code> object that supports one of the standard type-equal
     * functions. If you need to create an instance for a custom type, use the
     * <code>getEqualInstance</code> method or the alternate constructor.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *                     including the full namespace
     * @throws IllegalArgumentException if the function isn't standard
     */
    public EqualFunction(String functionName) {
        this(functionName, getArgumentType(functionName));
    }

    /**
     * Creates a new <code>EqualFunction</code> object.
     *
     * @param functionName the standard XACML name of the function to be handled by this object,
     *                     including the full namespace
     * @param argumentType the standard XACML name for the type of the arguments, inlcuding the full
     *                     namespace
     */
    public EqualFunction(String functionName, String argumentType) {
        super(functionName, getId(functionName), argumentType, false, 2, BooleanAttribute.identifier, false);
    }

    /**
     * Returns an <code>EqualFunction</code> that provides the type-equal functionality over the
     * given attribute type. This should be used to create new function instances for any new
     * attribute types, and the resulting object should be put into the <code>FunctionFactory</code>
     * (instances for the standard types are pre-installed in the standard factory).
     * <p>
     * Note that this method has the same affect as invoking the constructor with the same
     * parameters. This method is provided as a convenience, and for symmetry with the bag and set
     * functions.
     *
     * @param functionName the name to use for the function
     * @param argumentType the type to operate on
     * @return a new <code>EqualFunction</code>
     */
    public static EqualFunction getEqualInstance(String functionName, String argumentType) {
        return new EqualFunction(functionName, argumentType);
    }

    /**
     * Private helper that returns the type used for the given standard type-equal function.
     */
    private static String getArgumentType(String functionName) {
        String datatype = (String) (typeMap.get(functionName));

        if (datatype == null)
            throw new IllegalArgumentException("not a standard function: " + functionName);

        return datatype;
    }

    /**
     * Private helper that returns the internal identifier used for the given standard function.
     *
     * @param functionName function name
     * @return function id
     */
    private static int getId(String functionName) {

        if (functionName.equals(NAME_EQUAL_CASE_IGNORE)) {
            return ID_EQUAL_CASE_IGNORE;
        } else {
            return 0;
        }
    }

    /**
     * Returns a <code>Set</code> containing all the function identifiers supported by this class.
     *
     * @return a <code>Set</code> of <code>String</code>s
     */
    public static Set getSupportedIdentifiers() {
        return Collections.unmodifiableSet(typeMap.keySet());
    }

    /**
     * Evaluate the function, using the specified parameters.
     *
     * @param inputs  a <code>List</code> of <code>Evaluatable</code> objects representing the
     *                arguments passed to the function
     * @param context an <code>EvaluationCtx</code> so that the <code>Evaluatable</code> objects can
     *                be evaluated
     * @return an <code>EvaluationResult</code> representing the function's result
     */
    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {

        // Evaluate the arguments
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null)
            return result;

        if (argValues[1] instanceof StringAttribute
                && XACMLConstants.ANY.equals(((StringAttribute) argValues[1]).getValue())) {
            return EvaluationResult.getInstance(true);
        }

        // Now that we have real values, perform the equals operation
        if (getFunctionId() == ID_EQUAL_CASE_IGNORE) {
            return EvaluationResult.getInstance(argValues[0].encode().toLowerCase().
                    equals(argValues[1].encode().toLowerCase()));
        } else {
            return EvaluationResult.getInstance(argValues[0].equals(argValues[1]));
        }
    }

}
