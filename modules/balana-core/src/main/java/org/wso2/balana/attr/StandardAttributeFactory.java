/*
 * @(#)StandardAttributeFactory
 *
 * Copyright 2004-2006 Sun Microsystems, Inc. All Rights Reserved.
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

package org.wso2.balana.attr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.UnknownIdentifierException;

import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.proxy.*;
import org.wso2.balana.attr.proxy.xacml3.XPathAttributeProxy;
import org.wso2.balana.attr.xacml3.XPathAttribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * This factory supports the standard set of datatypes specified in XACML 1.x and 2.0. It is the
 * default factory used by the system, and imposes a singleton pattern insuring that there is only
 * ever one instance of this class.
 * <p>
 * Note that because this supports only the standard datatypes, this factory does not allow the
 * addition of any other datatypes. If you call <code>addDatatype</code> on an instance of this
 * class, an exception will be thrown. If you need a standard factory that is modifiable, you should
 * create a new <code>BaseAttributeFactory</code> (or some other <code>AttributeFactory</code>) and
 * configure it with the standard datatypes using <code>addStandardDatatypes</code> (or, in the case
 * of <code>BaseAttributeFactory</code>, by providing the datatypes in the constructor).
 * 
 * @since 1.2
 * @author Seth Proctor
 */
public class StandardAttributeFactory extends BaseAttributeFactory {

    // the one instance of this factory
    private static volatile StandardAttributeFactory factoryInstance = null;

    // the datatypes supported by this factory
    private static HashMap supportedDatatypes = null;

    // the supported identifiers for each version of XACML
    private static Set supportedV1Identifiers;
    private static Set supportedV2Identifiers;
    private static Set supportedV3Identifiers;

    // the logger we'll use for all messages
    private static Log logger = LogFactory.getLog(StandardAttributeFactory.class);

    /**
     * Private constructor that sets up proxies for all of the standard datatypes.
     */
    private StandardAttributeFactory() {
        super(supportedDatatypes);
    }

    /**
     * Private initializer for the supported datatypes. This isn't called until something needs
     * these values, and is only called once.
     */
    private static void initDatatypes() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing standard datatypes");
        }

        supportedDatatypes = new HashMap();

        // the 1.x datatypes
        supportedDatatypes.put(BooleanAttribute.identifier, new BooleanAttributeProxy());
        supportedDatatypes.put(StringAttribute.identifier, new StringAttributeProxy());
        supportedDatatypes.put(DateAttribute.identifier, new DateAttributeProxy());
        supportedDatatypes.put(TimeAttribute.identifier, new TimeAttributeProxy());
        supportedDatatypes.put(DateTimeAttribute.identifier, new DateTimeAttributeProxy());
        supportedDatatypes.put(DayTimeDurationAttribute.identifier,
                new DayTimeDurationAttributeProxy());
        supportedDatatypes.put(YearMonthDurationAttribute.identifier,
                new YearMonthDurationAttributeProxy());
        supportedDatatypes.put(DoubleAttribute.identifier, new DoubleAttributeProxy());
        supportedDatatypes.put(IntegerAttribute.identifier, new IntegerAttributeProxy());
        supportedDatatypes.put(AnyURIAttribute.identifier, new AnyURIAttributeProxy());
        supportedDatatypes.put(HexBinaryAttribute.identifier, new HexBinaryAttributeProxy());
        supportedDatatypes.put(Base64BinaryAttribute.identifier, new Base64BinaryAttributeProxy());
        supportedDatatypes.put(X500NameAttribute.identifier, new X500NameAttributeProxy());
        supportedDatatypes.put(RFC822NameAttribute.identifier, new RFC822NameAttributeProxy());

        supportedV1Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());

        // the 2.0 datatypes
        supportedDatatypes.put(DNSNameAttribute.identifier, new DNSNameAttributeProxy());
        supportedDatatypes.put(IPAddressAttribute.identifier, new IPAddressAttributeProxy());

        supportedV2Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());

        // the 3.0 datatypes.
        supportedDatatypes.put(XPathAttribute.identifier, new XPathAttributeProxy());

        supportedV3Identifiers = Collections.unmodifiableSet(supportedDatatypes.keySet());
    }

    /**
     * Returns an instance of this factory. This method enforces a singleton model, meaning that
     * this always returns the same instance, creating the factory if it hasn't been requested
     * before. This is the default model used by the <code>AttributeFactory</code>, ensuring quick
     * access to this factory.
     * 
     * @return the factory instance
     */
    public static StandardAttributeFactory getFactory() {
        if (factoryInstance == null) {
            synchronized (StandardAttributeFactory.class) {
                if (factoryInstance == null) {
                    initDatatypes();
                    factoryInstance = new StandardAttributeFactory();
                }
            }
        }

        return factoryInstance;
    }

    /**
     * A convenience method that returns a new instance of an <codeAttributeFactory</code> that
     * supports all of the standard datatypes. The new factory allows adding support for new
     * datatypes. This method should only be used when you need a new, mutable instance (eg, when
     * you want to create a new factory that extends the set of supported datatypes). In general,
     * you should use <code>getFactory</code> which is more efficient and enforces a singleton
     * pattern.
     * 
     * @return a new factory supporting the standard datatypes
     */
    public static AttributeFactory getNewFactory() {
        // first we make sure that everything has been initialized...
        getFactory();

        // ...then we create the new instance
        return new BaseAttributeFactory(supportedDatatypes);
    }

    /**
     * Returns the identifiers supported for the given version of XACML. Because this factory
     * supports identifiers from all versions of the XACML specifications, this method is useful for
     * getting a list of which specific identifiers are supported by a given version of XACML.
     * 
     * @param xacmlVersion a standard XACML identifier string, as provided in
     *            <code>PolicyMetaData</code>
     * 
     * @return a <code>Set</code> of identifiers
     * 
     * @throws UnknownIdentifierException if the version string is unknown
     */
    public static Set getStandardDatatypes(String xacmlVersion) throws UnknownIdentifierException {
        if (xacmlVersion.equals(XACMLConstants.XACML_1_0_IDENTIFIER)) {
            return supportedV1Identifiers;
        } else if (xacmlVersion.equals(XACMLConstants.XACML_2_0_IDENTIFIER)) {
            return supportedV2Identifiers;
        } else if(xacmlVersion.equals(XACMLConstants.XACML_3_0_IDENTIFIER)){
            return supportedV3Identifiers;
        }

        throw new UnknownIdentifierException("Unknown XACML version: " + xacmlVersion);
    }

    /**
     * Throws an <code>UnsupportedOperationException</code> since you are not allowed to modify what
     * a standard factory supports.
     * 
     * @param id the name of the attribute type
     * @param proxy the proxy used to create new attributes of the given type
     * 
     * @throws UnsupportedOperationException always
     */
    public void addDatatype(String id, AttributeProxy proxy) {
        throw new UnsupportedOperationException("a standard factory cannot "
                + "support new datatypes");
    }

}
