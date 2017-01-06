package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecInspectionProviderTest {
    @Test
    public void testGetElementReturnsStep() throws Exception {
        SpecStep step = mock(SpecStep.class);

        PsiElement element = new SpecInspectionProvider().getElement(step);

        assertEquals(step, element);
    }

    @Test
    public void testGetElementReturnsNullIfElementNotPresent() throws Exception {
        PsiElement element = new SpecInspectionProvider().getElement(null);

        assertEquals(null, element);
    }

    @Test
    public void testGetElementReturnsScenarioHeading() throws Exception {
        PsiElement e = mock(PsiElement.class);
        LeafPsiElement leafPsiElement = mock(LeafPsiElement.class);

        when(leafPsiElement.getElementType()).thenReturn(SpecTokenTypes.SCENARIO_HEADING);
        when(e.getParent()).thenReturn(leafPsiElement);

        PsiElement element = new SpecInspectionProvider().getElement(e);

        assertEquals(leafPsiElement, element);
    }

    @Test
    public void testGetElementReturnsSpecHeading() throws Exception {
        PsiElement e = mock(PsiElement.class);
        LeafPsiElement leafPsiElement = mock(LeafPsiElement.class);

        when(leafPsiElement.getElementType()).thenReturn(SpecTokenTypes.SPEC_HEADING);
        when(e.getParent()).thenReturn(leafPsiElement);

        PsiElement element = new SpecInspectionProvider().getElement(e);

        assertEquals(leafPsiElement, element);
    }
}