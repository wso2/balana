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

package org.wso2.balana.advance;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Apply;
import org.wso2.balana.cond.EqualFunction;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;
import org.wso2.balana.cond.GeneralBagFunction;
import org.wso2.balana.cond.xacml3.XACML3HigherOrderFunction;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.balana.cond.EqualFunction.NAME_STRING_EQUAL;
import static org.wso2.balana.cond.xacml3.XACML3HigherOrderFunction.NAME_ALL_OF;
import static org.wso2.balana.cond.xacml3.XACML3HigherOrderFunction.NAME_ANY_OF;
import static org.wso2.balana.cond.xacml3.XACML3HigherOrderFunction.NAME_ANY_OF_ANY;
import static org.wso2.balana.utils.Constants.PolicyConstants.XACMLData.FUNCTION_BAG;

/**
 * Test case for XACML3 Higher Order Functions. (any-of, all-of and any-of-any)
 */
public class XACML3HigherOrderFunctionTest extends TestCase {

    public void testCheckInputs() {

        XACML3HigherOrderFunction anyOfFunction = new XACML3HigherOrderFunction(NAME_ANY_OF);
        XACML3HigherOrderFunction allOfFunction = new XACML3HigherOrderFunction(NAME_ALL_OF);

        List<Expression> inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("John"));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));

        try {
            anyOfFunction.checkInputs(inputs);
            allOfFunction.checkInputs(inputs);
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));
        try {
            allOfFunction.checkInputs(inputs);
            Assert.fail("Input validation failed for all of");
            anyOfFunction.checkInputs(inputs);
            Assert.fail("Input validation failed for any of");
        } catch (IllegalArgumentException err) {
            // Expected error.
        }

        // Test for any-of-any
        XACML3HigherOrderFunction anyOfAnyFunction = new XACML3HigherOrderFunction(NAME_ANY_OF_ANY);
        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("John"));
        inputs.add(new StringAttribute("Ringo"));

        try {
            anyOfAnyFunction.checkInputs(inputs);
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));

        try {
            anyOfAnyFunction.checkInputs(inputs);
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("John"));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));

        try {
            anyOfAnyFunction.checkInputs(inputs);
            Assert.fail("Input validation failed for any of any");
        } catch (IllegalArgumentException err) {
            // Expected error.
        }
    }

    public void testEvaluate() {

        XACML3HigherOrderFunction anyOfFunction = new XACML3HigherOrderFunction(NAME_ANY_OF);
        XACML3HigherOrderFunction allOfFunction = new XACML3HigherOrderFunction(NAME_ALL_OF);

        List<Expression> inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("John"));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));

        try {
            EvaluationResult evaluate = anyOfFunction.evaluate(inputs, null);
            Assert.assertTrue(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        try {
            EvaluationResult evaluate = allOfFunction.evaluate(inputs, null);
            Assert.assertFalse(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Alex"));
        inputs.add(new StringAttribute("John"));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));

        try {
            EvaluationResult evaluate = anyOfFunction.evaluate(inputs, null);
            Assert.assertFalse(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(getApply(new String[]{"Paul", "Paul", "Paul"}));
        try {
            EvaluationResult evaluate = allOfFunction.evaluate(inputs, null);
            Assert.assertTrue(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        // Test for any-of-any
        XACML3HigherOrderFunction anyOfAnyFunction = new XACML3HigherOrderFunction(NAME_ANY_OF_ANY);

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(getApply(new String[]{"Paul1", "George1", "Ringo"}));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));
        try {
            EvaluationResult evaluate = anyOfAnyFunction.evaluate(inputs, null);
            Assert.assertTrue(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(getApply(new String[]{"Paul1", "George1", "Ringo1"}));
        inputs.add(getApply(new String[]{"Paul", "George", "Ringo"}));
        try {
            EvaluationResult evaluate = anyOfAnyFunction.evaluate(inputs, null);
            Assert.assertFalse(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("John"));
        inputs.add(new StringAttribute("Ringo"));
        try {
            EvaluationResult evaluate = anyOfAnyFunction.evaluate(inputs, null);
            Assert.assertFalse(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }

        inputs = new ArrayList();
        inputs.add(new EqualFunction(NAME_STRING_EQUAL));
        inputs.add(new StringAttribute("Paul"));
        inputs.add(new StringAttribute("John"));
        inputs.add(new StringAttribute("Paul"));
        try {
            EvaluationResult evaluate = anyOfAnyFunction.evaluate(inputs, null);
            Assert.assertTrue(((BooleanAttribute) evaluate.getAttributeValue()).getValue());
        } catch (IllegalArgumentException err) {
            Assert.fail("Input validation failed");
        }
    }

    private Apply getApply(String[] values) {

        List<StringAttribute> applyList = new ArrayList();
        for (String value : values) {
            applyList.add(new StringAttribute(value));
        }
        return new Apply(new GeneralBagFunction(FUNCTION_BAG), applyList);
    }
}
