/*
 * file CcDemoProps.java
 *
 * Licensed Materials - Property of IBM
 * Restricted Materials of IBM - you are allowed to copy, modify and 
 * redistribute this file as part of any program that interfaces with 
 * IBM Rational CM API.
 *
 * com.ibm.rational.stp.client.samples.cc.CcDemoProps
 *
 * (C) Copyright IBM Corporation 2008, 2010.  All Rights Reserved.
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP  Schedule Contract with IBM Corp.
 */

package com.redknee;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class CcDemoProps {

    /**
     * The name of the default demo property file
     */
    private static final String PROPS_FILE = "cc_cmapi.props";
    private static Properties m_props;
    private final File m_propsFile;

    /**
     * Construct instance using the default properties file $HOME/cc_cmapi.props
     */
    public CcDemoProps() throws Exception {
        this(getDefaultPropsFile());
    }

    /**
     * Construct instance using the specified properties file.
     */
    public CcDemoProps(File propsFile) throws Exception {
        InputStream propStream = new FileInputStream(propsFile);
        m_props = new Properties();
        m_props.load(propStream);
        m_propsFile = propsFile;
    }

    /**
     * User's OS login name on the CM Server.
     */
    public String getLoginName() {
        return getRequired("loginName");
    }

    /**
     * User's OS login domain on the CM Server.
     * Typically required on Windows CM Server but not on Unix
     */
    public String getLoginDomain() {
        return m_props.getProperty("loginDomain");
    }

    /**
     * User's OS login name and domain on the CM Server in the form
     * <code>DOMAIN\loginname</code>.
     */
    public String getLoginNameAndDomain() {
        String domain = getLoginDomain();
        if (domain != null && domain.length() > 0) {
            return domain + "\\" + getLoginName();
        } else {
            return getLoginName();
        }
    }

    /**
     * CearCase domain password on the CM Server.
     */
    public String getPassword() {
        return getRequired("password");
    }

    /**
     * CearCase primary group on the CM Server.
     */
    public String getPrimaryGroup() {
        return m_props.getProperty("primaryGroup");
    }

    /**
     * User's ClearQuest login name on the CM Server.
     */
    public String getCqLoginName() {
        return getRequired("cqLoginName");
    }

    /**
     * User's ClearQuest password on the CM Server.
     */
    public String getCqPassword() {
        return getRequired("cqPassword");
    }

    /**
     * User's HTTP proxy login.
     */
    public String getHttpProxyLoginName() {
        return getRequired("httpProxyLoginName");
    }

    /**
     * User's HTTP proxy password.
     */
    public String getHttpProxyPassword() {
        return getRequired("httpProxyPassword");
    }

    /**
     * Forward HTTP proxy host.
     */
    public String getHttpProxyHost() {
        return m_props.getProperty("httpProxyHost");
    }

    /**
     * Forward HTTP proxy port number.
     */
    public String getHttpProxyPort() {
        return m_props.getProperty("httpProxyPort");
    }

    /**
     * URL of CM Server.
     */
    public String getCmServerUrl() {
        return getRequired("serverUrl");
    }

    /**
     * VOB tag of the demo UCM project VOB.
     */
    public String getDemoUcmProjectVob() {
        return getRequired("demoUcmProjectVob");
    }

    /**
     * Client-side temporary directory for creating temp web views, etc.
     * E.g., "c:\temp", /var/tmp
     */
    public File getTempDir() {
        return new File(getRequired("tempDir"));
    }

    private String getRequired(String name) {
        String value = m_props.getProperty(name);
        if (value == null || value.length() == 0) {
            throw new IllegalStateException("Missing required property '" + name
                    + "' in property file: " + m_propsFile);
        }
        return m_props.getProperty(name);
    }

    /**
     * Get the default properties file.  By default, look in the current user's
     * home directory for a file called "cc_cmapi.props".
     */
    private static File getDefaultPropsFile() throws Exception {
        File propsFile = new File(System.getProperty("user.home"), PROPS_FILE);
        if ( ! propsFile.exists()) {
            throw new Exception("Missing property file: " + propsFile);
        }
        return propsFile;
    }
}
