/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.balana.extension.function;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BooleanAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.Evaluatable;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.FunctionBase;
import org.wso2.balana.ctx.EvaluationCtx;

import java.util.List;

public class ExtensionFunction extends FunctionBase {

    public static final String EXTENSION_FUNCTION = FUNCTION_NS + "extension-function";

    private static final int ID_EXTENSION_FUNCTION = 0;

    public ExtensionFunction() {

        super(EXTENSION_FUNCTION, ID_EXTENSION_FUNCTION, StringAttribute.identifier, false, 2, 2,
                BooleanAttribute.identifier, false);
    }

    public EvaluationResult evaluate(List<Evaluatable> inputs, EvaluationCtx context) {

        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        evalArgs(inputs, context, argValues);
        EvaluationResult result = new EvaluationResult(BooleanAttribute.getInstance(false));

        switch (getFunctionId()) {
            case ID_EXTENSION_FUNCTION:
                String resource = ((StringAttribute) argValues[0]).getValue().trim();
                String subject = ((StringAttribute) argValues[1]).getValue().trim();
                if (!isNullOrEmpty(resource) && !isNullOrEmpty(subject)) {
                    result = new EvaluationResult(BooleanAttribute.getInstance(true));
                    break;
                }
        }

        return result;
    }

    private boolean isNullOrEmpty(String attr) {

        return (attr == null || attr.isEmpty());
    }
}