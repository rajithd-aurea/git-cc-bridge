/*
 * file CcDemoBase.java
 *
 * Licensed Materials - Property of IBM
 * Restricted Materials of IBM - you are allowed to copy, modify and 
 * redistribute this file as part of any program that interfaces with 
 * IBM Rational CM API.
 *
 * com.ibm.rational.stp.client.samples.cc.CcDemoBase
 *
 * (C) Copyright IBM Corporation 2008, 2010.  All Rights Reserved.
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP  Schedule Contract with IBM Corp.
 */

package com.redknee;

import java.io.File;
import javax.wvcm.ProviderFactory;
import javax.wvcm.WvcmException;
import javax.wvcm.ProviderFactory.Callback;
import javax.wvcm.WvcmException.ReasonCode;

import junit.framework.TestCase;

import com.ibm.rational.wvcm.stp.StpProvider;
import com.ibm.rational.wvcm.stp.cc.CcProvider;

/**
 * TODO
 */
public class CcDemoBase extends TestCase {

    public class DefaultCallback implements Callback {

        public Authentication getAuthentication(String realm, int retryCount)
            throws WvcmException
        {
            trace("Getting credentials");
            if (retryCount > 0) {
                throw new WvcmException("Invalid credentials", ReasonCode.UNAUTHORIZED);
            }
            return new Authentication() {
                public String loginName() {
                    return props().getLoginNameAndDomain();
                }
                public String password() {
                    return props().getPassword();
                }
            };
        }
    }

    private CcDemoProps m_props;
    private CcProvider m_provider;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // Read the current user's properties file.
        m_props = new CcDemoProps(new File("/Users/rajith/projects/crossover/redknee/git-cc-bridge/git-cc-bridge/src/test/resources/cc_cmapi.props"));

        // Instantiate a ClearCase CM API provider
        StpProvider provider = (StpProvider) ProviderFactory.createProvider(
                CcProvider.PROVIDER_CLASS,
                new DefaultCallback());
        provider.setServerUrl(props().getCmServerUrl());
        m_provider = provider.ccProvider();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    CcDemoProps props() {
        return m_props;
    }

    CcProvider getClearCaseProvider() {
        return m_provider;
    }

    void trace(String msg) {
        System.out.println(msg);
    }
}
