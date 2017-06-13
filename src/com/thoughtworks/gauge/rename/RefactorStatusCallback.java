package com.thoughtworks.gauge.rename;

interface RefactorStatusCallback {
    void changeStatus(String message);

    void showErrorMessage(String text);

    void cancelDialog();
}
