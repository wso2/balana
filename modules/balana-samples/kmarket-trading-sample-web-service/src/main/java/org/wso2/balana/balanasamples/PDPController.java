package org.wso2.balana.balanasamples;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wso2.balana.PDP;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.xacml3.Advice;

import javax.rmi.CORBA.Util;
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
