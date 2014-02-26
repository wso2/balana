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

package org.wso2.balana;

import org.w3c.dom.Document;
import org.wso2.balana.attr.AttributeFactory;
import org.wso2.balana.combine.CombiningAlgFactory;
import org.wso2.balana.cond.FunctionFactory;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.CurrentEnvModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.finder.impl.SelectorModule;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the core class for the Balana project providing the init point of Balana engine.
 *
 */
public class Balana {

    /**
     * PDP configuration of Balana engine  instance
     */
    private PDPConfig pdpConfig;

    /**
     * Attribute factory that supports in Balana engine instance
     */
    private AttributeFactory attributeFactory;

    /**
     * Target Function factory that supports in Balana engine instance
     */
    private FunctionFactory functionTargetFactory;

    /**
     * Condition Function factory that supports in Balana engine instance
     */
    private FunctionFactory functionConditionFactory;

    /**
     * General function factory that supports in Balana engine instance
     */
    private FunctionFactory functionGeneralFactory;

    /**
     * combining factory that supports in Balana engine instance
     */
    private CombiningAlgFactory combiningAlgFactory;

    /**
     * builders to build XACML request
     */
    private DocumentBuilderFactory builder;

    /**
     * lock
     */
    private final static Object lock = new Object();

    /**
     * One instance of Balana engine is created.
     */
    private static Balana balana;

    /**
     * This constructor creates the Balana engine instance. First, it loads all configuration
     * from store and creates Balan engine with given configuration names.
     * If no configuration name is given, loads default configurations of the configuration store.
     * If configuration store does not configured or any error in building, It create default Balana
     * engine.
     *
     * @param pdpConfigName pdp configuration name
     * @param attributeFactoryName  attribute factory name
     * @param functionFactoryName  function factory name
     * @param combiningAlgFactoryName combine factory name
     */
    private Balana(String pdpConfigName, String attributeFactoryName, String functionFactoryName,
                                                                String combiningAlgFactoryName) {
        ConfigurationStore  store = null;

        try {
            if(System.getProperty(ConfigurationStore.PDP_CONFIG_PROPERTY) != null){
                store = new ConfigurationStore();
            } else {
                String configFile = (new File(".")).getCanonicalPath() + File.separator + "src" +
                File.separator + "main" + File.separator +  "resources" + File.separator + "config.xml";
                File file = new File(configFile);
                if(file.exists()) {
                    store = new ConfigurationStore(new File(configFile));
                }
            }

            if(store != null){
                if(pdpConfigName != null){
                    pdpConfig = store.getPDPConfig(pdpConfigName);
                } else {
                    pdpConfig = store.getDefaultPDPConfig();
                }

                if(attributeFactoryName != null){
                    this.attributeFactory = store.getAttributeFactory(attributeFactoryName);
                } else {
                    this.attributeFactory = store.getDefaultAttributeFactoryProxy().getFactory();
                }

                if(functionFactoryName != null){
                    this.functionTargetFactory = store.
                                    getFunctionFactoryProxy(functionFactoryName).getTargetFactory();
                } else {
                    this.functionTargetFactory = store.
                                    getDefaultFunctionFactoryProxy().getTargetFactory();
                }

                if(functionFactoryName != null){
                    this.functionConditionFactory = store.
                                    getFunctionFactoryProxy(functionFactoryName).getConditionFactory();
                } else {
                    this.functionConditionFactory = store.
                                    getDefaultFunctionFactoryProxy().getConditionFactory();
                }

                if(functionFactoryName != null){
                    this.functionGeneralFactory = store.
                                    getFunctionFactoryProxy(functionFactoryName).getGeneralFactory();
                } else {
                    this.functionGeneralFactory = store.
                                    getDefaultFunctionFactoryProxy().getGeneralFactory();
                }

                if(functionFactoryName != null){
                    this.combiningAlgFactory = store.getCombiningAlgFactory(functionFactoryName);
                } else {
                    this.combiningAlgFactory = store.getDefaultCombiningFactoryProxy().getFactory();
                }
            }

        } catch (Exception e) {
            // just ignore all exceptions as all are init again with default configurations
        }

        if(pdpConfig == null){
            
            //creating default one with Balana engine.
            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> policyFinderModules = new HashSet<PolicyFinderModule>();
            FileBasedPolicyFinderModule fileBasedPolicyFinderModule = new FileBasedPolicyFinderModule();
            policyFinderModules.add(fileBasedPolicyFinderModule);
            policyFinder.setModules(policyFinderModules);

            AttributeFinder attributeFinder = new AttributeFinder();
            List<AttributeFinderModule> attributeFinderModules = new ArrayList<AttributeFinderModule>();
            SelectorModule selectorModule = new SelectorModule();
            CurrentEnvModule currentEnvModule = new CurrentEnvModule();
            attributeFinderModules.add(selectorModule);
            attributeFinderModules.add(currentEnvModule);
            attributeFinder.setModules(attributeFinderModules);
                        
            pdpConfig = new PDPConfig(attributeFinder, policyFinder, null, false);
        }

        if(attributeFactory == null){
            attributeFactory = AttributeFactory.getInstance();
        }

        if(functionTargetFactory == null){
            functionTargetFactory = FunctionFactory.getInstance().getTargetFactory();
        }

        if(functionConditionFactory == null){
            functionConditionFactory = FunctionFactory.getInstance().getConditionFactory();
        }

        if(functionGeneralFactory == null){
            functionGeneralFactory = FunctionFactory.getInstance().getGeneralFactory();
        }

        if(combiningAlgFactory == null){
            combiningAlgFactory = CombiningAlgFactory.getInstance();
        }

        // init builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        this.builder = dbf;
    }

