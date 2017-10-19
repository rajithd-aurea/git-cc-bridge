package com.redknee.cc;

import org.apache.commons.lang3.StringUtils;

public class ClearCaseCommandBuilder {

    private static final String CLEAR_TOOL_VIEW_COMMAND = "/usr/atria/bin/cleartool setview -exec \" %s \" %s";

    private static final String CHECK_OUT_COMMAND = "cd %s && /usr/atria/bin/cleartool checkout -reserved -nc %s";

    private static final String CHECK_IN_COMMAND = "cd %s && /usr/atria/bin/cleartool ci -c '%s' -ide %s";

    private static final String COPY_FILE_COMMAND = "cp %s %s";

    private static final String REMOVE_FILE_COMMAND = "/usr/atria/bin/cleartool rmelem -c '%s' -force %s";

    private static final String CREATE_LABEL_COMMAND = "cd %s && /usr/atria/bin/cleartool mklbtype -nc %s";

    private static final String ATTACH_LABEL_TO_FILES_COMMAND = "/usr/atria/bin/cleartool mklabel %s %s";

    private static final String REMOVE_LABEL_COMMAND = "cd %s && /usr/atria/bin/cleartool rmtype -rmall -force lbtype:%s";

    private static final String CREATE_BRANCH_TYPE_COMMAND = "cd %s && /usr/atria/bin/cleartool mkbrtype -nc %s";

    private static final String ASSIGN_BRANCH_TO_ELEMENTS_COMMAND = "cd %s && /usr/atria/bin/cleartool mkbranch -nc -nco %s %s";

    private static final String CREATE_NEW_FILES_AND_DIRS_COMMAND = "cd %s && /usr/atria/bin/clearfsimport -comment '%s' -rec -nset %s .";

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

    public static String buildCreateNewFilesAndDirsCommand(String viewName, String vobPath, String commitMessage,
            String dirPath) {
        String command = String.format(CREATE_NEW_FILES_AND_DIRS_COMMAND, vobPath, commitMessage, dirPath);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, command, viewName);
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

    public static String buildCreateBranchTypeCommand(String viewName, String vobPath, String branchName) {
        String command = String.format(CREATE_BRANCH_TYPE_COMMAND, vobPath, branchName);
        return String.format(CLEAR_TOOL_VIEW_COMMAND, command, viewName);
    }

    public static String buildAssignBranchToElementsCommand(String viewName, String vobPath, String branchName,
            String... fileNames) {
        String command = String
                .format(ASSIGN_BRANCH_TO_ELEMENTS_COMMAND, vobPath, branchName, StringUtils.join(fileNames, " "));
        return String.format(CLEAR_TOOL_VIEW_COMMAND, command, viewName);
    }
}
