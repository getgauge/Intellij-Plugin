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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.connection.GaugeConnection;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptConceptImpl;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.reference.ReferenceCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class StepUtil {

    private static HashMap<String, StepValue> stepValueCache = new HashMap<>();

    public static PsiElement findStepImpl(SpecStep step, Module module) {
        if (module == null) {
            return null;
        }
        ReferenceCache cache = Gauge.getReferenceCache(module);
        PsiElement reference = cache.searchReferenceFor(step);
        if (reference == null) {
            reference = findStepReference(step, module);
        }
        return reference;
    }

    private static PsiElement findStepReference(SpecStep step, Module module) {
        Collection<PsiMethod> stepMethods = getStepMethods(module);
        PsiMethod method = findStepImplementationMethod(stepMethods, step, module);
        PsiElement referenceElement;
        if (method == null) {
            referenceElement = searchConceptsForImpl(step, module);
            if (referenceElement != null) {
                referenceElement = new ConceptStepImpl(referenceElement.getNode(), true);
            }
        } else {
            referenceElement = method;
        }
        addReferenceToCache(step, referenceElement, module);
        return referenceElement;
    }

    private static void addReferenceToCache(SpecStep step, PsiElement referenceElement, Module module) {
        Gauge.getReferenceCache(module).addStepReference(step, referenceElement);
    }

    private static PsiElement searchConceptsForImpl(SpecStep step, Module module) {
        try {
            VirtualFile[] conceptFiles = findConceptFiles(module);
            if (conceptFiles.length > 0) {
                PsiElement reference;
                for (VirtualFile file : conceptFiles) {
                    reference = searchConceptReference(file, step, module);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static PsiElement searchConceptReference(VirtualFile virtualFile, SpecStep step, Module module) {
        PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(virtualFile);
        if (psiFile == null || !psiFile.isValid()) {
            return null;
        }
        for (PsiElement psiElement : psiFile.getChildren()) {
            boolean isConcept = psiElement.getClass().equals(ConceptConceptImpl.class);
            if (psiElement.getClass().equals(ConceptConceptImpl.class)) {
                StepValue conceptStepValue = ((ConceptConceptImpl) psiElement).getStepValue();
                if (isConcept && step.getStepValue().getStepText().equals(conceptStepValue.getStepText()))
                    return psiElement;
            }
        }
        return null;
    }

    private static VirtualFile[] findConceptFiles(Module module) {
        Collection<VirtualFile> conceptFiles = FilenameIndex.getAllFilesByExt(module.getProject(), Constants.CONCEPT_EXTENSION);
        return conceptFiles.toArray(new VirtualFile[conceptFiles.size()]);
    }

    private static PsiMethod findStepImplementationMethod(Collection<PsiMethod> stepMethods, SpecStep step, Module module) {
        String stepText = step.getStepValue().getStepText();
        for (PsiMethod stepMethod : stepMethods) {
            if (isMatch(stepMethod, stepText, module)) {
                return stepMethod;
            }
        }
        return null;
    }

    public static boolean isMatch(PsiMethod stepMethod, String stepText, Module module) {
        List<String> annotationValues = getGaugeStepAnnotationValues(stepMethod);
        for (String value : annotationValues)
            if (SpecPsiImplUtil.getStepValueFor(module, stepMethod, value, false).getStepText().equals(stepText))
                return true;
        return false;
    }

    public static StepValue getStepValue(final GaugeConnection connection, final String text, Boolean hasInlineTable) {
        String stepText = hasInlineTable ? text + " <table>" : text;
        StepValue value = stepValueCache.get(stepText);
        if (value == null || value.getStepText().isEmpty()) {
            value = connection.getStepValue(text, hasInlineTable);
            stepValueCache.put(stepText, value);
        }
        return value;
    }

    public static List<String> getGaugeStepAnnotationValues(PsiMethod stepMethod) {
        final PsiModifierList modifierList = stepMethod.getModifierList();
        final PsiAnnotation[] annotations = modifierList.getAnnotations();
        List<String> values = new ArrayList<>();
        for (PsiAnnotation annotation : annotations) {
            values.addAll(getGaugeStepAnnotationValues(annotation));
        }
        return values;
    }

    private static List<String> getGaugeStepAnnotationValues(PsiAnnotation annotation) {
        List<String> values = new ArrayList<>();
        if (!isGaugeAnnotation(annotation)) {
            return values;
        }
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

    private static boolean isGaugeAnnotation(PsiAnnotation annotation) {
        return Step.class.getCanonicalName().equals(annotation.getQualifiedName());
    }


    public static Collection<PsiMethod> getStepMethods(Module module) {
        final PsiClass step = JavaPsiFacade.getInstance(module.getProject()).findClass("com.thoughtworks.gauge.Step", GlobalSearchScope.allScope(module.getProject()));
        if (step != null) {
            Collection<PsiMethod> methods = new ArrayList<>();
            for (Module m : Gauge.getSubModules(module))
                methods.addAll(AnnotatedElementsSearch.searchPsiMethods(step, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(m, true)).findAll());
            return methods;
        }
        return new ArrayList<>();
    }


    public static boolean isImplementedStep(SpecStep step, Module module) {
        return findStepImpl(step, module) != null;
    }

    public static boolean isStep(PsiElement element) {
        return element instanceof SpecStepImpl;
    }

    public static boolean isConcept(PsiElement element) {
        return element instanceof ConceptStepImpl;
    }

    public static boolean isMethod(PsiElement element) {
        return element instanceof PsiMethod;
    }

}