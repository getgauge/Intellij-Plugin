package com.thoughtworks.gauge.language;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SpecFile extends PsiFileBase {
    public SpecFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, Specification.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SpecFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Specification File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}