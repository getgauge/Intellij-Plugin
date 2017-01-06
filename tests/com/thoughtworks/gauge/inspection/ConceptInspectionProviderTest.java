package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.ConceptConceptHeading;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConceptInspectionProviderTest {
    @Test
    public void testGetElementReturnsStep() throws Exception {
        ConceptStep step = mock(ConceptStep.class);

        PsiElement element = new ConceptInspectionProvider().getElement(step);

        assertEquals(step, element);
    }

    @Test
    public void testGetElementReturnsNullIfElementNotPresent() throws Exception {
        PsiElement element = new ConceptInspectionProvider().getElement(null);

        assertEquals(null, element);
    }

    @Test
    public void testGetElementReturnsConceptHeading() throws Exception {
        PsiElement e = mock(ConceptConceptHeading.class);

        when(e.getParent()).thenReturn(e);

        PsiElement element = new ConceptInspectionProvider().getElement(e);

        assertEquals(e, element);
    }
}