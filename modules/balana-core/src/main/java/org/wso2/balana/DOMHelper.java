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
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;

/**
 * This class is created for patch provided in IDENTITY-416
 *
 * XML parsing via DOM API the method node.getLocalName() or getNodeName() is used. But not all parsed
 * nodes are elements or attribute. to support namespaces  it is needed to either access via node.getLocalName()
 * or DOMHelper.getLocalName(node) depending on the node type.
 */
public class DOMHelper {

	public static String getLocalName(Node child) {

		String localName = child.getLocalName();
		if (localName == null) return child.getNodeName();
		return localName;

	}
}
