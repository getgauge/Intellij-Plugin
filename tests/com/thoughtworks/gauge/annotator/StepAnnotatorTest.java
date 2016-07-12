package com.thoughtworks.gauge.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StepAnnotatorTest {

    private AnnotationHelper helper;
    private AnnotationHolder holder;
    private TextRange textRange;

    @Before
    public void setUp() throws Exception {
        holder = mock(AnnotationHolder.class);
        helper = mock(AnnotationHelper.class);
        textRange = mock(TextRange.class);
    }

    @Test
    public void testShouldNotAnnotateNonGaugeElement() throws Exception {
        PsiClass element = mock(PsiClass.class);
        when(helper.isGaugeModule(element)).thenReturn(true);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }

    @Test
    public void testShouldAnnotateBlankSpecStep() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.isEmpty(element)).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Step should not be blank");
    }

    @Test
    public void testShouldAnnotateBlankConceptStep() throws Exception {
        ConceptStepImpl element = mock(ConceptStepImpl.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.isEmpty(any(SpecStepImpl.class))).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);
        when(element.getNode()).thenReturn(mock(ASTNode.class));

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Step should not be blank");
    }

    @Test
    public void testShouldNotAnnotateInNonGaugeModule() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(helper.isGaugeModule(element)).thenReturn(false);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }

    @Test
    public void testShouldAnnotateInGaugeModule() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);
        Module module = mock(Module.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);
        when(helper.getModule(element)).thenReturn(module);
        when(helper.isEmpty(element)).thenReturn(false);
        when(helper.isImplemented(element, module)).thenReturn(false);
        when(holder.createErrorAnnotation(textRange, "Undefined Step")).thenReturn(new Annotation(1, 1, new HighlightSeverity("dsf", 1), "", ""));

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Undefined Step");
    }
}