    /**
     * Get instance of Balana engine
     *
     * @return returns <code>Balana</code>
     */
    public static Balana getInstance(){

        if(balana == null){
            synchronized (lock){
                if(balana == null){
                    balana = new Balana(null, null, null, null);
                }
            }
        }

        return balana;
    }

    /**
     * Get instance of Balana engine for given common identifier
     *
     * @param identifier identifier name
     * @return returns <code>Balana</code>
     */
    public Balana getInstance(String identifier){

        if(balana == null){
            synchronized (lock){
                if(balana == null){
                    balana = new Balana(identifier, identifier, identifier, identifier);
                }
            }
        }

        return balana;
    }

    /**
     * Get instance of Balana engine for given identifiers
     *
     * @param pdpConfigName pdp configuration name
     * @param attributeFactoryName  attribute factory name
     * @param functionFactoryName  function factory name
     * @param combiningAlgFactoryName combine factory name
     * @return returns <code>Balana</code>
     */
    public Balana getInstance(String pdpConfigName, String attributeFactoryName, String functionFactoryName,
                                                                String combiningAlgFactoryName){
        if(balana == null){
            synchronized (lock){
                if(balana == null){
                    balana = new Balana(pdpConfigName, attributeFactoryName, functionFactoryName,
                                                                        combiningAlgFactoryName);
                }
            }
        }
        return balana;
    }


    public PDPConfig getPdpConfig() {
        return pdpConfig;
    }

    public void setPdpConfig(PDPConfig pdpConfig) {
        this.pdpConfig = pdpConfig;
    }

    public AttributeFactory getAttributeFactory() {
        return attributeFactory;
    }

    public void setAttributeFactory(AttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory;
    }

    public FunctionFactory getFunctionTargetFactory() {
        return functionTargetFactory;
    }

    public void setFunctionTargetFactory(FunctionFactory functionTargetFactory) {
        this.functionTargetFactory = functionTargetFactory;
    }

    public FunctionFactory getFunctionConditionFactory() {
        return functionConditionFactory;
    }

    public void setFunctionConditionFactory(FunctionFactory functionConditionFactory) {
        this.functionConditionFactory = functionConditionFactory;
    }

    public FunctionFactory getFunctionGeneralFactory() {
        return functionGeneralFactory;
    }

    public void setFunctionGeneralFactory(FunctionFactory functionGeneralFactory) {
        this.functionGeneralFactory = functionGeneralFactory;
    }

    public CombiningAlgFactory getCombiningAlgFactory() {
        return combiningAlgFactory;
    }

    public void setCombiningAlgFactory(CombiningAlgFactory combiningAlgFactory) {
        this.combiningAlgFactory = combiningAlgFactory;
    }

    public DocumentBuilderFactory getBuilder() {
        return builder;
    }
}
