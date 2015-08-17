package com.thoughtworks.gauge.extract;

public class ExtractConceptInfo {
    public final String conceptName;
    public final String fileName;
    public final Boolean cancelled;

    public ExtractConceptInfo(String conceptName, String fileName, Boolean cancelled) {
        this.conceptName = conceptName;
        this.fileName = fileName;
        this.cancelled = cancelled;
    }
}
