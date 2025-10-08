/*
*  Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.balana.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.impl.Constants;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 *
 */
public class Utils {

    /**
     * Logger instance
     */
    private static final Log logger = LogFactory.getLog(Utils.class);

    /**
     * Defines XML Entity Expansion Limit
     */
    private static final int ENTITY_EXPANSION_LIMIT = 0;

    //Secured transformer factory implementation
    private static String JAVAX_TRANSFORMER_PROP_VAL =
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";

    /**
     * Convert Document element to a String object
     * @param doc Document element
     * @return String object
     * @throws TransformerException throws when transform fails
     */
    public static String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        String transformerFactoryClassName = System.getProperty("org.wso2.balana.TransformerFactory");
        if(transformerFactoryClassName == null) {
            transformerFactoryClassName = "org.apache.xalan.processor.TransformerFactoryImpl";
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance(transformerFactoryClassName, null);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString().substring(writer.toString().indexOf('>') + 1);
    }

    /**
     * Create Document Object
     *
     * @return Document Object
     * @throws ParserConfigurationException  throws
     */
    public static Document createNewDocument() throws ParserConfigurationException {

        DocumentBuilderFactory docFactory = getSecuredDocumentBuilderFactory();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        return doc;
    }

    /**
     * Create DocumentBuilderFactory with the XXE prevention measurements
     *
     * @return DocumentBuilderFactory instance
     */
    public static DocumentBuilderFactory getSecuredDocumentBuilderFactory() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        try {
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            logger.error(
                    "Failed to load XML Processor Feature " + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE + " or " +
                            Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE + " or " + Constants.LOAD_EXTERNAL_DTD_FEATURE);
        }

        SecurityManager securityManager = new SecurityManager();
        securityManager.setEntityExpansionLimit(ENTITY_EXPANSION_LIMIT);
        dbf.setAttribute(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, securityManager);

        return dbf;
    }
    
    
//    public static Element createElement(String xmlInput) {
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlInput)));
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        DOMSource source = new DOMSource(document);
//        StreamResult result =  new StreamResult(new StringWriter());
//        transformer.transform(source, result);
//    }

    /**
     * Create a secure process enabled TransformerFactory.
     *
     * @return Secured TransformerFactory which is stricly implemented via
     * com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
     */
    public static TransformerFactory getSecuredTransformerFactory() {

        TransformerFactory transformerFactory;
        try {
            // Prevent XXE Attack by ensure using the correct factory class to create TrasformerFactory instance.
            // This will instruct Java to use the version which supports using ACCESS_EXTERNAL_DTD argument.
            transformerFactory = TransformerFactory.newInstance(JAVAX_TRANSFORMER_PROP_VAL, null);
        } catch (TransformerFactoryConfigurationError e) {
            logger.error("Failed to load default TransformerFactory", e);
            // This part uses the default implementation of xalan.
            transformerFactory = TransformerFactory.newInstance();
        }

        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            logger.error("Failed to load XML Processor Feature " + XMLConstants.FEATURE_SECURE_PROCESSING +
                    " for secure-processing.");
        }
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return transformerFactory;
    }
}
