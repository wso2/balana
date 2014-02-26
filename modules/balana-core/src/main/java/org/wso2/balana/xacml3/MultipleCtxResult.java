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

package org.wso2.balana.xacml3;

import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;

import java.util.Set;

/**
 * This is used in cases where a normal result is Set of contexts, but if an context couldn't
 * be created or any error is occurred during the creation, then a Status object needs to be
 * returned instead.
 */
public class MultipleCtxResult {

    /**
     * A ,<code>Set</code> of <code>EvaluationCtx</code>
     */
    private Set<EvaluationCtx>  evaluationCtxSet;

    /**
     * <code>Status<code>
     */
    private Status status;

    /**
     * whether any indeterminate has occurred or not
     */
    private boolean indeterminate;

    /**
     * Constructs a <code>MultipleCtxResult</code> object with  required data
     *
     * @param evaluationCtxSet  A ,<code>Set</code> of <code>EvaluationCtx</code>
     */
    public MultipleCtxResult(Set<EvaluationCtx> evaluationCtxSet) {
        this(evaluationCtxSet, null, false);
    }

     /**
     * Constructs a <code>MultipleCtxResult</code> object with status error
     *
      * @param status  <code>Status<code>
      */
    public MultipleCtxResult(Status status) {
        this(null, status, true);
    }

    /**
     * Constructs a <code>MultipleCtxResult</code> object with  required data
     *
     * @param evaluationCtxSet  A ,<code>Set</code> of <code>EvaluationCtx</code>
     * @param status   <code>Status<code>
     * @param indeterminate  whether any indeterminate has occurred or not
     */
    public MultipleCtxResult(Set<EvaluationCtx> evaluationCtxSet, Status status, boolean indeterminate) {
        this.evaluationCtxSet = evaluationCtxSet;
        this.status = status;
        this.indeterminate = indeterminate;
    }

    public Set<EvaluationCtx> getEvaluationCtxSet() {
        return evaluationCtxSet;
    }

    public Status getStatus() {
        if(indeterminate){
            return status;
        } else {
            return null;
        }
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }
}
