package com.thoughtworks.gauge.findUsages.helper;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.findUsages.StepCollector;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;


public class ReferenceSearchHelperTest extends LightCodeInsightFixtureTestCase {
    @Test
    public void testShouldFindReferencesOfGaugeElement() throws Exception {
        Project project = myFixture.getProject();
        SpecStepImpl element = mock(SpecStepImpl.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(true);

        boolean shouldFindReferences = new ReferenceSearchHelper(moduleHelper).shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertTrue("Should find reference for SpecStep(Expected: true, Actual: false)", shouldFindReferences);
    }

    @Test
    public void testShouldNotFindReferencesOfNonGaugeElement() throws Exception {
        Project project = myFixture.getProject();
        PsiClass element = mock(PsiClass.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(true);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference for PsiClass(Expected: false, Actual: true)", shouldFindReferences);
    }

    @Test
    public void testShouldNotFindReferencesWhenUnknownScope() throws Exception {
        PsiClass element = mock(PsiClass.class);
        SearchScope scope = mock(SearchScope.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn(ReferenceSearchHelper.UNKNOWN_SCOPE);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference When unknown scope(Expected: false, Actual: true)", shouldFindReferences);
    }

    @Test
    public void testShouldFindReferencesWhenNotUnknownScope() throws Exception {
        PsiClass element = mock(PsiClass.class);
        SearchScope scope = mock(SearchScope.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn("Other Scope");

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference When scope is not unknown(Expected: true, Actual: false)", shouldFindReferences);
    }

    @Test
    public void testGetReferenceElements() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);
        SearchScope scope = mock(SearchScope.class);
        StepValue stepValue = new StepValue("hello", "", new ArrayList<>());
        StepCollector collector = mock(StepCollector.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(element.getStepValue()).thenReturn(stepValue);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper();
        referenceSearchHelper.getPsiElements(collector, searchParameters.getElementToSearch());

        verify(collector, times(1)).get("hello");
    }

    @Test
    public void testGetReferenceElementsForConceptStep() throws Exception {
        ConceptStepImpl element = mock(ConceptStepImpl.class);
        SearchScope scope = mock(SearchScope.class);
        StepValue stepValue = new StepValue("# hello", "", new ArrayList<>());
        StepCollector collector = mock(StepCollector.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(element.getStepValue()).thenReturn(stepValue);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper();
        referenceSearchHelper.getPsiElements(collector, searchParameters.getElementToSearch());

        verify(collector, times(1)).get("hello");
    }

    @Test
    public void testShouldNotFindReferencesIfNotGaugeModule() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);
        SearchScope scope = mock(SearchScope.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(false);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference for non Gauge Module(Expected: false, Actual: true)", shouldFindReferences);

        verify(scope, never()).getDisplayName();
    }

    @Test
    public void testShouldFindReferencesIfGaugeModule() throws Exception {
        SpecStepImpl element = mock(SpecStepImpl.class);
        SearchScope scope = mock(SearchScope.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);
        ModuleHelper moduleHelper = mock(ModuleHelper.class);
        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn("Other Scope");

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        verify(scope, times(1)).getDisplayName();
    }

}