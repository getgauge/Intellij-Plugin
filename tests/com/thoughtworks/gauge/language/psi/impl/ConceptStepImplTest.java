package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.reference.ConceptReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConceptStepImplTest {
    @Test
    public void testShouldGetReferenceInGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        ConceptStepImpl conceptStep = new ConceptStepImpl(node, helper);
        when(helper.isGaugeModule(conceptStep)).thenReturn(true);

        PsiReference reference = conceptStep.getReference();

        assertEquals(reference.getClass(), ConceptReference.class);
    }

    @Test
    public void testShouldNotGetReferenceInNonGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        ConceptStepImpl conceptStep = new ConceptStepImpl(node, helper);
        when(helper.isGaugeModule(conceptStep)).thenReturn(false);

        PsiReference reference = conceptStep.getReference();

        assertNull(reference);
    }
}