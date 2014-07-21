package com.thoughtworks.gauge.util;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.Query;
import com.thoughtworks.gauge.StepValueExtractor;
import com.thoughtworks.gauge.language.psi.SpecStep;

import java.util.Collection;

public class StepUtil {

    private static StepValueExtractor stepValueExtractor = new StepValueExtractor();

    public static PsiMethod findStepImpl(SpecStep step, Project project) {
        Collection<PsiMethod> stepMethods = getStepMethods(project);
        return filter(stepMethods, step);
    }

    private static PsiMethod filter(Collection<PsiMethod> stepMethods, SpecStep step) {
        String stepText = step.getStepValue().getValue();
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
        String annotationValue = AnnotationUtil.getStringAttributeValue(annotation, "value");
        String methodValue = stepValueExtractor.getValue(annotationValue).getValue();
        return methodValue.equals(stepValue);
    }

    public static Collection<PsiMethod> getStepMethods(Project project) {
        final PsiClass step = JavaPsiFacade.getInstance(project).findClass("com.thoughtworks.gauge.Step", GlobalSearchScope.allScope(project));
        final Query<PsiMethod> psiMethods = AnnotatedElementsSearch.searchPsiMethods(step, GlobalSearchScope.allScope(project));
        return psiMethods.findAll();
    }
}
