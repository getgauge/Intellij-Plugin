package com.thoughtworks.gauge.extract;

public class ExtractConceptInfo {
    public final String conceptName;
    public final String fileName;
    public final Boolean shouldContinue;

    public ExtractConceptInfo(String conceptName, String fileName, Boolean shouldContinue) {
        this.conceptName = conceptName;
        this.fileName = fileName;
        this.shouldContinue = shouldContinue;
    }
}
