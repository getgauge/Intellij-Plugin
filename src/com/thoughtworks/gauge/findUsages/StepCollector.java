package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.thoughtworks.gauge.annotator.FileManager;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.stub.FileStub;

import java.util.*;

public class StepCollector {

    private Project project;
    private HashMap<PsiFile, List<Integer>> map = new HashMap<PsiFile, List<Integer>>();

    public StepCollector(Project project) {
        this.project = project;
    }

    public StepsCollection getAllSteps() {
        List<VirtualFile> conceptFiles = FileManager.getConceptFiles(project);
        List<VirtualFile> files = FileManager.getAllSpecFiles(project);
        files.addAll(conceptFiles);
        for (VirtualFile file : files) {
            List<List<Integer>> values = FileBasedIndex.getInstance().getValues(FileStub.NAME, file.getPath(), GlobalSearchScope.allScope(project));
            if (values.size() > 0)
                map.put(PsiManager.getInstance(project).findFile(file), values.get(0));
            else
                map.put(PsiManager.getInstance(project).findFile(file), new ArrayList<Integer>());
        }
        return getSteps();
    }

    public StepsCollection getSteps() {
        List<SpecStepImpl> specSteps = new ArrayList<SpecStepImpl>();
        List<ConceptStepImpl> conceptSteps = new ArrayList<ConceptStepImpl>();
        for (PsiFile psiFile : map.keySet()) {
            Set<Integer> offsets = new HashSet<Integer>(map.get(psiFile));
            for (Integer offset : offsets) {
                PsiElement stepElement = getStepElement(psiFile.findElementAt(offset));
                if (stepElement == null) {
                    continue;
                }
                if (stepElement.getClass().equals(SpecStepImpl.class))
                    specSteps.add((SpecStepImpl) stepElement);
                else
                    conceptSteps.add((ConceptStepImpl) getStepElement(psiFile.findElementAt(offset)));
            }
        }
        return new StepsCollection(specSteps, conceptSteps);
    }

    private PsiElement getStepElement(PsiElement selectedElement) {
        if (selectedElement == null) return null;
        if (selectedElement.getClass().equals(SpecStepImpl.class) || selectedElement.getClass().equals(ConceptStepImpl.class))
            return selectedElement;
        return getStepElement(selectedElement.getParent());
    }
}
