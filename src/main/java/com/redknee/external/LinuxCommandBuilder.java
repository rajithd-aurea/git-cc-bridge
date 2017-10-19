package com.redknee.external;

import com.redknee.service.exception.ApiException;

public class LinuxCommandBuilder {

    private static final String CREATE_DIR_COMMAND = "mkdir -p %s";

    private static final String REMOVE_DIR_COMMAND = "rm -rf %s";

    public static String buildCreateDirCommand(String path) {
        return String.format(CREATE_DIR_COMMAND, path);
    }

    public static String buildRemoveDirCommand(String path) {
        if ("*".equals(path) || "/".equals(path)) {
            throw new ApiException(String.format("Invalid path to remove %s", path));
        }
        return String.format(REMOVE_DIR_COMMAND, path);
    }

}
