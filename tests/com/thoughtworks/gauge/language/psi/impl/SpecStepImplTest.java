package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.reference.StepReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecStepImplTest {
    @Test
    public void testShouldGetReferenceInGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        SpecStepImpl specStep = new SpecStepImpl(node, helper);
        when(helper.isGaugeModule(specStep)).thenReturn(true);

        PsiReference reference = specStep.getReference();

        assertEquals(reference.getClass(), StepReference.class);
    }

    @Test
    public void testShouldNotGetReferenceInNonGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        SpecStepImpl specStep = new SpecStepImpl(node, helper);
        when(helper.isGaugeModule(specStep)).thenReturn(false);

        PsiReference reference = specStep.getReference();

        assertNull(reference);
    }
}