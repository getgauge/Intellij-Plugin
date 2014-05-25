package com.thoughtworks.gauge.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.language.psi.StepElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StepUtil {
    public static List<StepElement> findSteps(Project project) {
        List<StepElement> result = new ArrayList<StepElement>();
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SpecFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            SpecFile simpleFile = (SpecFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                StepElement[] step = PsiTreeUtil.getChildrenOfType(simpleFile, StepElement.class);
                if (step != null) {
                    Collections.addAll(result, step);
                }
            }
        }
        return result;
    }
}
