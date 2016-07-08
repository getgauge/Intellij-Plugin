package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ReferenceSearchTest extends LightCodeInsightFixtureTestCase {

    @Test
    public void testShouldNotFindReferencesOfNonGaugeElement() throws Exception {
        Project project = myFixture.getProject();
        SpecStepImpl element = mock(SpecStepImpl.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        ReferenceSearchHelper mock = mock(ReferenceSearchHelper.class);

        when(mock.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(false);

        new ReferenceSearch(mock).processQuery(searchParameters, psiReference -> false);

        verify(mock, never()).getPsiElements(any(StepCollector.class), any(PsiElement.class));
    }

    @Test
    public void testShouldFindReferencesOfGaugeElement() throws Exception {
        Project project = myFixture.getProject();
        SpecStepImpl element = mock(SpecStepImpl.class);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        ReferenceSearchHelper mock = mock(ReferenceSearchHelper.class);
        StepCollector collector = mock(StepCollector.class);

        when(mock.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(true);
        when(mock.getStepCollector(element)).thenReturn(collector);

        new ReferenceSearch(mock).processQuery(searchParameters, psiReference -> false);

        verify(mock, times(1)).getPsiElements(collector, element);
    }
}