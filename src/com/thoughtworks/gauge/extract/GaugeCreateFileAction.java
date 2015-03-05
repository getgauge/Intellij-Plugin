package com.thoughtworks.gauge.extract;

import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GaugeCreateFileAction extends CreateFileAction {
    private String selectedText;
    private PsiFile psiFile;

    public GaugeCreateFileAction(String selectedText, PsiFile psiFile) {
        this.selectedText = selectedText;
        this.psiFile = psiFile;
    }

    @NotNull
    @Override
    protected PsiElement[] create(String s, PsiDirectory psiDirectory) throws Exception {
        Project project = psiDirectory.getProject();
        String path = psiFile.getContainingDirectory().getVirtualFile().getPath() + File.separator;
        File file1 = new File(path +s);
        final PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(file1.getName(),selectedText);
        file1  = new File((path+s).replace(file1.getName(),""));
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(LocalFileSystem.getInstance().findFileByIoFile(file1));
        directory.add(file);
        return new PsiElement[]{directory};
    }
}
