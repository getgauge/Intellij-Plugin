package com.thoughtworks.gauge.rename;

interface RefactorStatusCallback {
    void onStatusChange(String statusMessage);

    void onFinish(RefactoringStatus refactoringStatus);
}
