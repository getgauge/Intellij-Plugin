package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.thoughtworks.gauge.StepValue;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ParamAnnotatorTest {
    @Test
    public void testShouldNotAnnotateNonGaugeElement() throws Exception {
        PsiClass element = mock(PsiClass.class);
        AnnotationHolder holder = mock(AnnotationHolder.class);
        AnnotationHelper helper = mock(AnnotationHelper.class);

        when(helper.isGaugeModule(element)).thenReturn(true);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }

    @Test
    public void testShouldNotAnnotateInNonGaugeModule() throws Exception {
        PsiMethod element = mock(PsiMethod.class);
        AnnotationHolder holder = mock(AnnotationHolder.class);
        AnnotationHelper helper = mock(AnnotationHelper.class);

        when(helper.isGaugeModule(element)).thenReturn(false);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }

    @Test
    public void testShouldAnnotateWhenParamMismatch() throws Exception {
        PsiMethod element = mock(PsiMethod.class);
        AnnotationHolder holder = mock(AnnotationHolder.class);
        TextRange textRange = mock(TextRange.class);
        AnnotationHelper helper = mock(AnnotationHelper.class);
        PsiParameterList parameterList = mock(PsiParameterList.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(Collections.singletonList(new StepValue("step", "step", Collections.singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(2);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Parameter count mismatch(found [2] expected [1]) with step annotation : \"step\". ");
    }


    @Test
    public void testShouldAnnotateWhenParamMismatchInAlias() throws Exception {
        PsiMethod element = mock(PsiMethod.class);
        AnnotationHolder holder = mock(AnnotationHolder.class);
        TextRange textRange = mock(TextRange.class);
        AnnotationHelper helper = mock(AnnotationHelper.class);
        PsiParameterList parameterList = mock(PsiParameterList.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(Arrays.asList(new StepValue("step", "step", Arrays.asList("a", "b")), new StepValue("step1", "step1", Collections.singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(2);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Parameter count mismatch(found [2] expected [1]) with step annotation : \"step1\". ");
    }

    @Test
    public void testShouldNotAnnotateWhenThereIsNoParamMismatch() throws Exception {
        PsiMethod element = mock(PsiMethod.class);
        AnnotationHolder holder = mock(AnnotationHolder.class);
        TextRange textRange = mock(TextRange.class);
        AnnotationHelper helper = mock(AnnotationHelper.class);
        PsiParameterList parameterList = mock(PsiParameterList.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(Collections.singletonList(new StepValue("step", "step", Collections.singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(1);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }
}