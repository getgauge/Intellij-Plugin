package com.thoughtworks.gauge.inspection;

import com.intellij.openapi.util.text.StringUtil;
import com.thoughtworks.gauge.Constants;

public class GaugeError {
    private String type;
    private String fileName;
    private int lineNumber;
    private String message;

    public GaugeError(String type, String fileName, int lineNumber, String message) {
        this.type = type;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.message = String.format("%s line number: %d, %s", this.type, lineNumber, message);
    }

    boolean isFrom(String fileName) {
        return this.fileName.equals(fileName);
    }

    public String getMessage() {
        return message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    int getOffset(String text) {
        return StringUtil.lineColToOffset(text, lineNumber - 1, 0);
    }

    public static GaugeError getInstance(String error) {
        try {
            String[] parts = error.split(" ");
            String[] fileInfo = parts[1].split(Constants.SPEC_SCENARIO_DELIMITER);
            return new GaugeError(parts[0], fileInfo[0], Integer.parseInt(fileInfo[1]), error.split(":\\d+:? ")[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
