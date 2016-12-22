package com.thoughtworks.gauge.findUsages;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.thoughtworks.gauge.BeforeStep;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImplUsageProviderTest {
    @Test
    public void TestIsImplicitUsageWithNoModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(mock(PsiElement.class));

        assertFalse(isUsed);
    }

    @Test
    public void TestIsImplicitUsageWithNoElement() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(null);

        assertFalse(isUsed);
    }

    @Test
    public void TestIsImplicitUsageForClass() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        PsiClass c = mock(PsiClassImpl.class);
        PsiMethod method = mock(PsiMethod.class);
        PsiModifierList list = mock(PsiModifierList.class);
        PsiAnnotation annotation = mock(PsiAnnotation.class);
        when(annotation.getQualifiedName()).thenReturn(BeforeStep.class.getCanonicalName());
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{annotation});
        when(method.getModifierList()).thenReturn(list);
        when(c.getMethods()).thenReturn(new PsiMethod[]{method});
        when(helper.isGaugeModule(c)).thenReturn(true);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(c);

        assertTrue(isUsed);
    }

    @Test
    public void TestIsImplicitUsageForClassWithNoMethods() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        PsiClass c = mock(PsiClassImpl.class);
        when(helper.isGaugeModule(c)).thenReturn(true);
        when(c.getMethods()).thenReturn(new PsiMethod[]{});

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(c);

        assertFalse(isUsed);
    }

    @Test
    public void TestIsImplicitUsageForMethodParameter() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        SpecStep element = mock(SpecStepImpl.class);
        PsiParameter parameter = mock(PsiParameterImpl.class);
        when(parameter.getDeclarationScope()).thenReturn(element);
        when(helper.isGaugeModule(parameter)).thenReturn(true);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(parameter);

        assertTrue(isUsed);
    }

    @Test
    public void TestIsImplicitUsageForHook() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        PsiMethod method = mock(PsiMethod.class);

        PsiModifierList list = mock(PsiModifierList.class);
        PsiAnnotation annotation = mock(PsiAnnotation.class);
        when(annotation.getQualifiedName()).thenReturn(BeforeStep.class.getCanonicalName());
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{annotation});
        when(method.getModifierList()).thenReturn(list);
        when(helper.isGaugeModule(method)).thenReturn(true);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(method);

        assertTrue(isUsed);
    }

    @Test
    public void TestIsImplicitUsageForNonGaugeElement() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        PsiMethod method = mock(PsiMethod.class);

        PsiModifierList list = mock(PsiModifierList.class);
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{});
        when(method.getModifierList()).thenReturn(list);
        when(helper.isGaugeModule(method)).thenReturn(true);

        boolean isUsed = new ImplUsageProvider(null, helper).isImplicitUsage(method);

        assertFalse(isUsed);
    }
}