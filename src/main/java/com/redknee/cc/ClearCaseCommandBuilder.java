package com.redknee.cc;

import org.apache.commons.lang3.StringUtils;

public class ClearCaseCommandBuilder {

    private static final String CLEAR_TOOL_VIEW_COMMAND = "/usr/atria/bin/cleartool setview -exec \" %s \" %s";

    private static final String CHECK_OUT_COMMAND = "cd %s && /usr/atria/bin/cleartool checkout -reserved -nc %s";

    private static final String CHECK_IN_COMMAND = "cd %s && /usr/atria/bin/cleartool ci -c '%s' -ide %s";

    private static final String COPY_FILE_COMMAND = "cp %s %s";

    private static final String MK_ELEM_COMMAND = "cd %s && /usr/atria/bin/cleartool mkelem -c '%s' %s";

    private static final String MK_DIR_COMMAND = "cd %s && /usr/atria/bin/cleartool mkdir -c '%s' %s";

    private static final String REMOVE_FILE_COMMAND = "/usr/atria/bin/cleartool rmelem -c '%s' -force %s";

    private static final String CREATE_LABEL_COMMAND = "cd %s && /usr/atria/bin/cleartool mklbtype -nc %s";

    private static final String ATTACH_LABEL_TO_FILES_COMMAND = "/usr/atria/bin/cleartool mklabel %s %s";

    private static final String REMOVE_LABEL_COMMAND = "cd %s && /usr/atria/bin/cleartool rmtype -rmall -force lbtype:%s";

    public static String buildCheckOutCommand(String viewName, String path, String fileName) {
        String checkoutCommand = String.format(CHECK_OUT_COMMAND, path, fileName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, checkoutCommand, viewName);
    }

    public static String buildCheckInCommand(String viewName, String path, String fileName, String message) {
        String checkInCommand = String.format(CHECK_IN_COMMAND, path, message, fileName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, checkInCommand, viewName);
    }

    public static String buildCopyCommand(String viewName, String sourceFile, String destinationFile) {
        String copyCommand = String.format(COPY_FILE_COMMAND, sourceFile, destinationFile);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, copyCommand, viewName);
    }

    public static String buildMakeElementCommand(String viewName, String path, String commitMessage, String fileName) {
        String makeElementCommand = String.format(MK_ELEM_COMMAND, path, commitMessage, fileName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, makeElementCommand, viewName);
    }

    public static String buildMakeDirCommand(String viewName, String path, String commitMessage, String dirName) {
        String makeDirCommand = String.format(MK_DIR_COMMAND, path, commitMessage, dirName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, makeDirCommand, viewName);
    }

    public static String buildRemoveElementCommand(String viewName, String commitMessage,
            String filePath) {
        String removeCommand = String.format(REMOVE_FILE_COMMAND, commitMessage, filePath);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, removeCommand, viewName);
    }

    public static String buildCreateLabelCommand(String viewName, String vobPath, String labelName) {
        String createLabelCommand = String.format(CREATE_LABEL_COMMAND, vobPath, labelName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, createLabelCommand, viewName);
    }

    public static String buildAttachLabelCommand(String viewName, String labelName, String... fileNames) {
        String command = String.format(ATTACH_LABEL_TO_FILES_COMMAND, labelName, StringUtils.join(fileNames, " "));
        return String.format(CLEAR_TOOL_VIEW_COMMAND, command, viewName);
    }

    public static String buildRemoveLabelCommand(String viewName, String vobPath, String labelName) {
        String command = String.format(REMOVE_LABEL_COMMAND, vobPath, labelName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, command, viewName);
    }
}
