/*
 * file ListVobsDemo.java
 *
 * Licensed Materials - Property of IBM
 * Restricted Materials of IBM - you are allowed to copy, modify and 
 * redistribute this file as part of any program that interfaces with 
 * IBM Rational CM API.
 *
 * com.ibm.rational.stp.client.samples.cc.ListVobsDemo
 *
 * (C) Copyright IBM Corporation 2008, 2010.  All Rights Reserved.
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP  Schedule Contract with IBM Corp.
 */

package com.redknee;

import javax.wvcm.Resource;
import javax.wvcm.PropertyRequestItem.PropertyRequest;

import com.ibm.rational.wvcm.stp.cc.CcProvider;
import com.ibm.rational.wvcm.stp.cc.CcRegistryRegion;
import com.ibm.rational.wvcm.stp.cc.CcVobTag;

/**
 * TODO
 */
public class ListVobsDemo extends CcDemoBase {

    private CcProvider m_provider;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        m_provider = getClearCaseProvider();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * List the VOBs (actually VOB tags) in the CM Server's default ClearCase
     * registry region.
     */
    public void testListVobs() throws Exception {
        PropertyRequest wantedProps = new PropertyRequest(
                CcRegistryRegion.VOB_TAG_LIST.nest(
                        Resource.DISPLAY_NAME));
        CcRegistryRegion region = m_provider.doGetDefaultCcRegistryRegion(wantedProps);

        for (CcVobTag vobTag : region.getVobTagList()) {
            trace(vobTag.getDisplayName());
        }
    }
}
