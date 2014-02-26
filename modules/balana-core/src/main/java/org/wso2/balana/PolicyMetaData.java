/*
 * @(#)PolicyMetaData.java
 *
 * Copyright 2005-2006 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana;

import org.wso2.balana.attr.AttributeFactoryProxy;
import org.wso2.balana.combine.CombiningAlgFactoryProxy;
import org.wso2.balana.cond.FunctionFactoryProxy;

/**
 * This is used to share polcy meta-data throughout the policy tree. Examples of common meta-data
 * include the version of XACML or XPath being used in a policy.
 * 
 * @since 2.0
 * @author Seth Proctor
 */
public class PolicyMetaData {


    /**
     * The default version of XACML, 1.0, used if no namespace string is specified
     */
    public static final int XACML_DEFAULT_VERSION = XACMLConstants.XACML_VERSION_1_0;

    // private mapping from XACML version number to identifier string
    private static String[] xacmlIdentifiers = { XACMLConstants.XACML_1_0_IDENTIFIER,
            XACMLConstants.XACML_1_0_IDENTIFIER, XACMLConstants.XACML_2_0_IDENTIFIER,
            XACMLConstants.XACML_3_0_IDENTIFIER };

    /**
     * XPath 1.0 identifier
     */
    public static final String XPATH_1_0_IDENTIFIER = "http://www.w3.org/TR/1999/Rec-xpath-19991116";

    /**
     * Version identifier for an unspecified version of XPath
     */
    public static final int XPATH_VERSION_UNSPECIFIED = 0;

    /**
     * Version identifier for XPath 1.0
     */
    public static final int XPATH_VERSION_1_0 = 1;

    // private mapping from XPath version number to identifier string
    private static String[] xpathIdentifiers = { null, XPATH_1_0_IDENTIFIER };

    // the version of XACML
    private int xacmlVersion;

    // the version of XPath, or null if none is specified
    private int xpathVersion;

    /**
     * Creates a <code>PolicyMetaData</code> instance with all the parameters set to their default
     * values.
     */
    public PolicyMetaData() {
        this(XACML_DEFAULT_VERSION, XPATH_VERSION_UNSPECIFIED);
    }

    /**
     * Creates a <code>PolicyMetaData</code> instance with the given parameters. A proxy value of
     * null implies the default factory.
     *
     * @param xacmlVersion the version of XACML used in a policy
     * @param xpathVersion the XPath version to use in any selectors
     */
    public PolicyMetaData(int xacmlVersion, int xpathVersion) {
        this.xacmlVersion = xacmlVersion;
        this.xpathVersion = xpathVersion;
    }

    /**
     * Creates a <code>PolicyMetaData</code> instance with the given parameters.
     * 
     * @param xacmlVersion the version of XACML used in a policy
     * @param xpathVersion the XPath version to use in any selectors, or null if this is unspecified
     *            (ie, not supplied in the defaults section of the policy)
     * the XACML policy, if null use default factories
     * @throws IllegalArgumentException if the identifier strings are unknown
     */
    public PolicyMetaData(String xacmlVersion, String xpathVersion) {

        if (xacmlVersion == null){
            this.xacmlVersion = XACML_DEFAULT_VERSION;
        } else if (xacmlVersion.equals(XACMLConstants.XACML_1_0_IDENTIFIER)){
            this.xacmlVersion = XACMLConstants.XACML_VERSION_1_0;
        } else if (xacmlVersion.equals(XACMLConstants.XACML_2_0_IDENTIFIER)){
            this.xacmlVersion = XACMLConstants.XACML_VERSION_2_0;
        } else if (xacmlVersion.equals(XACMLConstants.XACML_3_0_IDENTIFIER)){
            this.xacmlVersion = XACMLConstants.XACML_VERSION_3_0;
        } else {
            throw new IllegalArgumentException("Unknown XACML version " + "string: " + xacmlVersion);
        }

        if (xpathVersion != null) {
//            if (!xpathVersion.equals(XPATH_1_0_IDENTIFIER)){
//                throw new IllegalArgumentException("Unsupported XPath " + " version: "
//                        + xpathVersion);
//            }
            this.xpathVersion = XPATH_VERSION_1_0;
        } else {
            this.xpathVersion = XPATH_VERSION_UNSPECIFIED;
        }        
    }

    /**
     * Returns which version of XACML is specified in this meta-data.
     *
     * @return the XACML version
     */
    public int getXACMLVersion() {
        return xacmlVersion;
    }

    /**
     * Returns the identifier string for the specified version of XACML.
     *
     * @return the identifier string
     */
    public String getXACMLIdentifier() {
        return xacmlIdentifiers[xacmlVersion];
    }

    /**
     * Returns which version of XPath is specified in this meta-data.
     *
     * @return the XPath version or null
     */
    public int getXPathVersion() {
        return xpathVersion;
    }

    /**
     * Returns the identifier string for the specified version of XPath, or null if no version is
     * specified.
     *
     * @return the identifier string or null
     */
    public String getXPathIdentifier() {
        return xpathIdentifiers[xpathVersion];
    }

}
