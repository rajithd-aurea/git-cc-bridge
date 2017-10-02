package com.redknee.cc;

import com.ibm.rational.clearcase.remote_core.rpc.IClearCaseUserCredentials;

public class Credentials implements IClearCaseUserCredentials {

    private final String username;
    private final String password;
    private final String group;

    public Credentials(String username, String password, String group) {
        super();
        this.username = username;
        this.password = password;
        this.group = group;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getGroupname() {
        return group;
    }

    @Override
    public String getWindowsDomain() {
        return null;
    }
}
