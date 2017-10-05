package com.redknee.external;

public class LinuxCommandBuilder {

    private static final String CREATE_DIR_COMMAND = "mkdir -p %s";

    public static String buildCreateDirCommand(String path) {
        return String.format(CREATE_DIR_COMMAND, path);
    }

}
