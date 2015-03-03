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

package org.wso2.balana.samples.hierarchical.resource;

import org.wso2.balana.Balana;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.ResourceFinderModule;
import org.wso2.balana.finder.ResourceFinderResult;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample resource finder for finding hierarchical resources under the root node
 */
public class HierarchicalResourceFinder extends ResourceFinderModule {

    private final static String DATA_TYPE = "http://www.w3.org/2001/XMLSchema#string" ;

    @Override
    public boolean isChildSupported() {
        return true;
    }

    @Override
    public boolean isDescendantSupported() {
        return true;
    }

    @Override
    public ResourceFinderResult findChildResources(AttributeValue parentResourceId, EvaluationCtx context) {

        ResourceFinderResult result = new ResourceFinderResult();

        if(!DATA_TYPE.equals(parentResourceId.getType().toString())){
            return result;
        }

        if("root".equals(parentResourceId.encode())){
            Set<AttributeValue> set = new HashSet<AttributeValue>();
            try{
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public"));
            } catch (Exception e) {
                // just ignore
            }
            result = new ResourceFinderResult(set);
        }

        return result;
    }


    @Override
    public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId, EvaluationCtx context) {

        ResourceFinderResult result = new ResourceFinderResult();

        if(!DATA_TYPE.equals(parentResourceId.getType().toString())){
            return result;
        }

        if("root".equals(parentResourceId.encode())){
            Set<AttributeValue> set = new HashSet<AttributeValue>();
            try{
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public/developments"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public/news"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/leadership"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/business"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/support"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/team"));
            } catch (Exception e) {
                // just ignore
            }
            result = new ResourceFinderResult(set);
        }
        return result;
    }
}
