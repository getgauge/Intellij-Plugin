package com.thoughtworks.gauge.rename;

public class RefactoringStatus {

    private String errorMessage;
    private Boolean isPassed;

    public RefactoringStatus(Boolean isPassed, String errorMessage) {
        this.errorMessage = errorMessage;
        this.isPassed = isPassed;
    }

    public RefactoringStatus(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean isPassed() {
        return isPassed;
    }
}
