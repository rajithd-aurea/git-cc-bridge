package com.redknee.cc;

public class ClearCaseCommandBuilder {

    private static final String CLEAR_TOOL_VIEW_COMMAND = "/usr/atria/bin/cleartool setview -exec \" %s \" %s";

    private static final String CHECK_OUT_COMMAND = "cd %s && /usr/atria/bin/cleartool checkout -reserved -nc %s";

    private static final String CHECK_IN_COMMAND = "cd %s && /usr/atria/bin/cleartool ci -c '%s' %s";

    public static String buildCheckOutCommand(String viewName, String path, String fileName) {
        String checkoutCommand = String.format(CHECK_OUT_COMMAND, path, fileName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, checkoutCommand, viewName);
    }

    public static String buildCheckInCommand(String viewName, String path, String fileName, String message) {
        String checkInCommand = String.format(CHECK_IN_COMMAND, path, message, fileName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, checkInCommand, viewName);
    }
}
