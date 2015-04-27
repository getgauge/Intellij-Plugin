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
    private HashMap<String, List<PsiElement>> stepTextToElement;

    public StepCollector(Project project) {
        this.project = project;
        stepTextToElement = new HashMap<String, List<PsiElement>>();
    }

    public void collect() {
        List<VirtualFile> conceptFiles = FileManager.getConceptFiles(project);
        List<VirtualFile> files = FileManager.getAllSpecFiles(project);
        files.addAll(conceptFiles);
        for (VirtualFile file : files) {
            List<Set<Integer>> values = FileBasedIndex.getInstance().getValues(FileStub.NAME, file.getPath(), GlobalSearchScope.allScope(project));
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (values.size() > 0) getSteps(psiFile, new HashSet<Integer>(values.get(0)));
        }
    }

    public List<PsiElement> get(String stepText) {
        return stepTextToElement.get(stepText) == null ? new ArrayList<PsiElement>() : stepTextToElement.get(stepText);
    }

    private void getSteps(PsiFile psiFile, Set<Integer> offsets) {
        for (Integer offset : offsets) {
            PsiElement stepElement = getStepElement(psiFile.findElementAt(offset));
            if (stepElement == null) continue;
            if (stepElement.getClass().equals(SpecStepImpl.class))
                addElement(stepElement, removeIdentifiers(((SpecStepImpl) stepElement).getStepValue().getStepText()));
            else
                addElement(stepElement, removeIdentifiers(((ConceptStepImpl) stepElement).getStepValue().getStepText()));
        }
    }

    private String removeIdentifiers(String text) {
        return text.charAt(0) == '*' || text.charAt(0) == '#' ? text.substring(1).trim() : text.trim();
    }

    private void addElement(PsiElement stepElement, String stepText) {
        List<PsiElement> elementsList = stepTextToElement.get(stepText);
        if (elementsList == null) {
            List<PsiElement> elements = new ArrayList<PsiElement>();
            elements.add(stepElement);
            stepTextToElement.put(stepText, elements);
            return;
        }
        elementsList.add(stepElement);
    }

    private PsiElement getStepElement(PsiElement selectedElement) {
        if (selectedElement == null) return null;
        if (selectedElement.getClass().equals(SpecStepImpl.class) || selectedElement.getClass().equals(ConceptStepImpl.class))
            return selectedElement;
        return getStepElement(selectedElement.getParent());
    }
}