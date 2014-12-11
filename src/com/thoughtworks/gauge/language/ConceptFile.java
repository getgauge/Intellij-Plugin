package com.thoughtworks.gauge.language;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class ConceptFile extends PsiFileBase {
    public ConceptFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, Concept.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ConceptFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Concept File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
