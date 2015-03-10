// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.Query;
import com.thoughtworks.gauge.ConceptInfo;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptConceptImpl;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.reference.ReferenceCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.thoughtworks.gauge.GaugeConstant.STEP_ANNOTATION_QUALIFIER;

public class StepUtil {

    public static PsiElement findStepImpl(SpecStep step, Module module) {
        ReferenceCache cache = Gauge.getReferenceCache(module);
        PsiElement reference = cache.searchReferenceFor(step);
        if (reference == null) {
            reference = findStepReference(step, module);
        }
        return reference;
    }

    private static PsiElement findStepReference(SpecStep step, Module module) {
        Collection<PsiMethod> stepMethods = getStepMethods(module.getProject());
        PsiMethod method = filter(stepMethods, step);
        PsiElement referenceElement;
        if (method == null) {
            referenceElement = searchConceptsForImpl(step, module);
            if (referenceElement != null){
                referenceElement = new ConceptStepImpl(referenceElement.getNode(),true);
            }
        } else {
            referenceElement = method.getChildren()[0].getChildren()[0];
        }
        addReferenceToCache(step, referenceElement, module);
        return referenceElement;
    }

    private static void addReferenceToCache(SpecStep step, PsiElement referenceElement, Module module) {
        Gauge.getReferenceCache(module).addStepReference(step, referenceElement);
    }

    private static PsiElement searchConceptsForImpl(SpecStep step, Module module) {
        try {
            List<ConceptInfo> conceptInfos = fetchAllConcepts(ModuleUtil.findModuleForPsiElement(step));
            for (ConceptInfo conceptInfo : conceptInfos) {
                String conceptName = conceptInfo.getStepValue().getStepAnnotationText().trim();
                if (conceptInfo.getStepValue().getStepText().equals(step.getStepValue().getStepText())) {
                    PsiFile[] conceptFiles = findConceptFiles(conceptInfo, module);
                    if (conceptFiles.length > 0) {
                        for (PsiElement psiElement : conceptFiles[0].getChildren()) {
                            boolean isConcept = psiElement.getClass().equals(ConceptConceptImpl.class);
                            if (psiElement.getClass().equals(ConceptConceptImpl.class)) {
                                String conceptHeading = ((ConceptConceptImpl) psiElement).getConceptHeading().getText().replaceFirst("#", "").trim();
                                if (isConcept && (conceptHeading.equals(conceptName) || conceptHeading.startsWith(conceptName.trim() + "\n")))
                                    return psiElement;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private static PsiFile[] findConceptFiles(ConceptInfo conceptInfo, Module module) {
        return FilenameIndex.getFilesByName(module.getProject(), getConceptFileName(conceptInfo), GlobalSearchScope.allScope(module.getProject()));
    }

    private static String getConceptFileName(ConceptInfo conceptInfo) {
        return new File(conceptInfo.getFilePath()).getName();
    }

    private static PsiMethod filter(Collection<PsiMethod> stepMethods, SpecStep step) {
        String stepText = step.getStepValue().getStepText();
        for (PsiMethod stepMethod : stepMethods) {
            if (isMatch(stepMethod, stepText)) {
                return stepMethod;
            }
        }
        return null;
    }

    public static boolean isMatch(PsiMethod stepMethod, String stepText) {
        final PsiModifierList modifierList = stepMethod.getModifierList();
        final PsiAnnotation[] annotations = modifierList.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotationTextMatches(annotation, stepText)) {
                return true;
            }
        }
        return false;
    }

    private static boolean annotationTextMatches(PsiAnnotation annotation, String stepValue) {
        Module moduleForPsiElement = ModuleUtil.findModuleForPsiElement(annotation);
        GaugeService gaugeService = Gauge.getGaugeService(moduleForPsiElement);
        if (gaugeService != null) {
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            for (String annotationValue : getGaugeStepAnnotationValues(annotation)) {
                String methodValue = gaugeConnection.getStepValue(annotationValue).getStepText();
                if (methodValue.equals(stepValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> getGaugeStepAnnotationValues(PsiAnnotation annotation) {
        List<String> values = new ArrayList<String>();
        PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue("value");
        Object value = JavaPsiFacade.getInstance(annotation.getProject()).getConstantEvaluationHelper().computeConstantExpression(attributeValue);
        if (value != null && value instanceof String) {
            values.add((String) value);
        } else if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] memberValues = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            for (PsiAnnotationMemberValue memberValue : memberValues) {
                Object val = JavaPsiFacade.getInstance(annotation.getProject()).getConstantEvaluationHelper().computeConstantExpression(memberValue);
                if (val != null && val instanceof String) {
                    values.add((String) val);
                }
            }
        }
        return values;
    }


    public static Collection<PsiMethod> getStepMethods(Project project) {
        final PsiClass step = JavaPsiFacade.getInstance(project).findClass(STEP_ANNOTATION_QUALIFIER, GlobalSearchScope.allScope(project));
        if (step != null) {
            final Query<PsiMethod> psiMethods = AnnotatedElementsSearch.searchPsiMethods(step, GlobalSearchScope.allScope(project));
            return psiMethods.findAll();
        }
        return new ArrayList<PsiMethod>();
    }


    public static boolean isImplementedStep(SpecStep step, Module module) {
        return findStepImpl(step, module) != null;
    }

    //Check if the step is a concept using list of concepts got from gauge API
    private static boolean isConcept(SpecStep step) {
        try {
            Module module = ModuleUtil.findModuleForPsiElement(step);
            List<ConceptInfo> conceptInfos = fetchAllConcepts(module);
            return conceptExists(conceptInfos, step.getStepValue());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<ConceptInfo> fetchAllConcepts(Module module) throws IOException {
        GaugeService gaugeService = Gauge.getGaugeService(module);
        if (gaugeService != null) {
            return gaugeService.getGaugeConnection().fetchAllConcepts();
        }

        return new ArrayList<ConceptInfo>();
    }

    private static boolean conceptExists(List<ConceptInfo> conceptInfos, StepValue stepValue) {
        for (ConceptInfo conceptInfo : conceptInfos) {
            if (conceptInfo.getStepValue().getStepText().trim().equals(stepValue.getStepText().trim())) {
                return true;
            }
        }
        return false;
    }
}