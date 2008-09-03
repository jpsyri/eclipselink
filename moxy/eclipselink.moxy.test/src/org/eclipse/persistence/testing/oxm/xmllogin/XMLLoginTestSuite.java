/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhou - new test cases for XMLLogin
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmllogin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLLoginTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLLogin Test Suite");

        suite.addTestSuite(XMLLoginDeploymentXMLTestCases.class);
        suite.addTestSuite(XMLLoginSessionsXMLTestCases.class);
        
        return suite;
    }
    
}