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
package org.wso2.balana.balanasamples;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wso2.balana.PDP;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.xacml3.Advice;
import java.util.List;

@RestController
public class PDPController {

    @PostMapping("/evaluate")
    public ResponseObject evaluate(@RequestBody RequestObject request)
    {
        int totalAmount = 0;

        Utilities.initData();

        Utilities.initBalana();

        totalAmount = Utilities.calculateTotal(request.getProductName(), request.getNumberOfProducts());

        String xacmlRequest = Utilities.createXACMLRequest(
                request.getUsername(), request.getProductName(), request.getNumberOfProducts(), totalAmount);

        PDP pdp = Utilities.getPDPNewInstance();

        String xacmlResponse = pdp.evaluate(xacmlRequest);

        String responseMessage = "";

        try {
            ResponseCtx responseCtx = ResponseCtx.getInstance(Utilities.getXacmlResponse(xacmlResponse));
            AbstractResult result  = responseCtx.getResults().iterator().next();
            if(AbstractResult.DECISION_PERMIT == result.getDecision()){
                responseMessage = "\n" + request.getUsername() + " is authorized to perform this purchase\n\n";
            } else {
                responseMessage += "\n" + request.getUsername() + " is NOT authorized to perform this purchase\n";
                List<Advice> advices = result.getAdvices();
                for(Advice advice : advices){
                    List<AttributeAssignment> assignments = advice.getAssignments();
                    for(AttributeAssignment assignment : assignments){
                        responseMessage += "Advice :  " + assignment.getContent() +"\n\n";
                    }
                }
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return new ResponseObject(responseMessage);
    }
}
