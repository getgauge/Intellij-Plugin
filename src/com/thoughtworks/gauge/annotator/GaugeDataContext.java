package com.thoughtworks.gauge.annotator;

import com.intellij.ide.IdeView;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class GaugeDataContext implements DataContext {
    private final DataContext dataContext;

    public GaugeDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        if (dataId.equals("IDEView")) {
            return new MyIdeView(LangDataKeys.PROJECT.getData(dataContext));
        } else {
            return dataContext.getData(dataId);
        }

    }

    private static class MyIdeView implements IdeView {
        private final Project project;

        private MyIdeView(Project project) {
            this.project = project;
        }

        @Override
        public void selectElement(PsiElement element) {

        }

        @Override
        public PsiDirectory[] getDirectories() {
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile root : ProjectRootManager.getInstance(psiManager.getProject()).getContentSourceRoots()) {
                PsiDirectory directory = psiManager.findDirectory(root);
                if (directory != null) {
                    return new PsiDirectory[]{directory};
                }
            }
            return PsiDirectory.EMPTY_ARRAY;
        }

        @Nullable
        @Override
        public PsiDirectory getOrChooseDirectory() {
            return DirectoryChooserUtil.getOrChooseDirectory(this);
        }
    }
}
